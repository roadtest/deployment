from fabric.api import env,run,task,roles,parallel,settings,hide,cd
from fabric.state import output
from fabric import colors
from fabric.tasks import execute
from fabric.operations import local
from fabric.utils import puts
from fabric_fixes.monkeyPatch import *
import os

env.reject_unkown_hosts = False
env.disable_known_hosts = True
output['aborts'] = False
NodePkg=['sc-nexus-cluster.ear','sc-nexus-api.war']
MasterPkg='sc-nexus-ext.war'
ChannelPkg='sc-nexus-channel.jar'
env.forward_agent = True
####
'''
Usage:
   fab -l	<=list all tasks
   fab TargetEnv:iot-app-staging Deploy	<=assign deployment destination to iot-app-staging, run Deploy task
    to run channel file update, also use iot-app role since they are master node with all deployment files
'''
####
env.roledefs = {
	'iot-app-staging': { 'hosts': ['ec2-user@papp1', 'ec2-user@papp2'],
				     'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem',
					 'Envs': 'staging'
	 },
	'iot-comm-staging': { 'hosts': ['pcomm1', 'pcomm2'],
                     'HostIP': ['10.196.104.28','10.196.104.132'],
				     'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem',
					 'Envs': 'staging'
	 },
	'iot-pgp-staging': { 'hosts': ['ec2-user@ppgp1','ec2-user@ppgp2'],
				     'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem',
					 'Envs': 'staging'
	 },
	'iot-app-prod': { 'hosts': ['ec2-user@app1', 'ec2-user@app2', 'ec2-user@app3','ec2-user@comm1',
							'ec2-user@comm2', 'ec2-user@comm3'],
				  'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem',
				  'Envs': 'prod'
	 },
	'iot-comm-prod': { 'hosts': ['ec2-user@comm1', 'ec2-user@comm2', 'ec2-user@comm3'],
				  'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem',
				  'Envs': 'prod'
	 },
	'iot-pgp-prod': { 'hosts': ['ec2-user@pgp1', 'ec2-user@pgp2'],
				  'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem',
				  'Envs': 'prod'
	 },
	'localone': { 'hosts': ['cma@localhost:2222'],
				  'SSHKEY': '/home/cma/.ssh/id_rsa'
	 }
}

#to replace the default abort System.Exit action
class HostChecking(Exception):
	pass

def CopyChannelJar():
    local("ssh-add " + env.key_filename)
    for Host in env.roledefs['iot-comm-' + Envs]['HostIP']:
        run("scp " + "/tmp/" + ChannelPkg + " " + Host + ":/tmp/")
#after copy jar from app1 to comm1 and comm2, you need to stop channel, replace old file, start channel
	execute(RenameChannelJar, hosts = env.roledefs['iot-comm-' + Envs]['hosts'])

def RenameChannelJar():
    BaseName = run('basename ' + ChannelPkg + ' .jar')
    ExistingFile = sudo('ls /opt/SeeControl/channel/' + BaseName + '*jar', user='autodesk')
    print("old file name is - " + ExistingFile)
    with settings(warn_only=True):
        sudo("/opt/SeeControl/bin/stop_channels.sh", user='autodesk')
    sudo('rm ' + ExistingFile)
    sudo('cp /tmp/' + ChannelPkg + ' ' + ExistingFile )
    sudo("/opt/SeeControl/bin/start-channels-cluster.sh", user='autodesk',pty=False)

def HostDirCheck():
    with settings(abort_exception = HostChecking):
        try:
            run("ls -ld /opt/SeeControl/sc-cluster",shell=True)
            return True
        except HostChecking:
            pass
            return False

def RoleCheck():
    with hide('everything'), settings(abort_exception = HostChecking):
        try:
            run("netstat -an | awk '{print $4}' | grep 4849", shell=True)
            return True
        except HostChecking:
            pass
            return False

def RestartChannel():
	with settings(warn_only=True):
		sudo("/opt/SeeControl/bin/stop_channels.sh", user='autodesk')
		sudo("/opt/SeeControl/bin/start-channels-cluster.sh", user='autodesk',pty=False)

@task
def RestartComm():
		puts("Restart process on comm hosts")
		execute(RestartChannel, hosts = env.roledefs['iot-comm-' + Envs]['hosts'])

def  AppList():
    Apps = sudo("/opt/SeeControl/sc-cluster/bin/asadmin --host localhost --port 4849 --user admin --passwordfile /home/autodesk/.scpass  list-applications sc-cluster | grep -v Command | awk '{print $1}'", user='autodesk' )
    MasterApps = sudo("/opt/SeeControl/sc-cluster/bin/asadmin --host localhost --port 4849 --user admin --passwordfile /home/autodesk/.scpass  list-applications | grep -v Command |  awk '{print $1}'", user='autodesk' )
    return Apps, MasterApps

@task
def TargetEnv(RoleName):
	env.user = "ec2-user"
	env.key_filename = env.roledefs[RoleName]['SSHKEY']
	env.hosts = env.roledefs[RoleName]['hosts']
	global Envs
	Envs = env.roledefs[RoleName]['Envs']

@task
def UnDeploy():
    if RoleCheck():
        puts("Enter master node")
        App,ClusterApp=AppList()
        if  App:
            for pkgs in App.split():
                puts("To undeploy app - " + pkgs)
                with settings(warn_only=True):
                    sudo("/opt/SeeControl/sc-cluster/bin/asadmin --host localhost --port 4849 --user admin --passwordfile /home/autodesk/.scpass undeploy --target sc-cluster " + pkgs, user='autodesk' )
        if  ClusterApp:
                puts("To undeploy Cluster app - " + ClusterApp)
                with settings(warn_only=True):
                    sudo("/opt/SeeControl/sc-cluster/bin/asadmin --host localhost --port 4849 --user admin --passwordfile /home/autodesk/.scpass undeploy --target server " + ClusterApp, user='autodesk' )
        sudo("cd /home/autodesk/sc-dist; ant glassfish-stop", user='autodesk')
        sudo("rm -rf /opt/SeeControl/sc-cluster/glassfish/domains/seecontrol/generated/*")
    else:
        print("Enter agent node")
        sudo("rm -rf /opt/SeeControl/node-sc-cluster/glassfish/nodes/app*/sc-instance*/generated/*")

@task
def DeployComm():
    if RoleCheck() or HostDirCheck():
        puts("Prepare copy channel jar files to comm servers - ")
        sudo("cp /home/autodesk/releases/sc-nexus-channel.jar /tmp; chmod 755 /tmp/sc-nexus-channel.jar", user='autodesk')
        CopyChannelJar()
        sudo("rm /tmp/sc-nexus-channel.jar")

@task
def Deploy():
	if RoleCheck() or HostDirCheck():
		puts("Start deployment from master node - ")
		sudo("cd /home/autodesk/sc-dist; ant glassfish-start", user='autodesk',pty=False)
        with cd('/home/autodesk/releases'):
            puts("Enter deployment directory - ")
            for pkgs in NodePkg:
                sudo("/opt/SeeControl/sc-cluster/bin/asadmin --host localhost --port 4849 --user admin --passwordfile /home/autodesk/.scpass deploy --target sc-cluster " + pkgs, user='autodesk')
            sudo("/opt/SeeControl/sc-cluster/bin/asadmin --host localhost --port 4849 --user admin --passwordfile /home/autodesk/.scpass deploy --target server " + MasterPkg, user='autodesk')
        RestartComm()

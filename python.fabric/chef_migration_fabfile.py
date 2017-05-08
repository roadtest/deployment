from fabric.api import env,run,task,roles,parallel
from fabric import colors
from fabric.tasks import execute
from fabric.operations import local
from fabric.utils import puts
from cuisine import *
from fabric_fixes.monkeyPatch import *
import datetime as dt

env.reject_unkown_hosts = False
env.disable_known_hosts = True
####
SourceFile="/mnt/fusion_connect-validator.pem"
#SourceFile="/mnt/flc-microservices-validator.pem"
DestFile = "/etc/chef/validation.pem"
ConfigFile = "/etc/chef/client.rb"
OldKey = "chef.a360.autodesk.com"
NewKey = "chef_server_url 'https://chef.pr.adskengineer.net/organizations/fusion_connect'"
#NewKey = "chef_server_url          'https://chef.pr.adskengineer.net/organizations/flc-microservices'"
#OldValidation = "seecontrol-validator"
#NewValidation = "validation_client_name 'fusion_connect-validator'"
OldValidation = "seecontrol-validator"
NewValidation = "validation_client_name 'fusion_connect-validator'"
####
env.roledefs = {
	'iot-staging': { 'hosts': ['ec2-user@pweb1', 'ec2-user@pweb2','ec2-user@papp1', 'ec2-user@papp2','ec2-user@pcomm1',
							'ec2-user@pcomm2','ec2-user@pdb1','ec2-user@pdb2','ec2-user@ppgp1','ec2-user@ppgp2', 'ec2-user@ppmq1','ec2-user@ppmq2'],
				     'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem'
	 },
	'iot-prod': { 'hosts': ['ec2-user@web1', 'ec2-user@web2','ec2-user@web3','ec2-user@app1', 'ec2-user@app2', 'ec2-user@app3','ec2-user@comm1',
							'ec2-user@comm2', 'ec2-user@comm3','ec2-user@db1','ec2-user@db2', 'ec2-user@db3','ec2-user@pgp1','ec2-user@pgp2', 'ec2-user@mq2','ec2-user@mq3'],
				  'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem'
	 },
	'fdm-staging': { 'hosts': [ 'ec2-user@fppapp2','ec2-user@fppdb1','ec2-user@fppdb2', 'ec2-user@fppdb3' ],
				  'SSHKEY': '/home/cma/unix_home/plm360use1-FusionDM-prod.pem'
	 },
	'fdm-prod': { 'hosts': [ 'ec2-user@fapp1', 'ec2-user@fapp2','ec2-user@fdb1','ec2-user@fdb2', 'ec2-user@fdb3' ],
				  'SSHKEY': '/home/cma/unix_home/plm360use1-FusionDM-prod.pem'
	 },
	'localone': { 'hosts': ['cma@localhost:2222'],
				  'SSHKEY': '/home/cma/.ssh/id_rsa'
	 },
	 'remoteone': { 'hosts': [ 'ec2-user@mq1'],
				  	'SSHKEY': '/home/cma/unix_home/plm360use1-iot-prod.pem'
	 }
}

def update_config( text ):
	res = []
	for line in text.split("\n"):
		if OldKey in line:
			res.append(NewKey)
		elif  OldValidation in line:
			res.append(NewValidation)
		else:
			res.append(line)
	return "\n".join(res)

@task
@parallel
def TestHost():
	run("hostname")

@task
def TargetEnv(RoleName):
	env.user = "ec2-user"
	env.key_filename = env.roledefs[RoleName]['SSHKEY']
	env.hosts = env.roledefs[RoleName]['hosts']

# run fab TargetEnv:localtwo ChefMigration
@task
def ChefMigration():
	puts("start Chef change task")
	sudo('cp -pr /etc/chef /home/ec2-user/chef.%s' % (dt.datetime.now().strftime("%Y%m%d%H")))
	sudo('knife node show $(hostname) -Fj -c /etc/chef/client.rb -Fj > /tmp/node.json')
	sudo("systemctl stop chef-client; rm /etc/chef/client.pem /etc/chef/validation.pem; chmod 755 /etc/chef /etc/chef/client.rb")
	put(SourceFile, DestFile, user='root', group='root', use_sudo=True, mode=0700 )
	file_update("/etc/chef/client.rb", update_config, sudo=True)
	sudo("chmod 700 /etc/chef /etc/chef/client.rb; systemctl start chef-client;sleep 2; chef-client")
	sudo('knife node from file /tmp/node.json -c /etc/chef/client.rb')

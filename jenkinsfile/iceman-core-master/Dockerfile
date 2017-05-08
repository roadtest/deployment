#
#   ctr-pod and java 8
#
FROM quay.io/autodeskcloud/ctr-lib-java

#
#   copy jar to root directory
#
ADD build/libs/iceman-core-0.0.1-SNAPSHOT.jar /iceman.jar

#
# - add the build tag
# - add the pod script + supervisor configuration
# - start supervisord (which will in turn start our ochopod script)
#
ADD build/BUILD /opt/pod/BUILD
ADD resources/pod /opt/pod
ADD resources/supervisor /etc/supervisor/conf.d
CMD /usr/bin/supervisord -n -c /etc/supervisor/supervisord.conf

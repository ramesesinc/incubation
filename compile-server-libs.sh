#!/bin/bash
NETBEANS_HOME=/c/TEMP/netbeans72-projects-treasury
ANT_HOME="$NETBEANS_HOME/java/ant"
REPO_HOME=`pwd`
echo ""
echo "NETBEANS_HOME: $NETBEANS_HOME"
echo "ANT_HOME     : $ANT_HOME"
echo "JAVA_HOME    : $JAVA_HOME"
echo "REPO_HOME    : $REPO_HOME"
cd $ANT_HOME/bin
chmod +x ant
./ant -version
echo ""
./ant -f $REPO_HOME/rameses-common2 clean jar
./ant -f $REPO_HOME/rameses-custom-impl clean jar
./ant -f $REPO_HOME/anubis-cms2 clean jar
./ant -f $REPO_HOME/rameses-osiris3-core2 clean jar
./ant -f $REPO_HOME/rameses-osiris3-mail clean jar
./ant -f $REPO_HOME/rameses-osiris3-rules clean jar
./ant -f $REPO_HOME/db-dialect clean jar
./ant -f $REPO_HOME/rameses-osiris3-server clean jar
./ant -f $REPO_HOME/rameses-rabbitmq-connection clean jar
./ant -f $REPO_HOME/rameses-redis-cache clean jar
./ant -f $REPO_HOME/rameses-services-extended clean jar
./ant -f $REPO_HOME/rameses-system-services clean jar
./ant -f $REPO_HOME/rameses-seti-services clean jar
./ant -f $REPO_HOME/rameses-tomcat-cp clean jar
./ant -f $REPO_HOME/rameses-websocket2 clean jar
./ant -f $REPO_HOME/rameses-webapi-common clean jar

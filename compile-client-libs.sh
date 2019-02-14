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
./ant -f $REPO_HOME/rameses-client-ui clean jar
./ant -f $REPO_HOME/osiris2-framework clean jar
./ant -f $REPO_HOME/osiris2-client-ui clean jar
./ant -f $REPO_HOME/osiris2-report clean jar
./ant -f $REPO_HOME/osiris2-draw clean jar
./ant -f $REPO_HOME/rameses-client-ui-bio clean jar
./ant -f $REPO_HOME/rameses-client-ui-support clean jar
./ant -f $REPO_HOME/rameses-jdbc16 clean jar
./ant -f $REPO_HOME/osiris2-client-themes clean jar
./ant -f $REPO_HOME/rameses-seti clean jar
./ant -f $REPO_HOME/rameses-seti-support clean jar
./ant -f $REPO_HOME/client-ui-extended clean jar
./ant -f $REPO_HOME/client-system clean jar
./ant -f $REPO_HOME/rameses-client-menu clean jar
./ant -f $REPO_HOME/rameses-client-report clean jar
./ant -f $REPO_HOME/rameses-rules-mgmt clean jar
./ant -f $REPO_HOME/rameses-workflow-mgmt clean jar
./ant -f $REPO_HOME/rameses-client-notification2 clean jar

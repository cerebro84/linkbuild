#!/bin/bash

CLASSPATH=avetanaBluetooth.jar:linkbuildControlTool.jar:linkbuildServer.jar:linkbuildUtility.jar:$CLASSPATH

case $1 in
	-tool)java -classpath $CLASSPATH com.jsoft.linkbuild.controlTool.StartControlTool
esac

java -classpath $CLASSPATH com.jsoft.linkbuild.listenerAndServerLibrary.StartListener

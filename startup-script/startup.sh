#!/bin/sh

# The class with public static main.
MAIN="ca.appspace.netprobe.Main"
JAVACMD="java"
CLASSPATH=${CLASSPATH}:network-probe-1.0.jar:/libs/*
# JAVA_MEM_OPTS="-Xms128m -Xmx128mm"

echo "JAVA=$JAVACMD"
echo "CLASSPATH=$CLASSPATH"
echo ""

exec "${JAVACMD}" -classpath ${CLASSPATH} ${MAIN} "$@"
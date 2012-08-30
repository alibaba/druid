#!/bin/sh

if [ -z "$JAVA_HOME" ] ; then
  echo "Error: JAVA_HOME is not defined."
  exit 1
fi

"$JAVA_HOME/bin/java" -cp "./druid-0.2.6.jar:$JAVA_HOME/lib/tools.jar" com.alibaba.druid.support.console.DruidStat  $@

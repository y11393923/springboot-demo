#!/bin/sh
CONF_DIR=/usr/local/example-spring-boot-package/config
LIB_DIR=/usr/local/example-spring-boot-package/lib
BINMAIN=com.zyy.PackageApplication
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
JAVA_OPTS=" -Djava.net.preferIPv4Stack=true -Dfile.encoding=utf-8"
JAVA_MEM_OPTS=" -server -Xms2g -Xmx2g -XX:SurvivorRatio=2 -XX:+UseParallelGC "
java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS $BINMAIN

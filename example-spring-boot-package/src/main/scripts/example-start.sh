 #!/bin/bash

BIN_FILE=example-spring-boot-package
MAIN_CLASS_NAME=PackageApplication
MAIN_CLASS=com.zyy.PackageApplication

BIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd $BIN_DIR
MONITOR_LOG="$BIN_DIR/monitor/monitor.log"
MONITOR_PIDFILE="$BIN_DIR/monitor/monitor.pid"
MONITOR_PID=0
if [[ -f $MONITOR_PIDFILE ]]; then
  MONITOR_PID=`cat $MONITOR_PIDFILE`
fi
PIDFILE="$BIN_DIR/$(basename $BIN_FILE).pid"
PID=0
if [[ -f $PIDFILE ]]; then
  PID=`cat $PIDFILE`
fi

CONF_DIR=./config
LIB_DIR=./lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
JAVA_OPTS=" -Djava.net.preferIPv4Stack=true -Dfile.encoding=utf-8 -Dlogging.config=$CONF_DIR/logback-spring.xml"
JAVA_MEM_OPTS=" -server -Xms2g -Xmx2g -XX:PermSize=1g -XX:SurvivorRatio=2 -XX:+UseParallelGC "

#START_CMD=$BIN_DIR/$BIN_FILE
START_CMD="java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS $MAIN_CLASS"
STOP_CMD="kill $PID"

running() {
  if [[ -z $1 || $1 == 0 ]]; then
    return 1
  fi
  if [[ ! -d /proc/$1 ]]; then
      return 1
  fi
}

start_app() {
  echo "### starting $BIN_FILE `date '+%Y-%m-%d %H:%M:%S'` ###" >>  /dev/null   2>&1 &
  nohup java $JAVA_OPTS $JAVA_MEM_OPTS -classpath $CONF_DIR:$LIB_JARS $MAIN_CLASS >> /dev/null 2>&1 &
  if ! $(running $!) ; then
    echo "failed to start $BIN_FILE"
    exit 1
  fi
  PID=$!
  echo $! > $PIDFILE
  echo "new pid $MAIN_CLASS_NAME-$!"
}

stop_app() {
  if ! $(running $PID) ; then
    return
  fi
  echo "stopping $PID of $BIN_FILE ..."
  $STOP_CMD
  while $(running $PID) ; do
    sleep 1
  done
}


start_monitor() {
  if ! $(running $PID) ; then
    echo "$(date '+%Y-%m-%d %T') $BIN_FILE is gone. monitor pid $!" >> $MONITOR_LOG
    start_app
    echo "$(date '+%Y-%m-%d %T') $BIN_FILE started. monitor pid $!" >> $MONITOR_LOG
  fi
  MONITOR_PID=$!
  echo "monitor pid $MAIN_CLASS_NAME-$!"
  echo $! > $MONITOR_PIDFILE
}


stop_monitor() {
  if ! $(running $MONITOR_PID) ; then
    return
  fi
  echo "stopping $MONITOR_PID of $BIN_FILE monitor ..."
  kill $MONITOR_PID
  while $(running $MONITOR_PID) ; do
    sleep 1
  done
}

start() {
  if ! [[ -e "$BIN_DIR/monitor" ]]; then
     mkdir "$BIN_DIR/monitor"
  fi
  start_app
  start_monitor
}

stop() {
  stop_monitor
  stop_app
}

restart() {
  stop
  start
}

restart
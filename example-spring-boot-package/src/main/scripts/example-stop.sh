#!/bin/bash

BIN_FILE=example-spring-boot-package
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

STOP_CMD="kill $PID"
MONITOR_INTERVAL=5

running() {
  if [[ -z $1 || $1 == 0 ]]; then
    return 1
  fi
  if [[ ! -d /proc/$1 ]]; then
      return 1
  fi
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

stop() {
  #stop_monitor
  stop_app
}

stop

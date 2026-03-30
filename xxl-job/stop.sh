#!/bin/sh
PID=$(ps -ef | grep xxl-job-admin-3.3.2.jar | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
  kill -9 $PID
  echo "XXL-JOB 已关闭"
else
  echo "XXL-JOB 未运行"
fi
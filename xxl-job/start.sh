#!/bin/sh
nohup java -jar xxl-job-admin-3.3.2.jar --spring.config.location=./application.properties > xxl-job.log 2>&1 &
echo "XXL-JOB 已启动"
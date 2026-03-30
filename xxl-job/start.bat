@echo off
chcp 65001
echo ==============================
echo       XXL-JOB 启动中
echo ==============================
D:
cd "D:\mycode\xxl-job-deploy"
java -jar xxl-job-admin-3.3.2.jar --spring.config.location=application.properties
pause
@echo off
chcp 65001
echo ==============================
echo       XXL-JOB 关闭中
echo ==============================
taskkill /f /fi "imagename eq java.exe" /fi "windowtitle eq xxl-job-admin*"
echo 已关闭
pause
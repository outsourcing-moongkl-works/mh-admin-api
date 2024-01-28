#!/bin/bash

CURRENT_TIME=$(date '+%Y-%m-%d %H:%M:%S %Z')
echo ">>> 현재 서버 시간: $CURRENT_TIME" >> /home/ubuntu/app/deploy.log

BUILD_JAR=$(ls /home/ubuntu/app/build/libs/mh-admin-api-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo ">>> build 파일명: $JAR_NAME" >> /home/ubuntu/app/deploy.log

echo ">>> build 파일 복사" >> /home/ubuntu/app/deploy.log
DEPLOY_PATH=/home/ubuntu/app/
cp $BUILD_JAR $DEPLOY_PATH

echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> /home/ubuntu/app/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/app/deploy.log
else
  echo ">>> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포 시각: $(date '+%H:%M:%S')" >> /home/ubuntu/app/deploy.log

export $(grep -v '^#' /home/ubuntu/.env | xargs -d '\n' -n 1)
# Java 어플리케이션 실행
CURRENT_TIME=$(date '+%Y%m%d_%H시%M분%S초')
nohup java -jar $DEPLOY_JAR >> "/home/ubuntu/app/logs/deploy/$CURRENT_TIME.log" 2>>"/home/ubuntu/app/logs/deploy_err/$CURRENT_TIME.log" &
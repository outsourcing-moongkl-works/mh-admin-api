#!/bin/bash

CURRENT_TIME=$(date '+%Y-%m-%d %H:%M:%S %Z')
echo ">>> 현재 서버 시간: $CURRENT_TIME" >> /home/ec2-user/logs/deploy_sh.log

BUILD_JAR=$(ls /home/ec2-user/app/build/libs/mh-admin-api-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo ">>> build 파일명: $JAR_NAME" >> /home/ec2-user/logs/deploy_sh.log

echo ">>> build 파일 복사" >> /home/ec2-user/logs/deploy_sh.log
DEPLOY_PATH=/home/ec2-user/app/
cp $BUILD_JAR $DEPLOY_PATH

echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> /home/ec2-user/logs/deploy_sh.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ec2-user/logs/deploy_sh.log
else
  echo ">>> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 30
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포 시각: $(date '+%H:%M:%S')" >> /home/ec2-user/logs/deploy_sh.log

export $(grep -v '^#' /home/ec2-user/.env | xargs -d '\n' -n 1)
# Java 어플리케이션 실행
CURRENT_TIME=$(date '+%Y%m%d_%H:%M:%S')
nohup java -jar $DEPLOY_JAR >> "/home/ec2-user/logs/deploy/$CURRENT_TIME.log" 2>>"/home/ec2-user/logs/deploy_err/$CURRENT_TIME.log" &
#!/bin/bash

PWD=`pwd`
DIRS="middleware nginx app"
DOCKER_PWD=${PWD}/docker

./gradlew clean bootJar

cd ${PWD}

docker -v
[ "$?" != "0" ] && echo "install docker first, see https://docs.docker.com/engine/install/" && exit 1
docker-compse -v
[ "$?" != "0" ] && echo "install docker-compose first, see https://docs.docker.com/compose/install/" && exit 1
DOCKER_PID=$(ps -ef|grep "/usr/bin/dockerd" |grep -v "grep" | awk '{print $2}')
[ "${DOCKER_PID}" == "" ] && echo "start docker first, like 'systemctl start docker'" && exit 1

# create network
DOCKER_NETWORK=$(docker network ls |grep rss_net)
[ "${DOCKER_NETWORK}" == "" ] && docker network create rss_net

for dir in $DIRS ; do
  echo "${dir} starting"
  cd ${DOCKER_PWD}/${dir}
  docker-compose up -d
  sleep 3
  cd ${DOCKER_PWD}
done

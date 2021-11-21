#!/bin/bash
CLEAN=YES
[ "$1" != "" ] && CLEAN=$1
docker-compose down
docker rmi aaronysj/rss:@image_tag@
[ "${CLEAN}" == "NO" ] && ./gradlew clean bootJar
docker-compose up -d

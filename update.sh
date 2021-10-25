#!/bin/bash

docker-compose down
docker rmi aaronysj/rss
./gradlew clean bootJar
docker-compose up -d

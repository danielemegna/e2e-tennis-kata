#!/bin/sh

set -e

#docker run --rm -it -v $PWD:/app -w /app openjdk:18-slim ./gradlew installDist
./gradlew installDist

rsync --progress --stats --update --recursive \
  ./build/install/EndToEndTennisKata/* \
  root@51.15.92.225:/root/e2e-tennis-kata/build/install/EndToEndTennisKata/

ssh -t root@51.15.92.225 -- docker stop tennis || true
ssh -t root@51.15.92.225 -- docker run --rm -d \
	-p 8088:8080 \
	-v /root/e2e-tennis-kata/build/install/EndToEndTennisKata/:/app \
	-w /app \
	--name tennis \
	openjdk:18-slim \
	./bin/EndToEndTennisKata

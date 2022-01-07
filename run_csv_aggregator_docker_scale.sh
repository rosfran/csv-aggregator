#!/bin/sh

docker-compose --file docker-compose-scale.yml up -d --build --scale app=5

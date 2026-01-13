#!/bin/bash

#######################################################
# 로컬에서 Spring Boot 를 Docker 로 기동하기 위한 스크립트 #
#######################################################

set -e

IMAGE_NAME=baekgwa-server
TAG=local
CONTAINER_NAME=baekgwa-server
NETWORK=baekgwa-network
ENV_FILE=.env.local

echo ">>> Docker image build"
docker build -t ${IMAGE_NAME}:${TAG} ../..

echo ">>> Remove existing image (if exists)"
docker rmi ${IMAGE_NAME}:${TAG} 2>/dev/null || true

echo ">>> Stop & remove existing container (if exists)"
docker rm -f ${CONTAINER_NAME} 2>/dev/null || true

echo ">>> Run container"
docker run -d \
  --name ${CONTAINER_NAME} \
  --network ${NETWORK} \
  -p 8080:8080 \
  --env-file ${ENV_FILE} \
  ${IMAGE_NAME}:${TAG}

echo ">>> Done"

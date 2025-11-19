#!/bin/bash

# 환경변수로 받은 KIBANA_USER 계정 생성 (이미 존재하면 건너뜀)
if ! bin/elasticsearch-users list | grep -q "^${KIBANA_USERNAME} "; then
  echo "Init Script: Creating Kibana user: ${KIBANA_USERNAME}"
  bin/elasticsearch-users useradd ${KIBANA_USERNAME} -p ${KIBANA_PASSWORD} -r kibana_system
else
  echo "Init Script: Kibana user ${KIBANA_USERNAME} already exists."
fi
#!/usr/bin/env bash
set -e

rm -f follow-service-chart-*

./build.sh

if [[ $1 == "--delete" ]]; then
  helm delete follow-service
fi

helm upgrade --install --create-namespace follow-service follow-service-chart-*

set +e
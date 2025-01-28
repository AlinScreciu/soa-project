#!/usr/bin/env bash
set -e

rm -f notifications-service-chart-*

./build.sh

if [[ $1 == "--delete" ]]; then
  helm delete notifications-service
fi

helm upgrade --install --create-namespace notifications-service notifications-service-chart-*

set +e
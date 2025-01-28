#!/usr/bin/env bash
set -e

rm -f identity-service-chart-*

./build.sh

if [[ $1 == "--delete" ]]; then
  helm delete identity-service
fi

helm upgrade --install --create-namespace identity-service identity-service-chart-*

set +e
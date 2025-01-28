#!/usr/bin/env bash
set -e

rm -f post-service-chart-*

./build.sh

if [[ $1 == "--delete" ]]; then
  helm delete post-service
fi

helm upgrade --install --create-namespace post-service post-service-chart-*

set +e
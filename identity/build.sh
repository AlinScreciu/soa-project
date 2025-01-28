#!/usr/bin/env bash
set -e

# shellcheck disable=SC2155
export JAVA_HOME=$(/usr/libexec/java_home -v 23)
mvn --version
# 1) Build your project JAR (assuming Maven)
mvn clean package -DskipTests

# 2) Build Docker image
# Replace <YOUR_DOCKER_REGISTRY> with your registry (e.g., "my-registry.io/my-project")
docker build -t identity-service:latest -f ./src/main/docker/Dockerfile .

# 4) Package Helm chart (optional step if you want a .tgz)
helm package src/main/helm --destination .

set +e
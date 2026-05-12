#!/bin/sh
set -eu

JAVA_OPTS=${JAVA_OPTS:-""}
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}

exec java ${JAVA_OPTS} -jar /app/app.jar \
  --spring.profiles.active=${SPRING_PROFILES_ACTIVE}

#!/usr/bin/env bash
set -ex

name="android-test"
dockerfilePath="./.ci/androidWithEmulator/DOCKERFILE"

docker build -f ${dockerfilePath} -t ${name} .

docker run --rm $name

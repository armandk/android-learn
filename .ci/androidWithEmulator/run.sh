#!/bin/bash

function wait_for_emulator() {
  boot_completed=false
  while [ "$boot_completed" == false ]; do
    status=$(adb devices | grep '^emulator' | tr -d '\r')

    echo "Boot Status: $status"
    if [[ -z "$status" || "$status" == *"offline" ]]; then
      sleep 1
    else
      boot_completed=true
    fi
  done
}

emulator -avd "${EMULATOR_NAME_ARM}" -no-boot-anim -no-window -gpu off -no-audio -accel auto -wipe-data -no-snapshot &

wait_for_emulator

adb devices

./gradlew bundle
./gradlew connectedAndroidTest --stacktrace
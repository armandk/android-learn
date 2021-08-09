#!/bin/bash
android_emulator_package="system-images;android-25;google_apis;armeabi-v7a"
emulator_name="android_arm"

sdkmanager --list
sdkmanager "${android_emulator_package}"

echo "no" | avdmanager --verbose create avd --force --name "${emulator_name}" --device "pixel" --package "${android_emulator_package}"
emulator -list-avds

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

emulator -avd "${emulator_name}" -no-boot-anim -no-window -gpu off -no-audio -accel auto -wipe-data -no-snapshot &

wait_for_emulator

./gradlew connectedAndroidTest --stacktrace
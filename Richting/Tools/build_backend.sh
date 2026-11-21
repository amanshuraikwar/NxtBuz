#!/bin/sh
set -eu
case "$PLATFORM_NAME" in
    iphoneos) target=IosArm64 ;;
    iphonesimulator) target=IosSimulatorArm64 ;;
    *) echo "Unsupported Richting platform: $PLATFORM_NAME" >&2; exit 1 ;;
esac
cd "$SRCROOT/.."
./gradlew ":richtingTrains:link${CONFIGURATION}Framework${target}"

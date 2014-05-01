#!/bin/bash

if [[ ! -e platforms/android ]]; then
  echo "Use this script to build a signed ChromeADT.apk"
  echo
  echo "Usage:"
  echo "  Run it with your CWD set to the root of the project"
  exit 1
fi

KEYSTORE_PATH="$(dirname $0)/CCAHarness-debug.keystore"
AAPT_PATH=$(which aapt)

if [[ -z "$AAPT_PATH" ]]; then
  ANDROID_PATH=$(which android)
  if [[ -z "$ANDROID_PATH" ]]; then
    echo "aapt not found (nor android tool)"
    exit 1
  fi
  BUILD_TOOLS=${ANDROID_PATH%/tools*}/build-tools
  AAPT_PATH=$(find "$BUILD_TOOLS" -name "aapt" | tail -n1)
  if [[ -z "$AAPT_PATH" ]]; then
    echo "aapt not found"
    exit 1
  fi
fi

if [[ ! -e "$KEYSTORE_PATH" ]]; then
  echo "Couldn't find keystore at: $KEYSTORE_PATH"
  exit 1
fi

cordova build android || exit 1

# Remove previous signing artifacts
APK_PATH=$(ls platforms/android/ant-build/*-debug.apk)
"$AAPT_PATH" remove "$APK_PATH" META-INF/MANIFEST.MF || exit 1
"$AAPT_PATH" remove "$APK_PATH" META-INF/CERT.SF
"$AAPT_PATH" remove "$APK_PATH" META-INF/CERT.RSA

# Resign.
jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore $KEYSTORE_PATH -storepass android "$APK_PATH" androiddebugkey || exit 1

# Verify signing
jarsigner -verify "$APK_PATH" || exit 1

echo "Build Complete. APK is at: $APK_PATH"

#!/bin/bash

KEYSTORE_PATH=../CCAHarness-debug.keystore
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

if [[ ! -e platforms/android ]]; then
  echo "Please run this with CWD=CCAHarness"
  exit 1
fi

if [[ ! -e $KEYSTORE_PATH ]]; then
  echo "Couldn't find $KEYSTORE_PATH"
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

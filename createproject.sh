#!/bin/bash

if [[ $# -eq 0 || "$1" = "--help" ]]; then
    echo "Usage: makeharness.sh path/to/cordova-app-harness"
    echo 'Options via variables:'
    echo '  PLATFORMS="android ios"'
    echo '  CORDOVA="path/to/cordova"'
    echo '  PLUGIN_SEARCH_PATH="path1:path2:path3"'
    echo '  DIR_NAME="path/to/put/new/project" (default is "CCAHarness")'
    echo '  CCA="path/to/cca"'
    exit 1
fi

CCA="${CCA-cca}"
AH_PATH=$(cd "$1" && pwd)
H_PATH=$(cd $(dirname "$0") && pwd)
DIR_NAME=CCAHarness

export APP_ID=org.chromium.eduardo
export APP_NAME="Chrome AdT"
"$AH_PATH/createproject.sh" "$DIR_NAME" || exit 1

if [[ -e "$CCA" ]]; then
    CCA="$(cd $(dirname "$CCA") && pwd)/$(basename "$CCA")"
fi
cd "$DIR_NAME"

# Using CCA here to get the right search path.
echo "Installing Chromium plugins"
"$CCA" plugin add \
    org.chromium.bootstrap \
    org.chromium.navigation \
    org.chromium.fileSystem \
    org.chromium.i18n \
    org.chromium.identity \
    org.chromium.idle \
    org.chromium.notifications \
    org.chromium.power \
    org.chromium.socket \
    org.chromium.syncFileSystem \
    org.chromium.FileChooser \
    org.chromium.polyfill.blob_constructor \
    org.chromium.polyfill.CustomEvent \
    org.chromium.polyfill.xhr_features \
    org.apache.cordova.network-information \
    "$H_PATH"/harness-push


if [[ $? != 0 ]]; then
    echo "Plugin installation failed. Probably you need to set PLUGIN_SEARCH_PATH env variable so that it contains the plugin that failed to install."
    exit 1
fi


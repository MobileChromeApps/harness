#!/bin/bash

if [[ $# -eq 0 || "$1" = "--help" ]]; then
    echo "Use this script to create a Chrome ADT project"
    echo
    echo "Usage: createproject.sh path/to/cordova-app-harness"
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

if [[ -e "$CCA" ]]; then
    CCA="$(cd $(dirname "$CCA") && pwd)/$(basename "$CCA")"
else
    if ! which "$CCA"; then
        echo "Could not find cca executable."
        echo "Make sure it's in your path or set the \$CCA variable to its location"
        exit 1
    fi
fi

export APP_ID=org.chromium.ChromeADT
export APP_NAME="Chrome ADT"
export APP_VERSION="0.4.2"
"$AH_PATH/createproject.sh" "$DIR_NAME" || exit 1

cd "$DIR_NAME"

# Using CCA here to get the right search path.
echo "Installing Chromium plugins"
"$CCA" plugin add \
    org.chromium.bootstrap \
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
    org.apache.cordova.keyboard \
    org.apache.cordova.statusbar \
    org.apache.cordova.network-information \
    "$H_PATH"/harness-push


if [[ $? != 0 ]]; then
    echo "Plugin installation failed. Probably you need to set PLUGIN_SEARCH_PATH env variable so that it contains the plugin that failed to install."
    exit 1
fi


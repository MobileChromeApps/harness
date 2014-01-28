#!/bin/sh

# Expects cca and cordova to be on your path.
# Expects to be run from a directory of which cordova-app-harness is a child.
# Arguments are: makeharness.sh <dir> <platforms...>, eg. makeharness.sh android ios
# The default is CCAHarness and both Android and iOS.

DIR=$1
cordova create $1 org.chromium.harness CCAHarness
cd $DIR

cordova platform add $2 $3

# Using CCA here to get the right search path.
cca plugin add org.apache.cordova.file org.apache.cordova.file-transfer org.chromium.bootstrap org.chromium.navigation org.chromium.fileSystem org.chromium.i18n org.chromium.identity org.chromium.idle org.chromium.notifications org.chromium.power org.chromium.socket org.chromium.syncFileSystem org.chromium.FileChooser org.chromium.polyfill.blob_constructor org.chromium.polyfill.CustomEvent org.chromium.polyfill.xhr_features

# Now straight Cordova plugins
cordova plugin add https://github.com/MobileChromeApps/zip.git ../cordova-app-harness/UrlRemap
cordova plugin add "https://git-wip-us.apache.org/repos/asf/cordova-plugins.git#:file-extras"
cordova plugin add "https://github.com/wildabeast/BarcodeScanner.git"

# Copy in the App Harness assets.
rm -rf www
cp -a ../cordova-app-harness/www .

# Change the start page.
awk '{ sub(/content src=".*"/, "content src=\"cdvah/index.html\""); print $0 }' config.xml > config.xml.1
mv config.xml.1 config.xml

cordova prepare


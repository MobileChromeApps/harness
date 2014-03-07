# Chrome Apps Developer Tool for Mobile

This repository contains scripts and assets that will remix the [Apache Cordova project](http://cordova.io)'s [App Harness](https://git-wip-us.apache.org/repos/asf/cordova-app-harness.git) so that it can run Chrome Apps on mobile devices. This is based on the plugins from the [cca](https://github.com/MobileChomeApps/mobile-chrome-apps) toolkit.

## Use a Pre-built APK
Pre-built APKs are available [here](https://github.com/MobileChromeApps/harness/releases).

## Building From Source
Use `makeharness.sh` to create a project. Example invocation:

    CCA=../mobile-chrome-apps/src/cca.js ./makeharness.sh ../../cordova/cordova-app-harness/

For more info:

    ./makeharness.sh --help

### Extra Steps

- Update the app's name, description, author, etc. in `config.xml`. Don't edit the package name or it will break on device.
- Replace the default Cordova icons with your desired icons. TODO: Put icons for the ADT in this repo and have the script install them.
- Build the app in release mode (using Android Studio or Eclipse ADT) to get a signed APK file.

## TODO

- Add icons

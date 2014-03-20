# Chrome Apps Developer Tool for Mobile

This repository contains scripts and assets that will remix the
[Apache Cordova project](https://cordova.apache.org)'s
[App Harness](https://git-wip-us.apache.org/repos/asf/cordova-app-harness.git)
so that it can run Chrome Apps on mobile devices. This is based on the plugins
from the [cca](https://github.com/MobileChomeApps/mobile-chrome-apps) toolkit.

## Use a Pre-built APK
Pre-built APKs are available [here](https://github.com/MobileChromeApps/harness/releases).

## Building From Source
Use `makeharness.sh` to create a project. Example invocation:

    PLATFORMS="android ios" ./makeharness.sh ../../cordova/cordova-app-harness

For more info:

    ./makeharness.sh --help

### Extra Steps

- Replace the title in index.html to "Chrome ADT"
- Replace the default Cordova icons with your desired icons.
- Build the app in release mode to get a signed APK file.

## TODO

- Add icons

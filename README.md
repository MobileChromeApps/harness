# Chrome Apps Developer Tool for Mobile

This repository contains scripts and assets that will remix the
[Apache Cordova project](https://cordova.apache.org)'s
[App Harness](https://git-wip-us.apache.org/repos/asf/cordova-app-harness.git)
so that it can run Chrome Apps on mobile devices. This is based on the plugins
from the [cca](https://github.com/MobileChomeApps/mobile-chrome-apps) toolkit.

## Use a Pre-built APK
Pre-built APKs are available [here](https://github.com/MobileChromeApps/harness/releases).

## Creating a Project
Use `createproject.sh` to create a project. Example invocation:

    PLATFORMS="android ios" ./createproject.sh ../../cordova/cordova-app-harness

For more info:

    ./createproject.sh --help

### Extra Steps

- Replace the default Cordova icons with your desired icons.
  - `rm platforms/android/res/drawable-*/icon.png`
  - `cp ../../mobile-chrome-apps/templates/default-app/assets/icons/icon128.png platforms/android/res/drawable/icon.png`
- Replace the title in index.html to "Chrome ADT vX.X.X"
  - `vim www/cdvah/index.html`

### Cutting a Release

- Update the version in `config.xml`
  - `vim config.xml`
- Update versionCode in `platforms/android/AndroidManifest.xml`
  - `vim platforms/android/AndroidManifest.xml`
- Build apk
  - `cd CCAHarness && ../buildharness.sh`
- Tag release
  - `git tag -am "Tagged vX.X.X-alpha" vX.X.X-alpha`
  - `git push origin master --tags`
- Upload apk to GitHub's releases page
  - Attach the apk
  - Write *short* release notes (download link should be visible without scrolling).

## TODO

- Add custom icon

# Chrome Apps Developer Tool for Mobile

This repository contains scripts and assets that will remix the [Apache Cordova project](http://cordova.io)'s [App Harness](https://git-wip-us.apache.org/repos/asf/cordova-app-harness.git) so that it can run Chrome Apps on mobile devices. This is based on the plugins from the [cca](https://github.com/MobileChomeApps/mobile-chrome-apps) toolkit.

The script is currently quite crude and doesn't build a very nice app bundle. Folliwing is a list of the manual steps required before and after running the script.

## Before

- Install [cca](https://github.com/MobileChromeApps/mobile-chrome-apps) via NPM: `sudo npm install -g cca`
- Check out the original Cordova App Harness: `git clone https://git-wip-us.apache.org/repos/asf/cordova-app-harness.git`

## Running

- Go to the directory where you checked out `cordova-app-harness` (but not inside the `cordova-app-harness` directory).
- Run `path/to/makeharness.sh FolderName [android] [ios]`


## After

- Update the app's name, description, author, etc. in `config.xml`. Don't edit the package name or it will break on device.
- Replace the default Cordova icons with your desired icons. TODO: Put icons for the ADT in this repo and have the script install them.
- Update the `AndroidManifest.xml`, if applicable, to remove `android:debuggable` before release.
- Build the app in release mode (using Android Studio or Eclipse ADT) to get a signed APK file.

## TODO

- Have the script check out cordova-app-harness if it's not found.
- Include icons and make the script install them.

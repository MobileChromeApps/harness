# App Harness Push

This is a [Cordova](http://cordova.io) plugin that integrates with the [App Harness](https://git-wip-us.apache.org/repos/asf/cordova-app-harness.git) to allow pushing new versions of your app over HTTP. This enables live reloading functionality, though making the request is currently the user's responsibility to do manually with `curl`, etc. The plan is to have integration with `cordova watch` or similar, and with [Spark](https://github.com/dart-lang/spark).

## Installation

    cordova plugin add https://github.com/MobileChromeApps/harness-push.git

## Use

**You must** press the "Start Listening" button on the main menu of the App Harness prior to using any of the requests below.

There are currently three requests you can make:

### Push - `cordova serve`

Make a `POST` request to

    /push?type=serve&name=com.example.YourApp&url=http://192.168.1.101:8000

and this will cause the App Harness to return to the main menu, and fetch the app from `cordova serve`.

### Push - `.crx` files

TODO

### Menu - Return to the App Harness menu

Sometimes, especially on emulators, it's hard or impossible to do the three-point touch that triggers the App Harness context menu. Sending a `POST` request to `/menu` will return to the App Harness main menu.

### Exec - Run arbitrary Javascript

This allows the sending of arbitrary Javascript. This is useful for debugging and may be removed later. Contact us with any interesting use-cases for this endpoint.

    /exec?code=location='file:///android_asset/www/someotherpage.html'


# App Harness Push

Allows pushing updates of apps to the App Harness.

## Installation

    cordova plugin add https://github.com/MobileChromeApps/harness-push.git

## Use

**You must** press the "Start Listening" button on the main menu of the App Harness prior to using any of the requests below.

There are currently four kinds of requests you can make:

## Port Forwarding

If you are not on the same network, you can use adb to port forward:

    adb forward tcp:2424 tcp:2424

When done:

    adb forward --remove tcp:2424

### Push - `cordova serve`

Make a `POST` request on port 2424 to:

    /push?type=serve&name=com.example.YourApp&url=http://192.168.1.101:8000

and this will cause the App Harness to return to the main menu, and fetch the app from `cordova serve`.

### Push - `.crx` files

Make a `POST` request on port 2424 to

    /push?type=crx&name=appname

with the file passed as an HTTP form submission, with the key `file`. The following curl line will do it, for example:

    curl -X POST "http://192.168.1.102:2424/push?type=crx&name=myapp" -F "file=@path/to/myapp.crx"

### Menu - Return to the App Harness menu

Sometimes, especially on emulators, it's hard or impossible to do the three-point touch that triggers the App Harness context menu. Sending a `POST` request on port 2424 to `/menu` will return to the App Harness main menu.

### Exec - Run arbitrary Javascript

This allows the sending of arbitrary Javascript. This is useful for debugging and may be removed later. Contact us with any interesting use-cases for this endpoint.

    /exec?code=location='file:///android_asset/www/someotherpage.html'


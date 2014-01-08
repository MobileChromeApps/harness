// Copyright (c) 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.apache.appharness;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class Push extends CordovaPlugin {

    private static final String LOG_TAG = "HarnessPush";
    private static final int PORT = 2424;

    private ServerSocket serverSocket;

    private JSONObject latestPush;

    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        if ("listen".equals(action)) {
            listen(callbackContext);
            return true;
        } else if ("listening".equals(action)) {
            listening(callbackContext);
            return true;
        } else if ("pending".equals(action)) {
            pending(callbackContext);
            return true;
        }

        return false;
    }

    private void listen(CallbackContext callbackContext) {
        // First, check that we're not already listening.
        if (serverSocket != null) {
            callbackContext.success();
            return;
        }

        // Create the ServerSocket.
        serverSocket = new ServerSocket(PORT);
        new ListenThread().start();
    }

    private void listening(CallbackContext callbackContext) {
        callbackContext.success(serverSocket != null ? 1 : 0);
    }

    private void pending(CallbackContext callbackContext) {
        if (latestPush != null) {
            callbackContext.success(latestPush);
            latestPush = null;
        } else {
            callbackContext.success((JSONObject) null); // TODO: Is this legal?
        }
    }

    private class ListenThread extends Thread {
        public void run() {
            try {
                while(true) {
                    // Block and wait for a client to connect.
                    Socket s = Push.this.serverSocket.accept();
                    // Spawn a receive thread to handle the results.
                    ReceiveThread rt = new ReceiveThread(s);
                    rt.start();
                }
            } catch (IOException ioe) {
                if (Push.this.serverSocket == null || Push.this.serverSocket.isClosed()) {
                    Log.i(LOG_TAG, "No longer listening, the server socket was closed.");
                } else {
                    Log.w(LOG_TAG, "Error in listening thread.", ioe);
                }
            }
        }
    }

    private class ReceiveThread extends Thread {
        private Socket socket;
        public ReceiveThread(Socket s) {
            super();
            this.socket = s;
        }

        public void run() {
            // Read the entire contents of the message into memory.
            try {
                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

                // Read the first 7 bytes which give the message type.
                byte[] typeBytes = new byte[7];
                int firstByte = bis.read(); // Block until we get the first byte.
                if (firstByte == -1) return;

                typeBytes[0] = (byte) firstByte;
                int bytes = 1;
                while(bytes < 7) {
                    int read = bis.read(typeBytes, bytes, 7-bytes);
                    if (read < 0) return;
                    bytes += read;
                }

                String msgType = new String(typeBytes);
                if ("PUSHSRV".equals(msgType)) {
                    // Gives a "cordova serve" address from which the app should be updated.
                    // NB: This will override the serve URL saved in the app's metadata.

                    // The next byte gives the length of the app's name in bytes,
                    // Then the name as an unterminated string.
                    // Then a byte giving the length of the URL in bytes,
                    // Then the URL as an unterminated string.
                    String name = readLengthString(bis);
                    String url = readLengthString(bis);

                    if (name == null || url == null) return; // Invalid message.
                    JSONObject payload = new JSONObject();
                    payload.put("name", name);
                    payload.put("type", "serve");
                    payload.put("url", url);
                    Push.this.latestPush = payload;
                }
            } catch (IOException ioe) {
                Log.w(LOG_TAG, "Exception while reading from socket", ioe);
            } catch (JSONException je) {
                Log.w(LOG_TAG, "Exception building JSON result from push command", je);
            }
        }

        private String readLengthString(BufferedInputStream bis) throws IOException {
            int len = bis.read();
            if (len <= 0) return null;
            byte[] arr = new byte[len];
            int bytes = 0;
            while(bytes < len) {
                int read = bis.read(arr, bytes, len-bytes);
                if (read < 0) return null;
                bytes += read;
            }

            return new String(arr);
        }
    }

}

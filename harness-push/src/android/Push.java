// Copyright (c) 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.apache.appharness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.urlremap.UrlRemap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;

public class Push extends CordovaPlugin {

    private static final String LOG_TAG = "HarnessPush";
    private static final int PORT = 2424;
    
    private PushServer server;
    private JSONObject latestPush;
    private boolean listening = false;

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
        if (listening) {
            callbackContext.success();
            return;
        }

        // Create the NanoHTTPD server.
        try {
            server = new PushServer();
            server.start();
            listening = true;
            callbackContext.success();
        } catch (IOException ioe) {
            Log.w(LOG_TAG, "Error launching web server", ioe);
            callbackContext.error("Could not launch server");
        }
    }

    private void listening(CallbackContext callbackContext) {
        callbackContext.success(listening ? 1 : 0);
    }

    private void pending(CallbackContext callbackContext) {
        if (latestPush != null) {
            callbackContext.success(latestPush);
            latestPush = null;
        } else {
            callbackContext.success(0);
        }
    }
    
    private void injectJS(final String toInject) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
            	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    webView.loadUrl("javascript:" + toInject);
                } else {
                    webView.evaluateJavascript(toInject, null);
                }
            }
        });
    }

    private void restartAppHarness() {
        ((UrlRemap)webView.pluginManager.getPlugin("UrlRemap")).resetMappings();
        webView.loadUrlIntoView(Config.getStartUrl(), false);
    }

    private class PushServer extends NanoHTTPD {
        public PushServer() {
            super(Push.PORT);
        }

        public Response serve(IHTTPSession session) {
            if (session.getMethod() != Method.POST)
                return new Response(Response.Status.METHOD_NOT_ALLOWED, "text/plain", "Method " + String.valueOf(session.getMethod()) + " not allowed.");

            if (session.getUri().equals("/push")) {
                Map<String, List<String>> params = decodeParameters(session.getQueryParameterString());

                List<String> typeList = params.get("type");
                String type = null;
                if (typeList != null) type = typeList.get(0);
                if (type == null) return new Response(Response.Status.BAD_REQUEST, "text/plain", "No push type specified");

                if ("crx".equals(type)) {
                    // Receive the file that came with the request, and save it under /data/data/my.app.id/push.crx.
                    try {
                        Map<String, String> files = new HashMap<String, String>();
                        session.parseBody(files);
                        if (!files.containsKey("file")) {
                            return new Response(Response.Status.BAD_REQUEST, "text/plain", "You must send a file with the form key 'file'.");
                        }
                        
                        // Copy the file out of the ephemeral cache/foo location and into somewhere permanent.
                        String source = files.get("file");
                        String target = source.replaceFirst("/cache/", "/crx_cache/") + ".crx";
                        String dir = target.replaceFirst("/[^/]*$", "");
                        
                        File cacheDir = new File(dir);
                        if (! (cacheDir.mkdir() || cacheDir.isDirectory()))
                        	return new Response(Response.Status.INTERNAL_ERROR, "text/plain", "Could not create cache directory");
                        
                        InputStream in = new FileInputStream(new File(source));
                        OutputStream out = new FileOutputStream(new File(target));
                        byte[] buf = new byte[1024];
                        int len;
                        while((len = in.read(buf)) > 0) {
                        	out.write(buf, 0, len);
                        }
                        in.close();
                        out.close();
                        
                        // Now prepare the return value for the harness.
                        String url = "file://" + target;
                        JSONObject payload = new JSONObject();
                        payload.put("name", params.get("name").get(0));
                        payload.put("type", "crx");
                        payload.put("url", url);
                        Push.this.latestPush = payload;
                        Push.this.restartAppHarness();
                        return new Response(Response.Status.OK, "text/plain", "Push successful");
                    } catch (Exception e) {
                        Log.w(LOG_TAG, "Exception while receiving files", e);
                    }
                } else if ("serve".equals(type)) {
                    // Create the latestPush value from the parameters.
                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("name", params.get("name").get(0));
                        payload.put("type", "serve");
                        payload.put("url", params.get("url").get(0));
                        Push.this.latestPush = payload;
                        Push.this.restartAppHarness();
                        return new Response(Response.Status.OK, "text/plain", "Push successful");
                    } catch (JSONException je) {
                        Log.w(LOG_TAG, "JSONException while building 'serve' mode push data", je);
                        return new Response(Response.Status.INTERNAL_ERROR, "text/plain", "Error building JSON result");
                    }
                }

                return new Response(Response.Status.BAD_REQUEST, "text/plain", "Push type '" + type + "' unknown");
            } else if (session.getUri().equals("/exec")) {
                Map<String, List<String>> params = decodeParameters(session.getQueryParameterString());
                for (String code : params.get("code")) {
                    injectJS(code);
                }
                return new Response(Response.Status.OK, "text/plain", "Executed successfully");
            } else if (session.getUri().equals("/menu")) {
            	//injectJS("window.location = 'app-harness:///cdvah/index.html'");
            	Push.this.restartAppHarness();
            	return new Response(Response.Status.OK, "text/plain", "Returning to main menu");
            } else {
                return new Response(Response.Status.NOT_FOUND, "text/plain", "URI " + String.valueOf(session.getUri()) + " not found");
            }
        }
    }
}

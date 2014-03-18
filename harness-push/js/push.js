// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

var exec = require('cordova/exec');

exports.getListenAddress = function(win) {
    chrome.socket.getNetworkList(function(interfaces) {
        // Filter out ipv6 addresses.
        var ret = interfaces.filter(function(i) {
            return i.address.indexOf(':') === -1;
        }).map(function(i) {
            return i.address;
        }).join(', ');
        win(ret);
    });
};

exports.listen = function(win, fail) {
    exec(win, fail, 'HarnessPush', 'listen', []);
};

exports.listening = function(win, fail) {
    exec(win, fail, 'HarnessPush', 'listening', []);
};

exports.pending = function(win, fail) {
    exec(win, fail, 'HarnessPush', 'pending', []);
};


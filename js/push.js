// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

var exec = require('cordova/exec');

exports.listen = function(win, fail) {
    exec(win, fail, 'HarnessPush', 'listen', []);
};

exports.listening = function(win, fail) {
    exec(win, fail, 'HarnessPush', 'listening', []);
};

exports.pending = function(win, fail) {
    exec(win, fail, 'HarnessPush', 'pending', []);
};


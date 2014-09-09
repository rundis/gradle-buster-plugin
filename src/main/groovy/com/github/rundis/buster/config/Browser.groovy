package com.github.rundis.buster.config

import com.github.rundis.buster.internal.browsercapture.SupportedBrowser


class Browser {
    final SupportedBrowser supportedBrowser
    final String name
    boolean remote

    Browser(String name) {
        this.name = name
        this.supportedBrowser = SupportedBrowser.fromShortName(name)
    }
}

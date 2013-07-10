package org.gradle.plugins.buster.config

import org.gradle.plugins.buster.internal.browsercapture.SupportedBrowser


class Browser {
    final SupportedBrowser supportedBrowser
    final String name
    boolean remote

    Browser(String name) {
        this.name = name
        this.supportedBrowser = SupportedBrowser.fromShortName(name)
    }
}

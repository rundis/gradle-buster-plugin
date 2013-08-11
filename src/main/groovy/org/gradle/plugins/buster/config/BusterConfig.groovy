package org.gradle.plugins.buster.config

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.plugins.buster.internal.browsercapture.SupportedBrowser

class BusterConfig {
    static final DEFAULT_BUSTER_PORT = 1111

    Integer port = DEFAULT_BUSTER_PORT
    File configFile
    NamedDomainObjectCollection<Browser> browsers

    BusterConfig(Project project) {
        browsers = project.container(Browser)
        browsers.add(new Browser(SupportedBrowser.PHANTOMJS.shortName))
    }

    void browsers(Closure c) {
        browsers.clear()
        browsers.configure(c)
    }

    String getCaptureUrl() {
        "http://localhost:${port}/capture"
    }

    String getServerUrl() {
        "http://localhost:${port}"
    }
}

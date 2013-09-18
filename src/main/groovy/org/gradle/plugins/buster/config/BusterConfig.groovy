package org.gradle.plugins.buster.config

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.plugins.buster.internal.browsercapture.SupportedBrowser

class BusterConfig {
    static final DEFAULT_BUSTER_PORT = 1111
    static final BUSTER_EXECUTABLES_SYS_PROP = 'buster.executables.path'

    Integer port = DEFAULT_BUSTER_PORT
    File configFile
    String busterExecutablesPath

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

    String getTestExecutablePath() {
        "${executablesPath()}buster-test"
    }

    String getServerExecutablePath() {
        "${executablesPath()}buster-server"
    }

    private String executablesPath() {
        String path = System.getProperty(BUSTER_EXECUTABLES_SYS_PROP) ?: busterExecutablesPath?: ""
        path ? "${path}/" : ""
    }




}

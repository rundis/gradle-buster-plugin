package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.config.BusterConfig
import org.gradle.plugins.buster.internal.browsercapture.BrowserCapturer


class CaptureBrowsersTask extends DefaultTask {
    static NAME = "captureBrowsers"

    BrowserCapturer browserCapturer

    protected CaptureBrowsersTask setBrowserCapturer(BrowserCapturer browserCapturer) {
        this.browserCapturer = browserCapturer
        this
    }


    @TaskAction
    void capture() {
        BusterConfig busterConfig = project.extensions.buster

        logger.info "Capturing browsers"
        browserCapturer.capture(busterConfig.browsers, busterConfig.captureUrl)
    }


}

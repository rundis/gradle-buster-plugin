package org.gradle.plugins.buster.internal

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.plugins.buster.config.BusterConfig
import org.gradle.plugins.buster.internal.browsercapture.BrowserCapturer
import org.gradle.plugins.buster.internal.process.BusterServer

class BusterTestingService {
    BrowserCapturer browserCapturer
    Project project
    private BusterServer busterServer


    void prepareForTest() {
        startBuster()
        captureBrowsers()
    }

    void tearDownAfterTest() {
        shutdownBrowsers()
        stopBuster()
    }

    private void startBuster() {
        logger.info "Starting buster server"
        busterServer = BusterServer.start(busterConfig)
        project.logger.info "Buster server started on port: $busterConfig.port"
    }

    private void captureBrowsers() {
        project.logger.info "Capturing browsers"
        browserCapturer.capture(busterConfig.browsers, busterConfig.captureUrl)
    }


    private void stopBuster() {
        if(!busterServer || busterServer.stopped) return
        project.logger.info "Stopping buster server"
        busterServer?.stop()
    }

    private void shutdownBrowsers() {
        if(!browserCapturer.captures) return
        project.logger.info "Releasing captured browsers"
        browserCapturer.shutdown()
    }


    private Logger getLogger() {
        project.logger
    }

    private BusterConfig getBusterConfig() {
        project.extensions.buster
    }
}

package org.gradle.plugins.buster.internal

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.plugins.buster.config.BusterConfig
import org.gradle.plugins.buster.internal.browsercapture.BrowserCapturer

class BusterTestingService {
    BrowserCapturer browserCapturer
    Buster buster
    Project project


    void prepareForTest() {
        startBuster()
        captureBrowsers()
    }

    void tearDownAfterTest() {
        shutdownBrowsers()
        stopBuster()
    }

    private void startBuster() {
        if (buster.running) {
            throw new GradleException("There is already a running buster instance")
        }

        logger.info "Starting buster server"
        Map cmdResult = buster.startServer(busterConfig)
        if (!cmdResult.ok) {
            throw new GradleException("Error starting Buster Server: $cmdResult.message")
        }
        project.logger.info cmdResult.message
    }

    private void captureBrowsers() {
        project.logger.info "Capturing browsers"
        browserCapturer.capture(busterConfig.browsers, busterConfig.captureUrl)
    }


    private void stopBuster() {
        project.logger.info "Stopping buster server"
        buster.stopServer()
    }

    private void shutdownBrowsers() {
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

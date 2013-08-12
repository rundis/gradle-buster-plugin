package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.internal.browsercapture.BrowserDriverUtil
import org.gradle.plugins.buster.internal.browsercapture.SupportedBrowser

class BusterSetupWebDriversTask extends DefaultTask {
    static NAME = "busterSetupWebDrivers"

    protected BrowserDriverUtil driverUtil

    BusterSetupWebDriversTask() {
        description = "Download and setup remote webdrivers helper executables"

        onlyIf {
            isSetupNeeded()
        }
    }

    protected BusterSetupWebDriversTask setDriverUtil(BrowserDriverUtil driverUtil) {
        this.driverUtil = driverUtil
        this
    }

    @TaskAction
    void setupDrivers() {
        if(isChromeDriverNeeded()) {
            logger.info "Setup chromedriver using default dir: ${driverUtil.defaultChromeDriversDir}"
            driverUtil.setupChromeDriver()
        }
    }

    boolean isSetupNeeded() {
        chromeDriverNeeded && !driverUtil.chromeDriverSetup
    }

    boolean isChromeDriverNeeded() {
        def browsers = project.buster.browsers.collect{it.name}

        browsers.contains(SupportedBrowser.CHROME.shortName)
    }

}

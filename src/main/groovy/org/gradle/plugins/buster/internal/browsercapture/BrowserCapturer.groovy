package org.gradle.plugins.buster.internal.browsercapture

import org.gradle.api.logging.Logger
import org.gradle.plugins.buster.config.Browser
import org.openqa.selenium.WebDriver

class BrowserCapturer {
    final Map<SupportedBrowser, WebDriver> captures = [:]
    final Logger logger


    BrowserCapturer(Logger logger) {
        this.logger = logger
    }


    def capture(Collection<Browser> browsers, String captureUrl) {
        browsers.each {
            capture it, captureUrl
        }
    }

    def capture(Browser browser, String captureUrl) {
        if (captures.containsKey(browser.supportedBrowser)) {
            logger.info("Browser ${browser.name} already captured. Noop.")
            return
        }

        try {
            WebDriver driver = createDriver(browser.supportedBrowser)
            captures[browser.supportedBrowser] = driver

            driver.get(captureUrl)

        } catch (Exception e) {
            logger.error("Error capturing ${browser.name}", e)
        }
    }


    Map<SupportedBrowser, WebDriver> getCaptures() {
        return Collections.unmodifiableMap(captures)
    }

    private def createDriver(SupportedBrowser supportedBrowser) {
        supportedBrowser.driverCreate.call()


    }

    def shutdown() {
        captures.each {
            try {
                it.value.quit()
            } catch (Exception e ) {
                logger.info("Error shutting down browser ${it.key.shortName}", e)
            }
        }
        captures.clear()
    }
}

package org.gradle.plugins.buster.internal.browsercapture

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.safari.SafariDriver


public enum SupportedBrowser {
    FIREFOX('firefox', {new FirefoxDriver() }),
    SAFARI('safari', {new SafariDriver() }),
    PHANTOMJS('phantomjs', {new PhantomJSDriver(new DesiredCapabilities())})

    private final String shortName
    private final Closure driverCreate


    private SupportedBrowser(String shortName, Closure driverCreate) {
        this.shortName = shortName
        this.driverCreate = driverCreate
    }

    String getShortName() { shortName }

    Closure getDriverCreate() { driverCreate }

    static SupportedBrowser fromShortName(String shortName) {
        assertValid(shortName)
        values().find{it.shortName == shortName}
    }

    private static assertValid(String shortName) {
        if(!values().find{it.shortName == shortName}) {
            throw new IllegalArgumentException(
                    "Browser with name $shortName is not supported.\nCurrently supported browsers are: ${supportedBrowsers()}")
        }
    }


    private static supportedBrowsers() {
        values().shortName.join(",")
    }

}
package org.gradle.plugins.buster.internal.browsercapture

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.safari.SafariDriver


public enum SupportedBrowser {
    FIREFOX('firefox', FirefoxDriver),
    SAFARI('safari', SafariDriver)

    private final String shortName
    private final Class driverClass


    private SupportedBrowser(String shortName, Class driverClass) {
        this.shortName = shortName
        this.driverClass = driverClass
    }

    String getShortName() { shortName }

    Class getDriverClass() { driverClass }

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
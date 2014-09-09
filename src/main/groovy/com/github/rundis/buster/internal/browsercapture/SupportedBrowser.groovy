package com.github.rundis.buster.internal.browsercapture

import org.gradle.api.logging.LogLevel
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.safari.SafariDriver


public enum SupportedBrowser {
    FIREFOX('firefox', {Map params -> new FirefoxDriver() }),

    SAFARI('safari', {Map params -> new SafariDriver() }),

    PHANTOMJS('phantomjs', {Map params ->
        def caps = new DesiredCapabilities()

        def logLevel = params.logLevel ?: LogLevel.ERROR
        def phantomLogLevel
        switch (logLevel) {
            case [LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG]:
                phantomLogLevel = logLevel.name()
                break
            case LogLevel.LIFECYCLE:
                phantomLogLevel = LogLevel.WARN.name()
                break
            case LogLevel.QUIET:
                phantomLogLevel = LogLevel.ERROR.name()
                break
            default:
                phantomLogLevel = "ERROR"
        }

        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
            ["--webdriver-loglevel=$phantomLogLevel"] as String[])
        new PhantomJSDriver(caps)
    }),

    CHROME('chrome', {Map params -> new ChromeDriver()})

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
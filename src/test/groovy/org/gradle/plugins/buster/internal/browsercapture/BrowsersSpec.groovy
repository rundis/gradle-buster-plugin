package org.gradle.plugins.buster.internal.browsercapture

import org.gradle.plugins.buster.config.Browser

import static org.gradle.plugins.buster.internal.browsercapture.SupportedBrowser.*
import org.gradle.api.logging.Logger
import org.openqa.selenium.safari.SafariDriver
import spock.lang.Specification


class BrowsersSpec extends Specification {

    private static final String CAPTURE_URL = "http://localhost:1111/capture"
    private Browsers browsers
    private Logger loggerMock

    def setup() {
        loggerMock = Mock(Logger)
        browsers = new Browsers(loggerMock)
    }


    def "capture supported browser"() {
        given:
        def mockDriver = Mock(SafariDriver)
        browsers.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}

        when:
        browsers.capture(createBrowsers(SAFARI), CAPTURE_URL)

        then:
        1 * mockDriver.get(CAPTURE_URL)
        browsers.captures.size() == 1
        browsers.captures[SAFARI] == mockDriver
    }

    def "capture already captured, does nothing"() {
        given:
        def mockDriver = Mock(SafariDriver)
        browsers.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}

        when:
        browsers.capture(createBrowsers(SAFARI), CAPTURE_URL)

        then:
        1 * mockDriver.get(_)

        when:
        browsers.capture(createBrowsers(SAFARI), CAPTURE_URL)

        then:
        0 * mockDriver.get(_)
        1 * loggerMock.info(_)
    }

    def "capture swallows exceptions and logs errors"() {
        given:
        def mockDriver = Mock(SafariDriver)
        browsers.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}
        def ex = new RuntimeException("Something really bad happening on get")

        mockDriver.get(CAPTURE_URL) >> {throw ex}

        when:
        browsers.capture(createBrowsers(SAFARI), CAPTURE_URL)

        then:
        1 * loggerMock.error(_, ex)

    }


    def "shutdown browsers"() {
        given:
        def mockDriver = Mock(SafariDriver)
        browsers.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}
        browsers.capture(createBrowsers(SAFARI, FIREFOX), CAPTURE_URL)

        when:
        browsers.shutdown()

        then:
        2 * mockDriver.quit()
        browsers.captures.size() == 0
    }


    def "Failure during shutdown logs errors"() {
        given:
        def mockDriver = Mock(SafariDriver)
        browsers.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}
        browsers.capture(createBrowsers(SAFARI, FIREFOX), CAPTURE_URL)
        def ex = new RuntimeException("Something really bad happening on get")
        mockDriver.quit() >> {throw ex}


        when:
        browsers.shutdown()

        then:
        2 * loggerMock.error(_, ex)
    }



    private Collection createBrowsers(SupportedBrowser ...supportedBrowsers) {
        supportedBrowsers.collect {
            new Browser(it.shortName)
        }
    }

}

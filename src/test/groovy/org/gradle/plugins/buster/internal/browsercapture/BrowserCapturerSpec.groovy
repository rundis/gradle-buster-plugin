package org.gradle.plugins.buster.internal.browsercapture

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.LoggingManager
import org.gradle.plugins.buster.config.Browser

import static org.gradle.plugins.buster.internal.browsercapture.SupportedBrowser.*
import org.gradle.api.logging.Logger
import org.openqa.selenium.safari.SafariDriver
import spock.lang.Specification


class BrowserCapturerSpec extends Specification {

    private static final String CAPTURE_URL = "http://localhost:1111/capture"
    private BrowserCapturer capturer
    private Logger loggerMock


    def setup() {
        loggerMock = Mock(Logger)
        capturer = new BrowserCapturer(loggerMock)
    }


    def "capture supported browser"() {
        given:
        def mockDriver = Mock(SafariDriver)
        capturer.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}

        when:
        capturer.capture(browsers(SAFARI), CAPTURE_URL)

        then:
        1 * mockDriver.get(CAPTURE_URL)
        capturer.captures.size() == 1
        capturer.captures[SAFARI] == mockDriver
    }

    def "capture already captured, does nothing"() {
        given:
        def mockDriver = Mock(SafariDriver)
        capturer.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}

        when:
        capturer.capture(browsers(SAFARI), CAPTURE_URL)

        then:
        1 * mockDriver.get(_)

        when:
        capturer.capture(browsers(SAFARI), CAPTURE_URL)

        then:
        0 * mockDriver.get(_)
        1 * loggerMock.info(_)
    }

    def "capture swallows exceptions and logs errors"() {
        given:
        def mockDriver = Mock(SafariDriver)
        capturer.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}
        def ex = new RuntimeException("Something really bad happening on get")

        mockDriver.get(CAPTURE_URL) >> {throw ex}

        when:
        capturer.capture(browsers(SAFARI), CAPTURE_URL)

        then:
        1 * loggerMock.error(_, ex)

    }


    def "shutdown browsers"() {
        given:
        def mockDriver = Mock(SafariDriver)
        capturer.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}
        capturer.capture(browsers(SAFARI, FIREFOX), CAPTURE_URL)

        when:
        capturer.shutdown()

        then:
        2 * mockDriver.quit()
        capturer.captures.size() == 0
    }


    def "Failure during shutdown logs errors"() {
        given:
        def mockDriver = Mock(SafariDriver)
        capturer.metaClass.createDriver = {SupportedBrowser browser -> mockDriver}
        capturer.capture(browsers(SAFARI, FIREFOX), CAPTURE_URL)
        def ex = new RuntimeException("Something really bad happening on get")
        mockDriver.quit() >> {throw ex}


        when:
        capturer.shutdown()

        then:
        2 * loggerMock.info(_, ex)
    }



    private Collection browsers(SupportedBrowser ...supportedBrowsers) {
        supportedBrowsers.collect {
            new Browser(it.shortName)
        }
    }

}

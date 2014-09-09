package com.github.rundis.buster.internal.browsercapture

import org.apache.commons.lang3.SystemUtils
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.rundis.buster.internal.browsercapture.BrowserDriverUtil.CHROME_BASE_FILE_NAME
import static com.github.rundis.buster.internal.browsercapture.BrowserDriverUtil.getCHROME_DRIVER_VERSION


class BrowserDriverUtilSpec extends Specification {

    BrowserDriverUtil driverUtil

    def setup() {
        driverUtil = new BrowserDriverUtil()
    }

    @Unroll
    def "resolves chrome driver filename based on os and architecture"() {
        given:
        GroovyMock(SystemUtils, global: true)

        when:
        SystemUtils.IS_OS_MAC_OSX >> (os == "mac")
        SystemUtils.IS_OS_LINUX >> (os == "linux")
        SystemUtils.OS_ARCH >> (os64 ? "arch64" : "arch32")

        def filename = driverUtil.resolveChromeDriverFileName()

        then:
        filename == expectedName


        where:
        os      | os64  | expectedName
        "mac"   | false | "${CHROME_BASE_FILE_NAME}_mac32_${CHROME_DRIVER_VERSION}.zip"
        "mac"   | true  | "${CHROME_BASE_FILE_NAME}_mac32_${CHROME_DRIVER_VERSION}.zip"
        "linux" | false | "${CHROME_BASE_FILE_NAME}_linux32_${CHROME_DRIVER_VERSION}.zip"
        "linux" | true  | "${CHROME_BASE_FILE_NAME}_linux64_${CHROME_DRIVER_VERSION}.zip"
    }

    def "unsupported os for chrome driver throws assertion error"() {
        given:
        GroovyMock(SystemUtils, global: true)
        SystemUtils.IS_OS_LINUX >> false
        SystemUtils.IS_OS_MAC_OSX >> false
        SystemUtils.IS_OS_SOLARIS >> true

        when:
        driverUtil.resolveChromeDriverFileName()

        then:
        AssertionError err = thrown()
    }


    def "get default chrome drivers dir"() {
        given:
        GroovyMock(SystemUtils, global: true)
        SystemUtils.USER_HOME >> "/home/users/dufus"

        when:
        def dir = driverUtil.defaultChromeDriversDir

        then:
        dir.absolutePath == "/home/users/dufus/.gradle/browserdrivers/chrome"
    }

}

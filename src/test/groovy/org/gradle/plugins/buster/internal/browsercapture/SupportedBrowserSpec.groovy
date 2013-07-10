package org.gradle.plugins.buster.internal.browsercapture

import spock.lang.Specification


class SupportedBrowserSpec extends Specification {

    def "create supported browser from shortName"() {
        expect:
        SupportedBrowser.fromShortName(shortName) == browser

        where:
        shortName | browser
        "firefox" | SupportedBrowser.FIREFOX
        "safari"  | SupportedBrowser.SAFARI
    }

    def "create with unsupported shortname throws exception"() {
        when:
        SupportedBrowser.fromShortName("dummy")

        then:
        IllegalArgumentException ex = thrown()

        ex.message.contains("firefox")
    }
}

package org.gradle.plugins.buster.internal

import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import spock.lang.Specification
import spock.lang.Unroll

class JUnitTestXmlSpec extends Specification {

    @Unroll
    def "validate that invalid xml throws exception"() {
        when:
        new JUnitTestXml(xml, Mock(Logger))

        then:
        Exception ex = thrown()
        ex.message.contains(errContains)

        where:
        xml                  | errContains
        null                 | "Empty"
        "No slaves captured" | "No slaves captured"
        "<xml><body></xml>"  | "Invalid test xml"
    }

    def "xml with no error validates fine"() {
        when:
        def logger = Mock(Logger)
        def xml = """<?xml version="1.0" encoding="UTF-8" ?>
            <testsuites>
                <testsuite errors="0" tests="3" time="0" failures="0" name="PhantomJS"/>
            </testsuites>
        """

        def retVal = new JUnitTestXml(xml, logger).validateNoErrors()

        then:
        retVal
    }

    def "xml with failures throws exception"() {
        given:
        def xml = """<?xml version="1.0" encoding="UTF-8" ?>
            <testsuites>
                <testsuite errors="0" tests="3" time="0" failures="3" name="PhantomJS"/>
            </testsuites>
        """

        when:
        new JUnitTestXml(xml, Mock(Logger)).validateNoErrors()

        then:
        GradleException ex = thrown()

    }

    def "xml with errors throws exception"() {
        given:
        def xml = """<?xml version="1.0" encoding="UTF-8" ?>
            <testsuites>
                <testsuite errors="3" tests="3" time="0" failures="0" name="PhantomJS"/>
            </testsuites>
        """

        when:
        new JUnitTestXml(xml, Mock(Logger)).validateNoErrors()

        then:
        GradleException ex = thrown()

    }


    def "log results gives highlevel summary for info level"() {
        given:
        def xml = """<?xml version="1.0" encoding="UTF-8" ?>
            <testsuites>
                <testsuite errors="0" tests="3" time="0" failures="0" name="PhantomJS"/>
            </testsuites>
        """

        def logger = Mock(Logger)

        when:
        new JUnitTestXml(xml, logger).logResults()

        then:
        1 * logger.info({it.contains("3")})


    }





}

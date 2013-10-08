package org.gradle.plugins.buster.internal.process

import org.gradle.api.GradleException
import org.gradle.plugins.buster.config.BusterConfig
import spock.lang.Specification


class BusterServerIntegrationSpec extends Specification {

    BusterServer server


    def cleanup() {
        if(BusterHelper.running) {
            server ? server.stop() : BusterHelper.stopServer()
        }
    }

    def "start and stop server ok"() {
        given:
        def configMock = Mock(BusterConfig) {
            getPort() >> 1111
            getServerExecutablePath() >> "buster-server"
        }

        when:
        server = BusterServer.start(configMock)

        then:
        BusterHelper.running

        when:
        server.stop()

        then:
        server.stopped
        !BusterHelper.running
    }

    def "start server throws exception when port already in use"() {
        given:
        def configMock = Mock(BusterConfig) {
            getPort() >> 1111
            getServerExecutablePath() >> "buster-server"
        }

        when:
        server = BusterServer.start(configMock)

        then:
        BusterHelper.running

        when:
        BusterServer.start(configMock)

        then:
        GradleException ex = thrown()
        ex.message.contains("Error starting")
    }

}

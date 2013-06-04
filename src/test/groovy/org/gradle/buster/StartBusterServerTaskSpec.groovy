package org.gradle.buster

import org.gradle.api.GradleException

class StartBusterServerTaskSpec extends AbstractBusterSpecification {
    StartBusterServerTask task

    def setup() {
        task = mockCollaborators(StartBusterServerTask.NAME, buster:busterMock)
    }

    def "start successfully"() {
        when:
        task.start()

        then:
        1 * busterMock.startServer(busterConfig) >> [ok: true, message: "Started fine"]
    }


    def "start throws exception when buster fails to start"() {
        when:
        1 * busterMock.startServer(busterConfig) >> [ok: false, message: "Failed to start"]
        task.start()

        then:
        def ex = thrown(GradleException)
        assert ex.message.contains("Failed to start")
    }

    def "execute with buster server running should be skipped"() {
        given:
        busterMock.running >> true

        when:
        task.execute()

        then:
        !task.didWork
        0 * busterMock.startServer(busterConfig)
    }

}

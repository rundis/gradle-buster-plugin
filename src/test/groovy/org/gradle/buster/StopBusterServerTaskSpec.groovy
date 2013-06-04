package org.gradle.buster


class StopBusterServerTaskSpec extends AbstractBusterSpecification {
    StopBusterServerTask task


    def setup() {
        task = mockCollaborators(StopBusterServerTask.NAME, buster:busterMock)
    }

    def "stop runs successfully"() {
        given:
        busterMock.getPid() >> 123

        when:
        task.stop()

        then:
        1 * busterMock.stopServer()
    }

    def "stop doesn't stop buster (silently) throws exception"() {
        given:
        busterMock.getPid() >> 123
        busterMock.running >> true

        when:
        task.stop()

        then:
        1 * busterMock.stopServer()
        AssertionError err = thrown()
        err.message.contains("Failed to stop")
    }

    def "execute when buster isn't running does nothing"() {
        given:
        busterMock.running >> false

        when:
        task.execute()

        then:
        !task.didWork
    }



}

package org.gradle.buster


class StopPhantomTaskSpec extends AbstractBusterSpecification {
    StopPhantomTask task

    def setup() {
        task = mockCollaborators(StopPhantomTask.NAME, phantom:phantomMock)
    }

    def "stop phantom successfully"() {
        when:
        task.stop()

        then:
        1 * phantomMock.stopServer()
    }

    def "execute does nothing when phantom is not running"() {
        given:
        phantomMock.running >> false

        when:
        task.execute()

        then:
        !task.didWork
    }
}

package org.gradle.buster

import org.gradle.api.GradleException


class CapturePhantomTaskSpec extends AbstractBusterSpecification {
    CapturePhantomTask task


    def setup() {
        task = mockCollaborators(CapturePhantomTask.NAME, buster:busterMock, phantom:phantomMock)

        task.metaClass.prepareForPhantomFile = {->
            // stub out
        }
    }

    def "capture phantomjs successfully"() {
        given:
        busterMock.running >> true

        when:
        task.capturePhantomJs()

        then:
        1 * phantomMock.createPhantomFile(busterConfig, _)
        1 * phantomMock.capturePhantom(busterConfig, _) >> [ok:true, message: "Phantom captured"]
    }

    def "capture phantom fails, throws exception"() {
        given:
        busterMock.running >> true
        1 * phantomMock.createPhantomFile(busterConfig, _)
        1 * phantomMock.capturePhantom(busterConfig, _) >> [ok:false, message: "Phantom failed"]

        when:
        task.capturePhantomJs()

        then:
        GradleException ex = thrown()
        ex.message.contains("Phantom failed")
    }

    def "capture phantom fails when buster server is not running"() {
        given:
        busterMock.running >> false

        when:
        task.capturePhantomJs()

        then:
        AssertionError err = thrown()
        err.message.contains("buster")
    }







}

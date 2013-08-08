package org.gradle.plugins.buster

import org.gradle.plugins.buster.internal.browsercapture.BrowserCapturer

class CaptureBrowsersTaskSpec extends AbstractBusterSpecification {
    BrowserCapturer capturerMock
    CaptureBrowsersTask task

    def setup() {
        capturerMock = Mock(BrowserCapturer)
        task = mockCollaborators([browserCapturer: capturerMock], CaptureBrowsersTask.NAME)
    }

    def "browser capture task delegates to browsercapturer"() {
        when:
        task.execute()

        then:
        1 * capturerMock.capture(busterConfig.browsers, busterConfig.captureUrl)
    }


}

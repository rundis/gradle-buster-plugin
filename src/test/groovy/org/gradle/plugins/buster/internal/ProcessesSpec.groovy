package org.gradle.plugins.buster.internal

import org.gradle.plugins.buster.internal.process.Processes
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class ProcessesSpec extends Specification {
    def BUSTER_PROCESS_NAME = 'buster-server'
    def processes = Processes.instance
    def busterProcess

    def 'should find the pid for a running process'() {
        expect:
        processes.pidFor(BUSTER_PROCESS_NAME) != -1
    }

    def 'should be able to check whether a given process is running or not'() {
        expect:
        processes.isRunning(BUSTER_PROCESS_NAME)
        !processes.isRunning('process-name-does-not-exist')
    }

    def 'should kill the process with a given process name'() {
        when: 'killing the process'
        processes.kill(BUSTER_PROCESS_NAME)

        then: 'the process should have exited within 1 second'
        new PollingConditions().within(1, {
            busterProcess.hasExited
        })
    }

    def setup() {
        busterProcess = new ProcessBuilder(BUSTER_PROCESS_NAME).redirectErrorStream(true).start()
        busterProcess.in.close()
        busterProcess.out.close()
        busterProcess.err.close()
    }

    def teardown() {
        busterProcess.destroy()
    }
}

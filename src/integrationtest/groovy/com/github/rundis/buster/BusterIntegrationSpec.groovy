package com.github.rundis.buster

import com.github.rundis.buster.testutils.BusterHelper
import spock.lang.Specification

class BusterIntegrationSpec extends Specification {

    File singleProjectDir = new File("example")
    File multiprojectBuildDir = new File("exampleMulti")
    File exampleFailBuildDir = new File("exampleFail")


    def teardown() {
        if (BusterHelper.running) {
            BusterHelper.stopServer()
        }

    }


    def "run tests for single project"() {
        when: "Running tests"
        runTestFor(singleProjectDir)

        then: "All tests are successfull and buster stopped"
        !BusterHelper.running
    }

    def "run tests for multiproject"() {

        when: "Running tests"
        runTestFor(multiprojectBuildDir)

        then: "All tests are successfull and buster stopped"
        !BusterHelper.running
    }

    def "run failing tests with also cleans up"() {
        when:
        runTestFor(exampleFailBuildDir)

        then:
        Exception ex = thrown()
        !BusterHelper.running
    }



    private def runTestFor(File buildDir) {
        def proc = "sh gradlew clean busterTest -i -b ${buildDir.absolutePath}/build.gradle".execute()
        println proc.text
        if(proc.exitValue() != 0) {
            throw new RuntimeException("Build failed, check logs!")
        }
    }

}

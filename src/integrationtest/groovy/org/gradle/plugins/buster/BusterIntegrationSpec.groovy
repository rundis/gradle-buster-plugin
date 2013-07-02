package org.gradle.plugins.buster

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.GradleBuild
import org.gradle.plugins.buster.internal.Buster
import org.gradle.plugins.buster.internal.Phantom
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


class BusterIntegrationSpec extends Specification {

    File singleProjectDir = new File("example")
    File singleProjectBuildFile = new File(singleProjectDir, "build.gradle")

    File multiprojectBuildDir = new File("exampleMulti")
    File multiprojectBuildFile = new File(multiprojectBuildDir, "build.gradle")


    File exampleFailBuildDir = new File("exampleFail")
    File exampleFailBuildFile = new File(exampleFailBuildDir, "build.gradle")


    def setup() {
        assert singleProjectBuildFile.exists(), "Example build file not found"
        assert multiprojectBuildFile.exists(), "ExampleMulti build file not found"
        assert exampleFailBuildFile.exists(), "Example fail build file not found"
    }

    def teardown() {
        if(Buster.instance.running) {
            Buster.instance.stopServer()
        }
        if(Phantom.instance.running) {
            Phantom.instance.stopServer()
        }

    }


    def "run tests and shut down phantom and buster after for single project"() {
        given:
        Project project = project(singleProjectDir)
        Task test = project.tasks["test"]
        Task cleanUp = project.tasks["cleanUp"]

        when: "Running tests"
        test.execute()

        then: "All tests are successfull and buster and phantom are left running"
        Buster.instance.running
        Phantom.instance.running

        when: "Stopping buster server"
        cleanUp.execute()

        then: "Buster server and phantom are stopped"
        !Buster.instance.running
        !Phantom.instance.running

    }

    def "run tests and shut down phantom and buster after for multiproject"() {
        given:
        Project project = project(multiprojectBuildDir)
        Task test = project.tasks["test"]
        Task cleanUp = project.tasks["cleanUp"]

        when: "Running tests"
        test.execute()

        then: "All tests are successfull and buster and phantom are left running"
        Buster.instance.running
        Phantom.instance.running

        when: "Stopping buster server"
        cleanUp.execute()

        then: "Buster server and phantom are stopped"
        !Buster.instance.running
        !Phantom.instance.running

    }

    def "run failing tests with killOnFailure set cleans up"() {
        given:
        def project = ProjectBuilder.builder().build().with {

            task(type: GradleBuild, 'test') {
                dir = exampleFailBuildDir.absolutePath
                tasks = ['busterTest']
                startParameter.projectProperties['busterKillOnFail']=true
            }
            it
        }
        Task test = project.tasks["test"]


        when:
        test.execute()
        sleep(100)
        then:

        Exception ex = thrown()
        !Buster.instance.running
        !Phantom.instance.running


    }



    Project project(File buildDir) {
        ProjectBuilder.builder().build().with {

            task(type: GradleBuild, 'test') {
                dir = buildDir.absolutePath
                tasks = ['busterTest']
            }

            task(type: GradleBuild, 'cleanUp') {
                dir = buildDir.absolutePath
                tasks = ['stopBusterServer']
            }

            it
        }
    }
}

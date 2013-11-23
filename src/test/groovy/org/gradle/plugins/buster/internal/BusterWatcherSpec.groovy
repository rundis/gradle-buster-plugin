package org.gradle.plugins.buster.internal

import name.pachler.nio.file.Paths
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class BusterWatcherSpec extends Specification {

    BusterJSParser parserMock

    def setup() {
        parserMock = Mock(BusterJSParser)
    }


    def "create watches given directory and subdirectories"() {
        given:

        def listener = { args -> println "Hello" }
        def project = project()
        def testRootPath = new File(project.projectDir, "example/lib")
        testRootPath.mkdirs()

        parserMock.extractGlobPatterns(_) >> [[includes: ['*.js']]]

        def watcher = BusterWatcher.create(project, parserMock, listener)

        expect:
        watcher.keys.values().contains(Paths.get(new File(project.projectDir, "example").absolutePath))
        watcher.keys.values().contains(Paths.get(new File(project.projectDir, "example/lib").absolutePath))
    }


    def "create file triggers pathevent"() {
        given:
        def project = project()
        int listenerInvokeCount = 0
        def listener = { args -> listenerInvokeCount++ }
        def dummyFile = new File(project.projectDir, "dummy.txt")
        def dummyFile2 = new File(project.projectDir, "dummy2.txt")

        parserMock.extractGlobPatterns(_) >> [[includes: ['*.txt']]]

        when:
        def service = Executors.newFixedThreadPool(2)
        Future future = service.submit({
            def watcher = BusterWatcher.create(project, parserMock, listener)
            watcher.metaClass.busterJsConfig = {
                "Dummy glob patterns"
            }
            watcher.processEvents()

            } as Runnable)
        sleep(250)
        dummyFile << "dill"
        dummyFile2 << "dall"

        try {
            future.get(1, TimeUnit.SECONDS)
        } catch (Exception e) {
        }
        service.shutdown()

        then:
        listenerInvokeCount == 1 // throttles events withing 1 second, so should only be one
    }

    def "create directory and then file triggers pathevent"() {
        def project = project()
        File subFolder = new File(project.projectDir, "sub")
        int listenerInvokeCount = 0
        def listener = { args ->
            listenerInvokeCount++
        }
        def dummyFile = new File(subFolder, "dummy.txt")
        parserMock.extractGlobPatterns(_) >> [[includes: ['**']]]

        when:
        def service = Executors.newFixedThreadPool(2)

        Future future = service.submit({
            def watcher = BusterWatcher.create(project, parserMock, listener)
            watcher.metaClass.busterJsConfig = {
                "Dummy glob patterns"
            }
            watcher.processEvents()

        } as Runnable)

        sleep(150)
        project.logger.info("Creating subdirectory")
        subFolder.mkdir()
        sleep(150)
        dummyFile << "dill"

        try {
            future.get(1, TimeUnit.SECONDS)
        } catch (Exception e) {}
        service.shutdown()
        println "number of invocations: " + listenerInvokeCount


        then:
        listenerInvokeCount >= 2 // at least 2 create events, but most likely also 2 modify events !
    }


    private Project project () {
        ProjectBuilder.builder().build().with {
            apply plugin: 'buster'
            it
        }
    }

}

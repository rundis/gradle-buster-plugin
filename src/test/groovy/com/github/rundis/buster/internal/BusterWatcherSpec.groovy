package com.github.rundis.buster.internal

import name.pachler.nio.file.Paths
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class BusterWatcherSpec extends Specification {

    BusterJSParser parserMock
    def service
    def listenerInvokeCount
    def listener

    def setup() {
        parserMock = Stub(BusterJSParser)
        service = Executors.newFixedThreadPool(1)
        listenerInvokeCount = 0
        listener = { args -> listenerInvokeCount++ }
    }

    def cleanup() {
        service.shutdown()
    }


    def "create watches given directory and subdirectories"() {
        given:
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

        def dummyFile = new File(project.projectDir, "dummy.txt")
        def dummyFile2 = new File(project.projectDir, "dummy2.txt")

        parserMock.extractGlobPatterns(_) >> [[includes: ['*.txt']]]

        when:
        Future future = service.submit({
            def watcher = BusterWatcher.create(project, parserMock, listener)
            watcher.metaClass.busterJsConfig = { "Dummy glob patterns" }
            watcher.processEvents()

            } as Runnable)
        sleep(150)
        dummyFile << "dill"
        dummyFile2 << "dall"

        try {
            future.get(1, TimeUnit.SECONDS)
        } catch (Exception e) {
        }

        then:
        listenerInvokeCount == 1 // throttles events
    }

    def "create directory and then file triggers pathevent"() {
        given:
        def project = project()
        File subFolder = new File(project.projectDir, "sub")
        def dummyFile = new File(subFolder, "dummy.txt")
        parserMock.extractGlobPatterns(_) >> [[includes: ['**']]]

        when:
        Future future = service.submit({
            def watcher = BusterWatcher.create(project, parserMock, listener)
            watcher.metaClass.busterJsConfig = { "Dummy glob patterns" }
            watcher.processEvents()

        } as Runnable)

        sleep(150)
        subFolder.mkdir()
        sleep(150)
        dummyFile << "dill"

        try {
            future.get(1, TimeUnit.SECONDS)
        } catch (Exception e) {}

        then:
        listenerInvokeCount >= 2 // at least 2 create events, but most likely also 2 modify events !
    }

    def "changes in buster js should be picked up"() {
        given:
        def project = project()
        def sub1 = new File(project.projectDir, "sub1")
        def sub2 = new File(project.projectDir, "sub2")
        def file1 = new File(sub1, "dill.js")
        def file2 = new File(sub2, "dill2.js")
        parserMock.extractGlobPatterns(_) >> [[includes: ['sub1/*.js']]]

        when: "Running with intitial config"
        sub1.mkdir()
        sub2.mkdir()
        Future future = service.submit({
            def watcher = BusterWatcher.create(project, parserMock, listener)
            watcher.metaClass.busterJsConfig = { "Dummy glob patterns" }
            watcher.processEvents()

        } as Runnable)


        sleep(150)
        file1 << "Hello"
        sleep(250)

        then: "Anything within that config is picked up"
        listenerInvokeCount == 1


        when: "Config changes"
        parserMock = Stub(BusterJSParser)
        file2 << "Hello2"
        sleep(250)

        then: "additional file is picked up"
        parserMock.extractGlobPatterns(_) >> [[includes: ['sub1/*.js', 'sub2/*.js']]]
        listenerInvokeCount == 2
    }


    private Project project () {
        ProjectBuilder.builder().build().with {
            apply plugin: 'com.github.rundis.buster'
            it
        }
    }

}

package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class BusterTestTask extends DefaultTask {
    static NAME = "busterTest"

    File reportsDir = new File(project.buildDir, "test-results")
    File outputFile = new File(reportsDir.path, "bustertests.xml")


    BusterTestTask() {
        dependsOn CapturePhantomTask.NAME
    }

    @TaskAction
    void test() {
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }

        project.exec {
            executable "buster"
            args = ["test", "--reporter", "xml"]
            standardOutput = new FileOutputStream(outputFile)
        }

        logResults()
    }

    private void logResults() {
        def xml = new XmlSlurper().parse(outputFile)
        xml.testsuite.each { suite ->
            logger.info "Suite: ${suite.@name}, testcases: ${suite.testcase.size()}"
        }
    }


}

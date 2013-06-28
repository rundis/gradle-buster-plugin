package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
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
        if(outputFile.exists()) {outputFile.delete()}


        def busterConfig = project.convention.getPlugin(BusterPluginConvention).busterConfig

        def stdOut = new ByteArrayOutputStream()

        def execResult = project.exec {
            executable "buster"
            args = ["test", "--reporter", "xml", "--server", "http://localhost:${busterConfig.port}"]
            standardOutput = stdOut
            ignoreExitValue = true
        }

        writeXmlReport(stdOut)
        execResult.exitValue == 0 ? logResults() : logTestErrors()
    }

    private void writeXmlReport(OutputStream outXml) {
        def testResults = outXml.toString()
        if(!testResults.contains("xml")) {
            throw new GradleException("Test execution failure: ${testResults}")
        }
        outputFile << testResults
    }


    private void logResults() {
        def xml = new XmlSlurper().parse(outputFile)
        xml.testsuite.each { suite ->
            logger.info "Suite: ${suite.@name}, testcases: ${suite.testcase.size()}"
        }
    }

    private void logTestErrors() {
        def xml = new XmlSlurper().parse(outputFile)
        def errMsg = "Test errors:\n\n" + xml.testsuite.findAll { it.@failures || it.@errors}.collect {suite ->
            "Suite: ${suite.@name}, failures: ${suite.@failures}, errors: ${suite.@errors}"
        }.join("\n")

        logger.info "Test results:"
        logger.info outputFile.text

        throw new GradleException(errMsg)
    }


}

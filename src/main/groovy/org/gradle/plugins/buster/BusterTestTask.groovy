package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.internal.BusterTestingService
import org.gradle.process.ExecResult

class BusterTestTask extends DefaultTask {
    static NAME = "busterTest"

    File reportsDir = new File(project.buildDir, "busterTest-results")
    File outputFile = new File(reportsDir.path, "bustertests.xml")

    BusterTestingService service


    protected BusterTestTask setService(BusterTestingService service) {
        this.service = service
        this
    }

    @TaskAction
    void test() {
        setupReportDir()

        service.prepareForTest()
        ExecResult execResult = executeTests()
        service.tearDownAfterTest()

        execResult.exitValue == 0 ? logResults() : logTestErrors()
    }

    private void setupReportDir() {
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }
        if (outputFile.exists()) {
            outputFile.delete()
        }
    }

    private ExecResult executeTests() {
        def stdOut = new ByteArrayOutputStream()
        def busterArgs = busterArgs()
        def execResult = project.exec {
            executable "buster"
            args = busterArgs
            standardOutput = stdOut
            ignoreExitValue = true
        }

        writeXmlReport(stdOut)
        execResult
    }

    private List busterArgs() {
        def busterConfig = project.buster
        def busterArgs = ["test", "--reporter", "xml", "--server", busterConfig.serverUrl]
        if (busterConfig.configFile) {
            busterArgs += ["--config", busterConfig.configFile.absolutePath]
        }
        busterArgs
    }

    private void writeXmlReport(OutputStream outXml) {
        def testResults = outXml.toString()
        if (!testResults.contains("xml")) {
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
        def errMsg = "Test errors:\n\n" + xml.testsuite.findAll { it.@failures || it.@errors }.collect { suite ->
            "Suite: ${suite.@name}, failures: ${suite.@failures}, errors: ${suite.@errors}"
        }.join("\n")

        logger.info "Test results:"
        logger.info outputFile.text

        throw new GradleException(errMsg)
    }

}

package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.internal.BusterJSParser
import org.gradle.plugins.buster.internal.BusterTestingService
import org.gradle.plugins.buster.internal.JUnitTestXml

class BusterTestTask extends DefaultTask {
    static NAME = "busterTest"

    File reportsDir = new File(project.buildDir, "busterTest-results")

    @OutputFile
    File outputFile = new File(reportsDir.path, "bustertests.xml")

    BusterTestingService service


    protected BusterTestTask setService(BusterTestingService service) {
        this.service = service
        this
    }

    @InputFiles
    public List<File> getInputFiles() {
        File configFile = project.buster.resolveConfigFile(project)

        if(!configFile) {
            project.logger.warn("No buster config file (found), unable to determine inputs for task")
            return []
        }

        def config = configFile.text
        def jsFilesGlobs = new BusterJSParser().parseGlobPatterns(config)

        project.files(configFile,
                project.fileTree (dir: configFile.parent, include: jsFilesGlobs)
        ).files.collect{it}

    }

    @TaskAction
    void test() {
        setupReportDir()

        service.prepareForTest()

        try {
            executeTests()
        } finally {
            service.tearDownAfterTest()
        }

    }

    private void setupReportDir() {
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }
        if (outputFile.exists()) {
            outputFile.delete()
        }
    }

    private void executeTests() {
        def stdOut = new ByteArrayOutputStream()
        def busterArgs = busterArgs()
        def execResult = project.exec {
            executable project.buster.testExecutablePath
            args = busterArgs
            standardOutput = stdOut
            ignoreExitValue = true
        }

        new JUnitTestXml(stdOut.toString(), logger)
                .writeFile(outputFile)
                .validateNoErrors()
                .logResults()

    }

    private List busterArgs() {
        def busterConfig = project.buster
        def busterArgs = ["--reporter", "xml", "--server", busterConfig.serverUrl]
        if (busterConfig.configFile) {
            busterArgs += ["--config", busterConfig.configFile.absolutePath]
        }
        busterArgs
    }

}

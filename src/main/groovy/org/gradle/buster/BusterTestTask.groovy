package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class BusterTestTask extends DefaultTask {

    @TaskAction
    void busterTest() {
        def reportsDir = new File(project.buildDir, "reports")
        def outputFile = new File(reportsDir.path, "bustertestresults.xml")
        if(!reportsDir.exists()) {
            reportsDir.mkdirs()
        }

        project.exec {
            executable "buster"
            args = ["test", "--reporter", "xml"]
            standardOutput = new FileOutputStream(outputFile)

        }


    }

}

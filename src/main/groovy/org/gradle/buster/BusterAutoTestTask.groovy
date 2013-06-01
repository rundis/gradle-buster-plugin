package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class BusterAutoTestTask extends DefaultTask {

    static NAME = "busterAutoTest"

    BusterAutoTestTask() {
        dependsOn CapturePhantomTask.NAME
    }

    @TaskAction
    void test() {
        project.exec {
            executable "buster"
            args = ["autotest"]
        }
    }
}

package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class BusterTestTask extends DefaultTask{

    @TaskAction
    void test() {
        "buster test".execute()
    }
}

package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class ServerTask extends DefaultTask{

    @TaskAction
    void startServer() {
        println "Starting buster server"


        "buster server".execute()

    }
}

package org.gradle.plugins.buster

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import org.gradle.plugins.buster.config.BusterConfig
import org.gradle.plugins.buster.internal.Buster
import org.gradle.plugins.buster.internal.browsercapture.BrowserCapturer

class BusterPlugin implements Plugin<Project>{

    Project project
    BrowserCapturer browserCapturer

    @Override
    void apply(Project project) {
        this.project = project
        this.browserCapturer = new BrowserCapturer(project.logger)

        project.tasks.create (BusterTestTask.NAME, BusterTestTask)

        project.tasks.create (BusterAutoTestTask.NAME, BusterAutoTestTask)
                .addShutdownHook {
                    project.logger.info "Release captured browsers shutdownhook"
                    browserCapturer.shutdown()
                }


        project.gradle.taskGraph.addTaskExecutionListener(new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {
                println "$project beforetaskListener ${task.name}"
            }

            @Override
            void afterExecute(Task task, TaskState taskState) {
                println "$project aftertaskListener ${task.name}"
            }
        })


        project.gradle.taskGraph.beforeTask {Task task ->
            println "Beforetask ${task.name}"
            if(task.name in [BusterTestTask.NAME, BusterAutoTestTask.NAME]) {
                println "About to start buster...."
                if(Buster.instance.running) {
                    throw new GradleException("There is already a running buster instance")
                }
                project.logger.info "Starting buster server"
                BusterConfig busterConfig = project.extensions.buster
                Map cmdResult = Buster.instance.startServer(busterConfig)
                if(!cmdResult.ok) {
                    throw new GradleException("Error starting Buster Server: $cmdResult.message")
                }
                project.logger.info cmdResult.message

                project.logger.info "Capturing browsers"
                browserCapturer.capture(busterConfig.browsers, busterConfig.captureUrl)
            }

        }

        project.gradle.taskGraph.afterTask {Task task, TaskState state ->
            println "aftertask ${task.name}"
            if(task.name == BusterTestTask.NAME) {
                project.logger.info "Releasing captured browsers"
                browserCapturer.shutdown()
                if(Buster.instance.running) {
                    project.logger.info "Stopping buster server"
                    Buster.instance.stopServer()
                }
            }

        }

        project.extensions.create("buster", BusterConfig, project)
    }
}

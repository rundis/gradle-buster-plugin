package org.gradle.buster

import org.gradle.api.Plugin
import org.gradle.api.Project


class BusterPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.tasks.add "startServer", ServerTask
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

package org.gradle.plugins.buster

import org.gradle.api.Plugin
import org.gradle.api.Project


class BusterPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.convention.plugins.put 'buster', new BusterPluginConvention(project)

        project.tasks.create StartBusterServerTask.NAME, StartBusterServerTask
        project.tasks.create (StopBusterServerTask.NAME, StopBusterServerTask).dependsOn StopPhantomTask.NAME
        project.tasks.create (CapturePhantomTask.NAME, CapturePhantomTask).dependsOn StartBusterServerTask.NAME
        project.tasks.create StopPhantomTask.NAME, StopPhantomTask
        project.tasks.create (BusterTestTask.NAME, BusterTestTask).dependsOn CapturePhantomTask.NAME
        project.tasks.create (BusterAutoTestTask.NAME, BusterAutoTestTask).dependsOn CapturePhantomTask.NAME
    }
}

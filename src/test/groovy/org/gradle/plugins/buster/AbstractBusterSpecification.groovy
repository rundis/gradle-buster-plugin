package org.gradle.plugins.buster

import org.gradle.api.Project
import org.gradle.plugins.buster.internal.Buster
import org.gradle.plugins.buster.internal.BusterConfig
import org.gradle.plugins.buster.internal.Phantom
import org.gradle.plugins.buster.BusterPluginConvention
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

abstract class AbstractBusterSpecification extends Specification {
    Project project
    Buster busterMock
    Phantom phantomMock
    BusterConfig busterConfig

    def setup() {
        project = project()
        busterMock = Mock(Buster)
        phantomMock = Mock(Phantom)
        busterConfig = project.convention.getPlugin(BusterPluginConvention).busterConfig
    }

    protected def mockCollaborators(Map collaborators, String taskName) {
        def task = project.tasks[taskName]
        collaborators.each {key, value ->
            task[key] = value
        }
        task
    }

    protected Project project() {
        ProjectBuilder.builder().build().with {
            apply plugin: 'buster'
            it
        }
    }
}

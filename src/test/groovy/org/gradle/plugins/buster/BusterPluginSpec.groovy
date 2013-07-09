package org.gradle.plugins.buster

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


class BusterPluginSpec extends Specification {


    def "specifying simple properties"() {
        given:
        def aPort = 1112
        def busterJs = new File("buster.js")
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                port = aPort
                configFile = busterJs
            }
            it
        }

        expect:
        project.buster.with {
            port == aPort
            configFile == configFile
        }


    }


    def "specifying browsers to capture"() {
        given:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                browsers {
                    firefox
                    safari
                    ie {remote = true}
                }
            }
            it
        }

        expect:
        project.buster.browsers.firefox
        project.buster.browsers.size() == 3
        project.buster.browsers.ie.remote
    }



}

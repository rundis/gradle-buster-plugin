package org.gradle.plugins.buster

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


class BusterPluginSpec extends Specification {


    def "specifying simple properties"() {
        when:
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

        then:
        project.buster.with {
            port == aPort
            configFile == configFile
        }


    }


    def "specifying browsers to capture"() {
        when:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                browsers {
                    firefox
                    safari
                }
            }
            it
        }

        then:
        project.buster.browsers.firefox
        project.buster.browsers.size() == 2
    }

    def "unknown browser throws exception"() {
        when:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                browsers {
                    myCustomBrowser
                }
            }
            it
        }

        then:
        Exception e = thrown()

    }



}

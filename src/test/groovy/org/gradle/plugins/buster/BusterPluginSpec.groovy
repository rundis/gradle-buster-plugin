package org.gradle.plugins.buster

import org.gradle.api.Project
import org.gradle.plugins.buster.config.BusterConfig
import org.gradle.plugins.buster.internal.browsercapture.SupportedBrowser
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll


class BusterPluginSpec extends Specification {

    def cleanup() {
        System.setProperty(BusterConfig.BUSTER_EXECUTABLES_SYS_PROP, "")
    }


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

    def "dynamically assign port"() {
        when:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                port = new ServerSocket(0).getLocalPort()

            }
            it
        }

        then:
        project.buster.port > 0
    }

    @Unroll
    def "let the plugin dynamically assign port"() {
        when:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                port = 0

            }
            it
        }
        triggerResolve.call(project)


        then:
        project.buster.port > 0
        project.buster.port != 1111 // possible but unlikely enough that we can deal with it!


        where:
        triggerResolve << [
            {p -> p.buster.resolvedPort},
            {p -> p.buster.captureUrl},
            {p -> p.buster.serverUrl}
        ]

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


    def "no browser specified adds phantom (local) and warns user"() {
        when:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'
            it
        }

        then:
        project.buster.browsers[SupportedBrowser.PHANTOMJS.shortName]
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

    def "buster executables path specified"() {
        given:
        String path = "myPath/subPath"

        when:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                busterExecutablesPath = path
            }
            it
        }


        then:
        project.buster.testExecutablePath == "${path}/buster-test"
        project.buster.serverExecutablePath == "${path}/buster-server"
    }

    def "buster executables system property set"() {
        given:
        String path = "myPath/subPath"
        String systemPropPath = "otherPath/dufus"
        System.setProperty(BusterConfig.BUSTER_EXECUTABLES_SYS_PROP, systemPropPath)


        when:
        Project project = ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                busterExecutablesPath = path
            }
            it
        }


        then:
        project.buster.testExecutablePath == "${systemPropPath}/buster-test"
        project.buster.serverExecutablePath == "${systemPropPath}/buster-server"
    }





}

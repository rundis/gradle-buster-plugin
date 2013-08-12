package org.gradle.plugins.buster

import org.gradle.api.Project
import org.gradle.plugins.buster.internal.browsercapture.BrowserDriverUtil
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BusterSetupWebDriversTaskSpec extends Specification {
    BrowserDriverUtil driverUtil

    def setup() {
        driverUtil = Mock(BrowserDriverUtil)
    }

    def "do setup of chrome when driver specified and no driver executable sys property set"() {
        given:
        Project project = projectWithChrome()
        BusterSetupWebDriversTask task = project.tasks.findByName(BusterSetupWebDriversTask.NAME)
        task.driverUtil = driverUtil

        when:
        task.execute()



        then:
        1 * driverUtil.setupChromeDriver()
    }

    def "skip setup if chrome but sys property set"() {
        given:
        Project project = projectWithChrome()
        BusterSetupWebDriversTask task = project.tasks.findByName(BusterSetupWebDriversTask.NAME)
        task.driverUtil = driverUtil

        driverUtil.chromeDriverSetup >> true

        when:
        task.execute()


        then:
        !task.didWork
    }


    def "skip setup if not chrome browser specied"() {
        Project project =  ProjectBuilder.builder().build().with {
            apply plugin: 'buster'
            it
        }
        BusterSetupWebDriversTask task = project.tasks.findByName(BusterSetupWebDriversTask.NAME)
        task.driverUtil = driverUtil

        driverUtil.chromeDriverSetup >> true

        when:
        task.execute()


        then:
        !task.didWork
    }




    private Project projectWithChrome() {
        ProjectBuilder.builder().build().with {
            apply plugin: 'buster'

            buster {
                browsers {
                    chrome
                }
            }
            it
        }
    }

}

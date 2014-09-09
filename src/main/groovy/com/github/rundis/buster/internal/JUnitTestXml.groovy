package com.github.rundis.buster.internal

import groovy.util.slurpersupport.GPathResult
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger

class JUnitTestXml {
    private final String xml
    private final GPathResult gPath
    private final Logger logger


    JUnitTestXml(String xml, Logger logger) {
        if(!xml) { throw new GradleException("Empty test results")}
        if(!xml.toLowerCase().contains("xml")) {
            throw new GradleException("Test execution failure: ${xml}")
        }

        this.xml = xml

        try {
            this.gPath = new XmlSlurper().parseText(xml)
        } catch (Exception e) {
            throw new GradleException("Invalid test xml:\n${xml}", e)
        }
        this.logger = logger
    }



    JUnitTestXml writeFile(File file) {
        file << xml
        this
    }

    JUnitTestXml validateNoErrors() {
        def errors = gPath.testsuite.findAll { it.@failures != "0" || it.@errors !="0" }
        if(errors.size() < 1) { return this}

        def errMsg = "Test errors:\n\n" + errors.collect { suite ->
            "Suite: ${suite.@name}, failures: ${suite.@failures}, errors: ${suite.@errors}"
        }.join("\n")

        throw new GradleException(errMsg)
    }


    JUnitTestXml logResults() {
        gPath.testsuite.each { suite ->
            logger.info "Suite: ${suite.@name}, tests: ${suite.@tests}"
        }
        return this
    }
}

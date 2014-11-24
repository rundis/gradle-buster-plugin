package com.github.rundis.buster.internal

import groovy.util.slurpersupport.GPathResult
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger

class JUnitTestXml {
    private String[] xml
    private def parseResults
    private Logger logger

    JUnitTestXml(String busterXMLOutput, Logger logger) {
        if(!busterXMLOutput) { throw new GradleException("Empty test results")}

        this.logger = logger

        this.xml = collectResults(busterXMLOutput)

        this.parseResults = []

        this.xml.each {
            try {
                this.parseResults << new XmlSlurper().parseText(it)
            } catch (Exception e) {
                throw new GradleException("Invalid test xml:\n${it}", e)
            }
        }

    }

    private static def collectResults(String result) {
        def results = []

        result.split($/<\?xml/$).each {
            if( it ) {
                // we have to give back what we took
                results << "<?xml${it}"
            }
        }

        results
    }

    JUnitTestXml writeReports(File outputDir) {
        xml.eachWithIndex { String entry, int i ->
            File out = new File(outputDir, "bustertests${{->if(0<i)i}}.xml")
            out << entry
        }
        this
    }

    JUnitTestXml validateNoErrors() {
        parseResults.each {
            validateNoErrorsSingleResult((GPathResult)it)
        }
        this
    }

    private validateNoErrorsSingleResult(GPathResult gPath) {
        def errors = gPath.testsuite.findAll { it.@failures != "0" || it.@errors != "0" }
        if (errors.isEmpty()) {
            return
        }

        def errMsg = "Test errors:\n\n" + errors.collect { suite ->
            "Suite: ${suite.@name}, failures: ${suite.@failures}, errors: ${suite.@errors}"
        }.join("\n")

        throw new GradleException(errMsg)
    }


    JUnitTestXml logResults() {
        parseResults.each { gPath ->
            gPath.testsuite.each { suite ->
                logger.info "Suite: ${suite.@name}, tests: ${suite.@tests}"
            }
        }
        return this
    }
}

package org.gradle.plugins.buster.internal

import spock.lang.Specification


class BusterJSParserSpec extends Specification {

    BusterJSParser parser = new BusterJSParser()

    def "extract globs grouped by rootPath"() {
        given:
        def config = """
            var config = module.exports;

            config["My tests"] = {
                environment: "browser",
                sources: [
                        "lib/*.js",
                        'dill/**/*.js',
                        '!dill/mamma/*.js'],
                tests: ["test/**/*.tests.js"]
            };
            config["My tests2"] = {
                rootPath:"../",
                environment: "browser",
                sources:["lib/rundis.js"]
            };
            config["My tests3"] = {
                rootPath:"../",
                environment: "browser",
                sources:["lib/rundis2.js"]
            };
        """


        when:
        def globPatterns = parser.extractGlobPatterns(config)

        then:
        globPatterns[0].rootPath == ""
        globPatterns[0].includes.containsAll(['lib/*.js', 'dill/**/*.js', 'test/**/*.tests.js'])
        globPatterns[0].excludes.containsAll(['dill/mamma/*.js'])
        globPatterns[1].includes.containsAll(['lib/rundis.js', 'lib/rundis2.js'])
    }
}

package org.gradle.plugins.buster.internal

import spock.lang.Specification


class BusterJSParserSpec extends Specification {

    BusterJSParser parser = new BusterJSParser()

    def "parse handles super simple config"() {
        given:
        def config = """
            var config = module.exports;

            config["My tests"] = {
                environment: "browser", // or "node"
                sources: [
                    "lib/*.js",
                    'dill/**/*.js'],
                tests: ["test/**/*.tests.js"]
            };
            config["My tests2"] = {
                environment: "browser", // or "node"
                sources:["lib/rundis.js"]
            };
        """

        when:
        def globPatterns = parser.parseGlobPatterns(config)

        then:
        globPatterns.containsAll( ["lib/*.js", "dill/**/*.js", "test/**/*.tests.js", "lib/rundis.js"])
    }
}

package org.gradle.plugins.buster.internal

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


class GlobMatcherSpec extends Specification {

    @Shared
    def ROOTPATH = getClass().getResource("").toURI().toString()


    @Unroll("#path")
    def "matches if any glob mattern matches"() {
        given:
        def matcher = globMatcher(["*.jsf", "**/*.js", "lib/**/*.txt"])

        expect:
        matcher.matches(path) == matches


        where:
        path                     | matches
        "${ROOTPATH}dill.jsf"    | true
        "${ROOTPATH}dill.jsa"    | false
        "${ROOTPATH}sub/dill.js" | true
        "dall/dill.js"           | true
        "lib/jalla/dall.txt"     | true
        "lib/dall.txt"           | true
        "lib/dall.txts"          | false
        "dull/dall.txt"          | false
    }

    def "handles double star like node.js glob matching does"() {
        given:
        def matcher = globMatcher(["lib/**/*.js"])

        expect:
        matcher.matches("lib/dill.js")
        matcher.matches("lib/dall/dill.js")
        !matcher.matches("lib/dall.txt")
    }

    def "adds pathseparator to end if not given"() {
        given:
        def matcher = new GlobMatcher("/usr/local/project/dill", ["**/*.js"])

        expect:
        matcher.rootPath == "/usr/local/project/dill/"
    }


    private GlobMatcher globMatcher(List globPatterns) {
        new GlobMatcher(ROOTPATH, globPatterns)
    }
}

package org.gradle.plugins.buster.internal


class BusterJSParser {

    static pattern = /(?s)[sources|src|tests|testHelpers|specHelpers|specs|libs|deps|resources]\s*:\s*\[(.*?)\]/

    List<String> parseGlobPatterns(String busterConfig) {
        def m = busterConfig =~ pattern
        m.collect {
            it[1].split(",").collect { it.trim().replaceAll(/\"|\'/, "") }
        }.flatten().unique()
    }
}

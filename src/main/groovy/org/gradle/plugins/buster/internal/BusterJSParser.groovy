package org.gradle.plugins.buster.internal


class BusterJSParser {

    /**
     * @param config Buster configuration
     * @return List of maps. Each entry has rootPath, includes and excludes
     */
    List<Map> extractGlobPatterns(String config) {
        extractConfigGroups(config).collect{String group ->
            [rootPath: extractRootPath(group)] +
            toIncludesExcludes(extractGlobs(group))
        }.groupBy{it.rootPath}.collect {k, v ->
            [rootPath: k, includes:v.sum{it.includes}, excludes: v.sum{it.excludes}]
        }
    }



    private List extractConfigGroups(String config) {
        def m = config =~ /(?s)\]\s=\s*\{\s*(.*?)\};/
        m.collect{it[1]}
    }

    private String extractRootPath(String configGroup) {
        def m = configGroup =~ /rootPath\s*:\s*[\"|\'](.*)[\"|\']\s*,/
        m ? m[0][1] : ""
    }

    private Map toIncludesExcludes(List globs) {
        globs.inject([includes:[], excludes:[]]) {acc, val ->
            val.startsWith("!") ? acc.excludes.add(val.substring(1)) : acc.includes.add(val)
            acc
        }
    }

    private List extractGlobs(String configGroup) {
        def pattern = /(?s)[sources|src|tests|testHelpers|specHelpers|specs|libs|deps|resources]\s*:\s*\[(.*?)\]/
        def m = configGroup =~ pattern
        m.collect {
            it[1].split(",").collect { it.trim().replaceAll(/\"|\'/, "") }
        }.flatten().unique()
    }

}

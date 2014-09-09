package com.github.rundis.buster.internal

import org.apache.tools.ant.types.selectors.SelectorUtils

class GlobMatcher {
    private final String rootPath
    private final List<Map> globPatterns

    GlobMatcher(String rootPath, List<Map> globPatterns) {
        this.rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/"
        this.globPatterns = globPatterns
    }

    boolean matches(String path) {
        def candidate = (path - rootPath)
        globPatterns.find{group ->
            group.includes.find{
                SelectorUtils.matchPath(it, candidate)
            }
        }
    }
}

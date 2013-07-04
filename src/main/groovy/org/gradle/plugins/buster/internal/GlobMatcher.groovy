package org.gradle.plugins.buster.internal

import org.apache.tools.ant.types.selectors.SelectorUtils

class GlobMatcher {
    private final String rootPath
    private final List<String> globPatterns

    GlobMatcher(String rootPath, List<String> globPatterns) {
        this.rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/"
        this.globPatterns = globPatterns
    }

    boolean matches(String path) {
        def candidate = (path - rootPath)
        globPatterns.find{SelectorUtils.matchPath(it, candidate)}
    }
}

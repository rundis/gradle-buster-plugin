package org.gradle.plugins.buster.internal

import sun.nio.fs.Globs

import java.util.regex.Pattern


class GlobMatcher {
    private final String rootPath
    private final List<Pattern> globPatterns


    GlobMatcher(String rootPath, List<String> globPatterns) {
        this.rootPath = rootPath
        this.globPatterns = globPatterns.collect{
            def pattern = it.replaceAll("\\**/", "**")
            Pattern.compile(windows ? Globs.toWindowsRegexPattern(pattern) : Globs.toUnixRegexPattern(pattern))
        }
    }

    boolean matches(String path) {
        def candidate = (path - rootPath)
        globPatterns.find{it.matcher(candidate).matches()}
    }


    private static boolean isWindows() {
        System.properties['os.name'].contains('windows')
    }


}

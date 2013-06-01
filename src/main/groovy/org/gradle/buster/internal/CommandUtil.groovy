package org.gradle.buster.internal


class CommandUtil {

    static String executePiped(String pipedCommands) {
        Process proc = pipedCommands.tokenize( '|' ).inject( null ) { p, c ->
            if(p) {
                p | c.execute()
            } else {
                c.execute()
            }
        }

        proc.text
    }
}

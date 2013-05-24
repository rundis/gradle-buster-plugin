package org.gradle.buster.internal


class Buster {

    static boolean isRunning() {
        pid
    }

    static String getPid() {
        def command = "ps aux|grep buster-server|grep node|awk {print\$2}"

        Process proc = command.tokenize( '|' ).inject( null ) { p, c ->
            if( p )
                p | c.execute()
            else
                c.execute()
        }

        proc.text
    }

    static void startServer() {
        def sout = new StringBuffer(), serr = new StringBuffer()
        def proc = "buster server".execute()

        // TODO: Would be nice to check the output without blocking...
    }

    static void stopServer() {
        if(isRunning()) {
            "kill -9 ${getPid()}".execute()
        }
    }
}

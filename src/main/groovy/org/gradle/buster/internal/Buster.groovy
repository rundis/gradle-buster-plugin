package org.gradle.buster.internal


class Buster {

    static boolean isRunning() {
        pid
    }

    static String getPid() {
        def command = "ps aux|grep buster-server|grep node|awk {print\$2}"

        Process proc = command.tokenize( '|' ).inject( null ) { p, c ->
            if(p) {
                p | c.execute()
            } else {
                c.execute()
            }
        }

        proc.text
    }

    static Map startServer() {
        def proc = new ProcessBuilder("buster", "server").redirectErrorStream(true).start()
        def out = proc.in.newReader().readLine()

        proc.in.close()
        proc.out.close()
        proc.err.close()


        [ok: out.contains("running"), message: out]
    }

    static void stopServer() {
        if(isRunning()) {
            "kill -9 ${getPid()}".execute()
        }
    }
}

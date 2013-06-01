package org.gradle.buster.internal

import static org.gradle.buster.internal.CommandUtil.executePiped



class Buster {

    static boolean isRunning() { pid }

    static String getPid() {
        executePiped "ps aux|grep buster-server|grep node|awk {print\$2}"
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
            "kill -9 ${pid}".execute()
        }
    }
}

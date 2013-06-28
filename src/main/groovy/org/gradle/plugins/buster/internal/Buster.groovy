package org.gradle.plugins.buster.internal

@Singleton
class Buster {

    boolean isRunning() { pid }

    String getPid() {
        org.gradle.plugins.buster.internal.CommandUtil.executePiped "ps aux|grep buster-server|grep node|awk {print\$2}"
    }

    Map startServer(BusterConfig busterConfig) {
        def proc = new ProcessBuilder("buster", "server", "-p", busterConfig.port.toString()).redirectErrorStream(true).start()
        def out = proc.in.newReader().readLine()

        proc.in.close()
        proc.out.close()
        proc.err.close()


        [ok: out.contains("running"), message: out]
    }

    void stopServer() {
        if(isRunning()) {
            "kill -9 ${pid}".execute()
        }
    }
}

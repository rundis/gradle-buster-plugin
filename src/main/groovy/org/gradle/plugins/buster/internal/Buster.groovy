package org.gradle.plugins.buster.internal

@Singleton
class Buster {
    def BUSTER_PROCESS_NAME = 'buster-server'

    boolean isRunning() {
        Processes.instance.isRunning(BUSTER_PROCESS_NAME)
    }

    String getPid() {
        Processes.instance.pidFor(BUSTER_PROCESS_NAME)
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
        if (isRunning()) {
            Processes.instance.kill(BUSTER_PROCESS_NAME)
        }
    }
}

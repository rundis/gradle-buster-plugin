package com.github.rundis.buster.testutils



class BusterHelper {
    static BUSTER_PROCESS_NAME = 'buster-server'

    static boolean isRunning() {
        Processes.instance.isRunning(BUSTER_PROCESS_NAME)
    }

    static stopServer() {
        if (isRunning()) {
            Processes.instance.kill(BUSTER_PROCESS_NAME)
        }
    }
}

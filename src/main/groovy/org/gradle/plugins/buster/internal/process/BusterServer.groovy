package org.gradle.plugins.buster.internal.process

import org.gradle.api.GradleException
import org.gradle.plugins.buster.config.BusterConfig


class BusterServer {

    private final Process process
    private boolean stopped


    BusterServer(Process process) {
        this.process = process
    }

    static BusterServer start(BusterConfig config) {
        def proc = new ProcessBuilder(
                config.serverExecutablePath,
                "-p",
                config.resolvedPort.toString()
        ).redirectErrorStream(true).start()

        def out = proc.in.newReader().readLine()

        proc.in.close()
        proc.out.close()
        proc.err.close()

        if(!out.contains("running")) {
            throw new GradleException("Error starting buster server (on port ${config.resolvedPort}): $out")
        }

        return new BusterServer(proc)
    }

    void stop() {
        if(stopped) return

        try {
            process.destroy()
            stopped = true
        } catch(Exception e) {
            throw new GradleException("Error stopping buster server", e)
        }
    }

    boolean isStopped() {return stopped}

}

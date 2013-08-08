package org.gradle.plugins.buster.internal


import org.gradle.plugins.buster.config.BusterConfig

@Singleton
class Phantom {
    def PHANTOMJS_PROCESS_NAME = 'phantomjs'

    boolean isRunning() {
        Processes.instance.isRunning(PHANTOMJS_PROCESS_NAME)
    }

    String getPid() {
        Processes.instance.pidFor(PHANTOMJS_PROCESS_NAME)
    }

    Map capturePhantom(BusterConfig busterConfig, File phantomFile) {
        def proc = new ProcessBuilder("phantomjs", phantomFile.path).redirectErrorStream(true).start()
        def out = proc.in.newReader().readLine()
        proc.in.close()
        proc.out.close()
        proc.err.close()

        [ok: out.contains("captured"), message: out]
    }

    void stopServer() {
        if (isRunning()) {
            Processes.instance.kill(PHANTOMJS_PROCESS_NAME)
        }
    }

    void createPhantomFile(BusterConfig busterConfig, File phantomFile) {
        String phantomSetup = """
        var system = require('system'),
        captureUrl = 'http://localhost:${busterConfig.port}/capture';
        if (system.args.length==2) {
            captureUrl = system.args[1];
        }

        phantom.silent = false;
        var page = require('webpage').create();

        page.open(captureUrl, function(status) {

           if (status !== 'success') {
               console.log('Phantom failed to connect to: ' + captureUrl);
               phantom.exit(1);
           } else {
               console.log('Phantom captured to url: ' + captureUrl);
           }
           page.onConsoleMessage = function (msg, line, id) {
               var fileName = id.split('/');
               // format the output message with filename, line number and message
               // weird gotcha: phantom only uses the first console.log argument it gets :(
               console.log(fileName[fileName.length-1]+', '+ line +': '+ msg);
           };

           page.onAlert = function(msg) {
               console.log(msg);
           };

        });

        """

        phantomFile << phantomSetup
    }

}

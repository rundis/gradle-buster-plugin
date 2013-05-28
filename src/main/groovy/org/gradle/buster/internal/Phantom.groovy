package org.gradle.buster.internal


class Phantom {

    static boolean isRunning() {
        pid
    }

    static String getPid() {
        def command = "ps aux|grep phantomjs|head -1|awk {print\$2}"

        Process proc = command.tokenize( '|' ).inject( null ) { p, c ->
            if( p )
                p | c.execute()
            else
                c.execute()
        }

        proc.text
    }

    static Map capturePhantom(File phantomFile) {
        def proc = new ProcessBuilder("phantomjs", phantomFile.path).redirectErrorStream(true).start()


        sleep(1000)

        // TODO : this blocks and absolutely no output received from phantom
        //def out = proc.in.newReader().readLine()


        proc.in.close()
        proc.out.close()
        proc.err.close()


        [ok: true, message: "Phantom running"]
    }

    static void stopServer() {
        if(isRunning()) {
            "kill -9 ${getPid()}".execute()
        }
    }

    static void createPhantomFile(File phantomFile) {
        String phantomSetup = """
        var system = require('system'),
        captureUrl = 'http://localhost:1111/capture';
        if (system.args.length==2) {
            captureUrl = system.args[1];
        }

        var fs = require('fs');
        fs.write('/dev/stdout', 'hello world\n', 'w');
        phantom.exit(1);


        phantom.silent = false;

        var page = require('webpage').create();

        page.open(captureUrl, function(status) {
            if(!phantom.silent) {
                if (status !== 'success') {
                    console.log('phantomjs failed to connect');
                    phantom.exit(1);
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
            }
        });

        """

        phantomFile << phantomSetup
    }

}

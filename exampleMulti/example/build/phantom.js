
        var system = require('system'),
        captureUrl = 'http://localhost:1111/capture';
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

        
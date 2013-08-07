package org.gradle.plugins.buster.internal

import org.hyperic.sigar.Sigar
import org.hyperic.sigar.SigarException

@Singleton
class Processes {
    static SIGKILL = 9
    static Sigar sigar

    static {
        new Natives().unpackNativeLibraries()
        sigar = new Sigar()
    }

    def isRunning(def processName) {
        pidFor(processName) != -1
    }

    def kill(def processName) {
        def pid = pidFor(processName)

        if (pid != -1) {
            sigar.kill(pid, SIGKILL)
        }
    }

    def pidFor(def processName) {
        def pids = sigar.procList
        def pid = pids.toList().find { pid ->
            try {
                isProcessInPidsArguments(pid, processName)
            } catch (SigarException e) {
                // Process started by another user -> no access
                false
            }
        }

        // Return -1 if pid not found for process name
        pid ?: -1
    }

    def isProcessInPidsArguments(def pid, def processName) {
        sigar.getProcArgs(pid).find { entry ->
            entry.contains(processName)
        } != null
    }

    static class Natives {
        def unpackNativeLibraries() {
            def tempDirectory = createTempDirectory()

            [
                    'libsigar-amd64-linux-1.6.4.so',
                    'libsigar-amd64-solaris-1.6.4.so',
                    'libsigar-universal64-macosx-1.6.4.dylib',
                    'libsigar-x86-linux-1.6.4.so'
            ].each { nativeLibraryName ->
                unpackNativeLibrary(nativeLibraryName, tempDirectory)
            }

            System.setProperty('org.hyperic.sigar.path', tempDirectory.absolutePath)
        }

        def createTempDirectory() {
            def tempFile = File.createTempFile('native', 'lib')
            tempFile.delete()
            tempFile.mkdirs()
            tempFile.deleteOnExit()

            tempFile
        }

        def unpackNativeLibrary(def nativeLibraryName, def tempDirectory) {
            try {
                InputStream is = getClass().getResourceAsStream("/" + nativeLibraryName);
                File tempNativeLib = new File(tempDirectory, nativeLibraryName)
                FileOutputStream os = new FileOutputStream(tempNativeLib);
                copyAndClose(is, os);
            } catch (IOException ioe) {
                throw new RuntimeException("Could not unpack native library " + nativeLibraryName, ioe);
            }
        }

        def copyAndClose(InputStream is, OutputStream os) throws IOException {
            byte[] buffer = new byte[1024];

            while (true) {
                int len = is.read(buffer);
                if (len < 0) break;
                os.write(buffer, 0, len);
            }

            is.close();
            os.close();
        }
    }
}

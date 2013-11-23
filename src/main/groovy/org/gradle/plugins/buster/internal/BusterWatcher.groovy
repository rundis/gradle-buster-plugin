package org.gradle.plugins.buster.internal

import name.pachler.nio.file.*
import org.gradle.api.Project
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor

import static name.pachler.nio.file.StandardWatchEventKind.*

class BusterWatcher {

    private final Project project
    private final BusterJSParser busterJSParser
    private final Closure listener
    private final WatchService watcher
    private final Map<WatchKey,Path> keys
    private volatile boolean stop
    private final Throttler throttler




    private BusterWatcher(Project project, Closure listener, BusterJSParser busterJSParser) throws IOException {
        this.project = project
        this.busterJSParser = busterJSParser
        this.watcher = FileSystems.getDefault().newWatchService()
        this.keys = new HashMap<>()
        this.listener = listener
        this.throttler = new Throttler(100, listener)
    }

    static BusterWatcher create(Project project, BusterJSParser busterJSParser, Closure listener) {
        try {
            Path path = Paths.get(project.projectDir.absolutePath)
            println path

            BusterWatcher watcher = new BusterWatcher(project, listener, busterJSParser)
            watcher.registerAll(path)
            return watcher
        } catch (IOException e) {
            throw new BusterWatcherException("Error creating watcher", e);
        }
    }

    void stop() {
        stop = true
    }



    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
        while(!stop) {
            // wait for key to be signalled
            WatchKey key
            try {
                key = watcher.take()
            } catch (InterruptedException x) {
                return
            }

            Path dir = keys.get(key)
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue
            }


            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind()

                if (kind == OVERFLOW) {
                    project.logger.warn("Watcher overflow")
                    continue
                }

                Path child = resolveChild(dir, event)
                if(globMatcher().matches(child.toString())) {
                    throttler.queue([kind:event.kind().name(), path:child])
                }
                if (kind == ENTRY_CREATE) {
                    registerIfDirectory(child)
                }
            }

            boolean valid = key.reset()
            if (!valid) {
                keys.remove(key)

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break
                }
            }
        }

    }

    private GlobMatcher globMatcher() {
        def globPatterns = busterJSParser.extractGlobPatterns(busterJsConfig())
        new GlobMatcher(project.projectDir.absolutePath, globPatterns)
    }

    private String busterJsConfig() {
        File busterJsFile = project.buster.resolveConfigFile(project)
        if(!busterJsFile) {
            throw new IllegalArgumentException("No default buster config file found and no config file specified in options")
        }

        busterJsFile.text
    }



    private Path resolveChild(Path dir, WatchEvent<?> event) {
        WatchEvent<Path> ev = cast(event)
        Path name = ev.context()
        return dir.resolve(name)
    }

    private void registerIfDirectory(Path child) {
        try {
            if (new File(child.toString()).directory) {
                registerAll(child)
            }
        } catch (IOException x) {
            throw new BusterWatcherException("Error registering new directory", x)
        }
    }


    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
        keys.put(key, dir)

        project.logger.info("Registered watcher for dir:" + dir.toString())

    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        register(start)
        project.fileTree(dir: start.toString()).visit(new FileVisitor() {
            @Override
            void visitDir(FileVisitDetails fileVisitDetails) {
                register(Paths.get(fileVisitDetails.file.absolutePath))
            }

            @Override
            void visitFile(FileVisitDetails fileVisitDetails) {  }
        })
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event
    }
}

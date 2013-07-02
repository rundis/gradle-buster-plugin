package org.gradle.plugins.buster.internal;

public class BusterWatcherException extends RuntimeException {
    public BusterWatcherException(String message, Throwable t) {
        super(message, t);
    }
}

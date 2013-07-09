package org.gradle.plugins.buster.config

class BusterConfig {
    static final DEFAULT_BUSTER_PORT = 1111

    Integer port = DEFAULT_BUSTER_PORT
    File configFile

    // Also contains "magic collection"
    // NamedDomainObjectCollection<Browser> browsers



}

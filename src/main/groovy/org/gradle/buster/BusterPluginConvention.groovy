package org.gradle.buster

import org.gradle.api.Project
import org.gradle.buster.internal.BusterConfig
import org.gradle.util.ConfigureUtil


class BusterPluginConvention {
    static final DEFAULT_BUSTER_PORT = 1111
    final BusterConfig busterConfig

    BusterPluginConvention(Project project) {
        busterConfig = new BusterConfig()
        busterConfig.port = DEFAULT_BUSTER_PORT
    }


    void buster(Closure configure) {
        assert configure != null, "Configure closure should not be null"
        ConfigureUtil.configure(configure, busterConfig)
    }


}

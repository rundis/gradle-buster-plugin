package com.github.rundis.buster.internal.browsercapture

import org.apache.commons.lang3.SystemUtils


class BrowserDriverUtil {
    static DEFAULT_DRIVERS_DIR_SUBPATH = ".gradle/browserdrivers"

    static CHROME_DRIVER_VERSION = "2.2"
    static CHROME_BASE_FILE_NAME = "chromedriver"
    static CHROME_DEFAULT_SUBDIR = "chrome"
    static CHROME_DRIVER_SYS_PROPERTY = 'webdriver.chrome.driver'


    AntBuilder ant

    String resolveChromeDriverFileName() {
        assert isSupportedOSChrome(), "No chromedriver supported for $osName"

        if (SystemUtils.IS_OS_MAC_OSX) {
            "${CHROME_BASE_FILE_NAME}_mac32_${CHROME_DRIVER_VERSION}.zip"
        } else {
            "${CHROME_BASE_FILE_NAME}_linux${os64Bits ? "64" : "32"}_${CHROME_DRIVER_VERSION}.zip"
        }
    }

    String chromeDriverUrl() {
        "https://chromedriver.googlecode.com/files/${resolveChromeDriverFileName()}"
    }

    boolean isChromeDriverSetup() {
        System.getProperty('webdriver.chrome.driver')
    }

    String setupChromeDriver() {
        def destDir = defaultChromeDriversDir
        def driverExecutable = new File(destDir, "chromedriver")

        if (!driverExecutable.exists()) {
            destDir.mkdirs()


            def destFileName = new File(destDir, "driver.zip")
            ant.get(
                    src: chromeDriverUrl(),
                    dest: destFileName,
                    skipexisting: true
            )
            ant.unzip(src: destFileName, dest: destDir)
            ant.delete(file: destFileName)
            ant.chmod(file: driverExecutable, perm: '700')
        }
        System.setProperty(CHROME_DRIVER_SYS_PROPERTY, driverExecutable.absolutePath)
        driverExecutable.absolutePath
    }

    private boolean isSupportedOSChrome() {
        SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_LINUX
    }

    private boolean isOs64Bits() {
        SystemUtils.OS_ARCH.contains('64')
    }

    private String getOsName() {
        SystemUtils.OS_NAME
    }


    File getDefaultChromeDriversDir() {
        new File("${SystemUtils.USER_HOME}/$DEFAULT_DRIVERS_DIR_SUBPATH/$CHROME_DEFAULT_SUBDIR")
    }
}

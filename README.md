A gradle plugin for running buster.js tests.

Its primary goal is to allow you to run your javascript tests as part of your CI build. Secondary but still very important is to provide
a smooth development cycle for you as a developer.


_Big disclaimer: Will not run on Windows_


### Preconditions
* [Gradle](http://www.gradle.org) 1.6 or higher
* [NodeJS/npm](http://nodejs.org/) - Precondition for Buster.js. Gives you superfast javascript tests.
* [Buster.js node module](http://busterjs.org/docs/getting-started/) - The kickass javascript test framework this plugin is all about enabling in your gradle builds.
* [PhantomJS](http://phantomjs.org/) - Required for enabling headless testing on your CI server. It makes sense that you also test with phantom during development.

In addition you may of course test locally with any browser(s) you wish. In fact You should be able to do all your buster testing without thinking much about the gradle plugin.


### Set up plugin

```groovy
buildscript {
    repositories {
        mavenCentral()
        mavenRepo urls: 'http://dl.bintray.com/rundis/maven'
    }
    dependencies {
        classpath  'org.gradle.buster:gradle-buster-plugin:0.1.1'
    }
}

apply plugin: 'buster'


build.dependsOn busterTest, stopBusterServer // Optional, hook up the relevant buster tasks into your build task graph

```

#### Once gradle 1.7 is released it should be even simpler
```groovy
buildscript {
    repositories { jcenter() }
    dependencies {
        classpath  'org.gradle.buster:gradle-buster-plugin:0.1.1'
    }
}

apply plugin: 'buster'


build.dependsOn busterTest, stopBusterServer // Optional, hook up the relevant buster tasks into your build task graph

```


### Configuration options
```groovy
buster {
	port = 1112 // override default of 1111 for buster, optional to specify
	configFile = file('config/buster.js') /* If left out it will look in $project.projectDir/buster.js | $project.projectDir/test/buster.js | $project.projectDir/spec/buster.js */
}
```

Please note that in a multiproject setting, you are in trouble if you start setting up different ports for the buster server.


### Available tasks
* busterServer : Starts the buster server
* capturePhantom : Captures phantom browser in buster server (depends on busterServer task)
* busterTest : Run buster test. Assumes you have configured buster.js for your project. (depends on capturePhantom task)
    * Test output in junit format can be found in $project/build/busterTest-results/bustertest.xml
* stopPhantom: Stops the capture phantom browser (might be a good idea sometimes to ensure reproducable tests)
* stopBusterServer : Stops the buster server. (Depends on stopPhantom)
* busterAutoTest : Run buster testing in continuous mode (depends on capturePhantom task). Any changes/adds/deletes to files matching glob patterns in
the buster.js configuration file will automatically trigger a buster test run.

All tasks have sanitychecks of whether they need to execute or not.
The only exception currently is busterTest which would benefit from incremental support.


#### busterTest
You may configure the busterTest task with the following options
* _busterKillOnFail_  -  If true the task will kill phantom and buster server on test failure (or failure to launch buster test)

The following example shows how you would make it conditional (to allow CI to always do it, whilst for development you leave it running)

```groovy
busterTest {
    busterKillOnFail = project.hasProperty("busterKillOnFail") && project.busterKillOnFail ?: false
}
```

Now on your CI server you specify -PbusterKillOnFail=true to ensure that the buster server and phantom processes aren't left dangling.


#### busterAutoTest
* This task will keep the gradle build running (ctrl+c to quit).
* It doesn't support multiprojects very nicely, so you would need to do something along the lines of _$ gradle :subproject:busterAutoTest_ for each subproject


### Usage scenarios

#### As part of a CI build
* You would typically either 
	* hook up the relevant gradle tasks as dependencies in your build task graph (as shown in the top)
	* or specify gradle busterTest stopBusterServer in a gradle build step in your build server config
* Its hightly recommended to set up busterKillOnFail to true on the busterTestTask as shown above. Alternatively you may
add a gradle build step in your build config that invokes stopBusterServer, and then have busterTest in a separate build step running after.
This way you ensure there aren't any dangling processes left before your tests start running.
The main culprit would be PhantomJS, that could be left running and the on subsequent build runs you will not be able to capture phantom.




### Version history

#### 0.1.1
* Added incubating support for auto testing (Continuous testing)

#### 0.1.0
Initial release with bare bone functionality.

### License
The BSD 2-Clause License
A gradle plugin for running buster.js tests.

Its primary goal is to allow you to run your javascript tests as part of your CI build. Secondary but still very important is to provide
a smooth development cycle for you as a developer.


_Big disclaimer: Will not run on Windows_


### Preconditions
* [Gradle](http://www.gradle.org) 1.6 or higher
* [NodeJS/npm](http://nodejs.org/) - Precondition for Buster.js. Gives you superfast javascript tests.
* [Buster.js node module](http://busterjs.org/docs/getting-started/) - The kickass javascript test framework this plugin is all about enabling in your gradle builds.
* PhantomJS - Required for enabling headless testing on your CI server. It makes sense that you also test with phantom during development.

In addition you may of course test locally with any browser(s) you wish. In fact You should be able to do all your buster testing without thinking much about the gradle plugin.


### Set up plugin
```groovy
	buildscript {
	    repositories {
	        mavenRepo urls: 'https://github.com/rundis/rundis-maven-repo/raw/master/'
	    }
	    dependencies {
	        classpath  'org.gradle.buster:gradle-buster-plugin:0.1-SNAPSHOT'
	    }
	}

apply plugin: 'buster'
```

### Configuration options
```groovy
buster {
	port = 1112 // override default of 1111 for buster, optional to specify
	configFile = file('config/buster.js') // If left out it will look in $project.projectDir/buster.js | $project.projectDir/test/buster.js | $project.projectDir/spec/buster.js
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

All tasks have sanitychecks of whether they need to execute or not.
The only exception currently is busterTest which would benefit from incremental support.


### Version history

#### 0.1.0

### License
The BSD 2-Clause License
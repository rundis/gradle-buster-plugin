A gradle plugin for running buster.js tests.

_Disclaimer: Will not run on Windows_


### Preconditions
* NodeJS/npm
* PhantomJS (?mac -> $ brew install phantomjs)
* [Buster.js node module](http://busterjs.org/docs/getting-started/)



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
	port = 1112 // override default of 1111 for buster
}
```


### Available tasks
* busterServer : Starts the buster server
* capturePhantom : Captures phantom browser in buster server (depends on busterServer task)
* busterTest : Run buster test. Assumes you have configured buster.js for your project. (depends on capturePhantom task)
    * Test output in junit format can be found in $project/build/test-results/bustertest.xml
* stopPhantom: Stops the capture phantom browser (might be a good idea sometimes to ensure reproducable tests)
* stopBusterServer : Stops the buster server. (Depends on stopPhantom)

All tasks have sanitychecks of whether they need to execute or not.
The only exception currently is busterTest which would benefit from incremental support.
A gradle plugin for running buster.js tests.

Its primary goal is to allow you to run your javascript tests as part of your CI build. Secondary but still very important is to provide
a smooth development cycle for you as a developer.


_Big disclaimer: Will not run on Windows_



### Preconditions
* [Gradle](http://www.gradle.org) 1.6 or higher
* [NodeJS/npm](http://nodejs.org/) - Precondition for Buster.js. Gives you superfast javascript tests.
* [Buster.js node module](http://docs.busterjs.org/en/latest/) - The kickass javascript test framework this plugin is all about enabling in your gradle builds.
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
        classpath  'org.gradle.buster:gradle-buster-plugin:0.2.0'
    }
}

apply plugin: 'buster'


build.dependsOn busterTest // Optional, hook up the relevant buster tasks into your build task graph

```

#### Gradle 1.7
```groovy
buildscript {
    repositories { jcenter() }
    dependencies {
        classpath  'org.gradle.buster:gradle-buster-plugin:0.2.0'
    }
}

apply plugin: 'buster'


build.dependsOn busterTest // Optional, hook up the relevant buster tasks into your build task graph

```


### Configuration options
```groovy
buster {
	port = 1112 // override default of 1111 for buster, optional to specify
	configFile = file('config/buster.js') /* If left out it will look in $project.projectDir/buster.js | $project.projectDir/test/buster.js | $project.projectDir/spec/buster.js */
	browsers {  // if you leave out this closure, phantomjs is default added to the browsers used for testing
	    phantomjs
	    firefox
	    safari
	}
}
```


#### More on browser configuration
Browsers are captured using [Selenium](http://docs.seleniumhq.org/).
This opens up for some really powerful options for future versions of this plugin. Future versions of the plugin might support running
browsers on a remote machine (handy if you wish to test real browsers from your headless ci machine, or say you are on a nix system and need to test internet explorer).

Currently only PhantomJs, Firefox, Safari(only mac) and Chrome are supported.
Please keep in mind the expected location for executables
* [Chrome](https://code.google.com/p/selenium/wiki/ChromeDriver#Requirements)
* PhantomJs - Must be in path or set system property: `phantomjs.binary.path`
* [Firefox](https://code.google.com/p/selenium/wiki/FirefoxDriver#Important_System_Properties)


The chrome driver for selenium requires a separate platform dependent executable. This is setup for you automatically
unless you specify the the system property : `webdriver.chrome.driver`.
The executable will be installed to  `~/.gradle/browserdrivers/chrome/`

So if you are happy with the defaults, all you have to do is specify which browsers you wish to test with.



### Available tasks
* busterTest : Will start a buster server, capture defined browsers, run the tests, shutdown browsers and stop the buster server
* busterAutoTest : Run buster testing in continuous mode . Any changes/adds/deletes to files matching glob patterns in
the buster.js configuration file will automatically trigger a buster test run. Also starts a buster server and captures browsers, before it starts listening for changes.
* busterSetupWebDrivers : Used by the 2 above to do required driver setup on demand. If no setup is needed the task is skipped.



#### busterTest
_busterTest does not yet support incremental builds_


#### busterAutoTest
* This task will keep the gradle build running (ctrl+c to quit).
* It doesn't support multiprojects very nicely, so you would need to do something along the lines of _$ gradle :subproject:busterAutoTest_ for each subproject


### Usage scenarios

#### As part of a CI build
* You would typically either 
	* hook up the relevant gradle tasks as dependencies in your build task graph (as shown in the top)
	* or specify gradle busterTestin a gradle build step in your build server config

#### Local development
With the support for automatically capturing browsers and the autotest support, you might consider using the plugin
for your local javascript tdd cycle as well.

To setup up which browsers you whish to test on your machine you could use an [init script](http://www.gradle.org/docs/current/userguide/init_scripts.html).
Example init.gradle:
```groovy
projectsEvaluated {
	rootProject.allprojects {project ->
		project.plugins.hasPlugin('buster') {
			buster {
				browsers {
					phantomjs
					chrome
					safari
					firefox
				}
			}
		}
	}
}
```


### Limitations
* Will not run on windows
* Not tested with gradle daemon. Should work for busterTest, but busterAutoTest most likely will not cleanup until the daemon dies !
* Running parallel builds with buster testing on your ci server could cause problems as the buster server process management doesn't handle more than one buster process at a time.


### Version history

#### Upcoming (promiseware :-) )
* Support for testing with multiple browsers using selenium to capture browsers

#### 0.1.1
* Added incubating support for auto testing (Continuous testing)

#### 0.1.0
Initial release with bare bone functionality.

### License
The BSD 2-Clause License
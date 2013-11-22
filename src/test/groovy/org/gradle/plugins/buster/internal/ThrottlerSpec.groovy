package org.gradle.plugins.buster.internal

import spock.lang.Specification


class ThrottlerSpec extends Specification {


    def "executes in 100 ms"() {
        given:
        def numExecutes = 0;
        Closure myClosure = {args -> numExecutes++}
        def throttler = new Throttler(delay:100, closure: myClosure)

        when:
        throttler.queue([:])
        sleep(150)

        then:
        numExecutes == 1
    }

    def "does not execute before 100 ms"() {
        given:
        def numExecutes = 0;
        Closure myClosure = {args -> numExecutes++}
        def throttler = new Throttler(delay:100, closure: myClosure)

        when:
        throttler.queue([:])
        sleep(50)

        then:
        numExecutes == 0
    }

    def "executes only once in 100ms"() {
        given:
        def numExecutes = 0;
        Closure myClosure = {args -> numExecutes++}
        def throttler = new Throttler(delay:100, closure: myClosure)

        when:
        throttler.queue([:])
        sleep(20)
        throttler.queue([:])
        sleep(150)

        then:
        numExecutes == 1
    }


}

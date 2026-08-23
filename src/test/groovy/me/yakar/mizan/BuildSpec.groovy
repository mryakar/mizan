package me.yakar.mizan

import spock.lang.Specification

class BuildSpec extends Specification {

    def "the build runs tests on Java 21"() {
        expect:
        System.getProperty('java.specification.version') == '21'
    }
}
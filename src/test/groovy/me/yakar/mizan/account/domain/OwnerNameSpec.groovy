package me.yakar.mizan.account.domain

import me.yakar.mizan.shared.error.ValidationException
import spock.lang.Specification

class OwnerNameSpec extends Specification {

    def "a name keeps its text without surrounding whitespace"() {
        expect:
        new OwnerName('  Ahmet Yakar  ').value() == 'Ahmet Yakar'
    }

    def "a name is rendered as its own text"() {
        expect:
        new OwnerName('Ahmet Yakar').toString() == 'Ahmet Yakar'
    }

    def "a name of the maximum length is accepted"() {
        expect:
        new OwnerName('y' * 100).value().length() == 100
    }

    def "a missing or blank name is rejected"() {
        when:
        new OwnerName(candidate)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'ownerName'

        where:
        candidate << [null, '', '   ', '\t\n']
    }

    def "a name longer than the maximum is rejected"() {
        when:
        new OwnerName('y' * 101)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'ownerName'
        failure.message.contains('100')
    }
}

package me.yakar.mizan.shared.money

import me.yakar.mizan.shared.error.ValidationException
import spock.lang.Specification

import java.util.Currency

class CurrenciesSpec extends Specification {

    def "a valid ISO-4217 code is accepted, whatever its case or padding"() {
        expect:
        Currencies.parse(candidate) == Currency.getInstance('TRY')

        where:
        candidate << ['TRY', 'try', ' TRY ', 'tRy']
    }

    def "an absent code is reported as missing rather than invalid"() {
        when:
        Currencies.parse(candidate)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'currency'
        failure.message == 'Currency is required'

        where:
        candidate << [null, '', '   ', '\t\n']
    }

    def "an unknown code is reported as invalid rather than missing"() {
        when:
        Currencies.parse(candidate)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'currency'
        failure.message == 'Currency must be a valid ISO-4217 code'

        where:
        candidate << ['TR', 'abc', 'XX1', 'TURKISH']
    }
}

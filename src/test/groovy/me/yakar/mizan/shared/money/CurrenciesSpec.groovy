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

    def "an unknown or malformed code is rejected"() {
        when:
        Currencies.parse(candidate)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'currency'

        where:
        candidate << [null, '', '   ', 'TR', 'abc', 'XX1', 'TURKISH']
    }
}

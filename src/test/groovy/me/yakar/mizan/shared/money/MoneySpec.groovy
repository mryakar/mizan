package me.yakar.mizan.shared.money

import me.yakar.mizan.shared.error.ValidationException
import spock.lang.Specification

import java.util.Currency

class MoneySpec extends Specification {

    static final Currency TRY = Currency.getInstance('TRY')
    static final Currency JPY = Currency.getInstance('JPY')

    def "amounts are normalised to the fraction digits of their currency"() {
        expect:
        Money.of(new BigDecimal(amount), currency).amount().toPlainString() == normalised

        where:
        amount    | currency || normalised
        '10'      | TRY      || '10.00'
        '10.0'    | TRY      || '10.00'
        '10.0000' | TRY      || '10.00'
        '10'      | JPY      || '10'
        '10.00'   | JPY      || '10'
    }

    def "amounts that differ only in scale are equal"() {
        expect:
        Money.of(new BigDecimal('10.0'), TRY) == Money.of(new BigDecimal('10.0000'), TRY)
    }

    def "the same amount in different currencies is not equal"() {
        expect:
        Money.of(BigDecimal.TEN, TRY) != Money.of(BigDecimal.TEN, Currency.getInstance('EUR'))
    }

    def "a zero amount carries its currency"() {
        when:
        def zero = Money.zero(TRY)

        then:
        zero.amount().toPlainString() == '0.00'
        zero.currency() == TRY
    }

    def "an amount requires both a value and a currency"() {
        when:
        new Money(amount, currency)

        then:
        def failure = thrown(ValidationException)
        failure.field() == field

        where:
        amount         | currency || field
        null           | TRY      || 'amount'
        BigDecimal.ONE | null     || 'currency'
    }

    def "an amount with more precision than its currency allows is rejected"() {
        when:
        Money.of(new BigDecimal('10.005'), TRY)

        then:
        thrown(ArithmeticException)
    }
}

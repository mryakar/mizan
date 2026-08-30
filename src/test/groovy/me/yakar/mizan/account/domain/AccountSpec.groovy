package me.yakar.mizan.account.domain

import spock.lang.Specification

import java.time.Instant
import java.util.Currency

class AccountSpec extends Specification {

    def "an opened account carries a fresh id and the moment it was opened"() {
        given:
        def openedAt = Instant.parse('2026-08-30T09:15:00Z')

        when:
        def account = Account.open(new OwnerName('Ahmet Yakar'), Currency.getInstance('TRY'), openedAt)

        then:
        account.id() != null
        account.ownerName().value() == 'Ahmet Yakar'
        account.currency() == Currency.getInstance('TRY')
        account.openedAt() == openedAt
    }

    def "two accounts opened with the same details are still distinct"() {
        given:
        def name = new OwnerName('Ahmet Yakar')
        def currency = Currency.getInstance('TRY')
        def openedAt = Instant.parse('2026-08-30T09:15:00Z')

        expect:
        Account.open(name, currency, openedAt).id() != Account.open(name, currency, openedAt).id()
    }
}

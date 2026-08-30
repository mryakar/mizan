package me.yakar.mizan.account.application

import me.yakar.mizan.account.domain.AccountId
import me.yakar.mizan.account.domain.AccountNotFoundException
import me.yakar.mizan.account.domain.OwnerName
import me.yakar.mizan.shared.money.Money
import me.yakar.mizan.shared.time.Clock
import spock.lang.Specification

import java.time.Instant
import java.util.Currency

class AccountServiceSpec extends Specification {

    static final Instant NOW = Instant.parse('2026-08-30T09:15:00Z')
    static final Currency TRY = Currency.getInstance('TRY')

    InMemoryAccountRepository accounts = new InMemoryAccountRepository()
    AccountService service = new AccountService(accounts, { NOW } as Clock)

    def "opening an account stores it and stamps it with the current time"() {
        when:
        def summary = service.open(new OwnerName('Ahmet Yakar'), TRY)

        then:
        accounts.size() == 1
        summary.account().ownerName().value() == 'Ahmet Yakar'
        summary.account().currency() == TRY
        summary.account().openedAt() == NOW
    }

    def "a newly opened account has a zero balance in its own currency"() {
        when:
        def summary = service.open(new OwnerName('Ahmet Yakar'), TRY)

        then:
        summary.balance() == Money.zero(TRY)
    }

    def "an opened account can be read back"() {
        given:
        def opened = service.open(new OwnerName('Ahmet Yakar'), TRY)

        when:
        def found = service.summaryOf(opened.account().id())

        then:
        found.account() == opened.account()
    }

    def "the balance read back is the one the ledger reports"() {
        given:
        def opened = service.open(new OwnerName('Ahmet Yakar'), TRY)
        accounts.withBalance(opened.account().id(), Money.of(new BigDecimal('42.50'), TRY))

        expect:
        service.summaryOf(opened.account().id()).balance() == Money.of(new BigDecimal('42.50'), TRY)
    }

    def "reading an account that does not exist fails"() {
        when:
        service.summaryOf(AccountId.next())

        then:
        thrown(AccountNotFoundException)
    }
}

package me.yakar.mizan.account.persistence

import me.yakar.mizan.account.domain.Account
import me.yakar.mizan.account.domain.AccountId
import me.yakar.mizan.account.domain.OwnerName
import me.yakar.mizan.platform.database.Database
import me.yakar.mizan.shared.money.Money
import me.yakar.mizan.support.PostgresSpec
import spock.lang.Shared

import java.time.Instant
import java.time.ZoneOffset
import java.util.Currency

import static me.yakar.mizan.db.tables.Accounts.ACCOUNTS
import static me.yakar.mizan.db.tables.Entries.ENTRIES

class JooqAccountRepositorySpec extends PostgresSpec {

    static final Currency TRY = Currency.getInstance('TRY')
    static final Instant OPENED_AT = Instant.parse('2026-08-30T09:15:00Z')

    @Shared
    Database database

    @Shared
    JooqAccountRepository repository

    def setupSpec() {
        database = Database.connect(databaseConfiguration())
        database.migrate()
        repository = new JooqAccountRepository(database.dsl())
    }

    def cleanupSpec() {
        database.close()
    }

    def setup() {
        database.dsl().deleteFrom(ENTRIES).execute()
        database.dsl().deleteFrom(ACCOUNTS).execute()
    }

    def "a saved account is read back with every detail intact"() {
        given:
        def account = new Account(AccountId.next(), new OwnerName('Ahmet Yakar'), TRY, OPENED_AT)

        when:
        repository.save(account)
        def summary = repository.findSummary(account.id())

        then:
        summary.isPresent()
        summary.get().account() == account
    }

    def "an account with no entries has a zero balance"() {
        given:
        def account = new Account(AccountId.next(), new OwnerName('Ahmet Yakar'), TRY, OPENED_AT)
        repository.save(account)

        expect:
        repository.findSummary(account.id()).get().balance() == Money.zero(TRY)
    }

    def "the balance is the sum of the account's entries"() {
        given:
        def account = new Account(AccountId.next(), new OwnerName('Ahmet Yakar'), TRY, OPENED_AT)
        repository.save(account)
        postEntry(account.id(), '10.00')
        postEntry(account.id(), '-3.50')

        expect:
        repository.findSummary(account.id()).get().balance() ==
                Money.of(new BigDecimal('6.50'), TRY)
    }

    def "entries belonging to other accounts do not affect a balance"() {
        given:
        def mine = new Account(AccountId.next(), new OwnerName('Ahmet Yakar'), TRY, OPENED_AT)
        def theirs = new Account(AccountId.next(), new OwnerName('Simge Yakar'), TRY, OPENED_AT)
        repository.save(mine)
        repository.save(theirs)
        postEntry(mine.id(), '10.00')
        postEntry(theirs.id(), '999.00')

        expect:
        repository.findSummary(mine.id()).get().balance() == Money.of(new BigDecimal('10.00'), TRY)
    }

    def "an account that was never saved is not found"() {
        expect:
        repository.findSummary(AccountId.next()).isEmpty()
    }

    private void postEntry(AccountId accountId, String amount) {
        database.dsl()
                .insertInto(ENTRIES)
                .set(ENTRIES.ID, UUID.randomUUID())
                .set(ENTRIES.ACCOUNT_ID, accountId.value())
                .set(ENTRIES.AMOUNT, new BigDecimal(amount))
                .set(ENTRIES.CREATED_AT, OPENED_AT.atOffset(ZoneOffset.UTC))
                .execute()
    }
}

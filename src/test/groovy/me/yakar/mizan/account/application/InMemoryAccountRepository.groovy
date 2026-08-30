package me.yakar.mizan.account.application

import me.yakar.mizan.account.domain.Account
import me.yakar.mizan.account.domain.AccountId
import me.yakar.mizan.account.domain.AccountRepository
import me.yakar.mizan.account.domain.AccountSummary
import me.yakar.mizan.shared.money.Money

/**
 * Stands in for the database in unit tests. Balances are held alongside accounts so that a test can
 * describe an account that already has movement on it, without depending on how entries are stored.
 */
class InMemoryAccountRepository implements AccountRepository {

    private final Map<AccountId, Account> accounts = [:]
    private final Map<AccountId, Money> balances = [:]

    @Override
    void save(Account account) {
        accounts[account.id()] = account
    }

    @Override
    Optional<AccountSummary> findSummary(AccountId accountId) {
        Account account = accounts[accountId]
        if (account == null) {
            return Optional.empty()
        }
        Optional.of(new AccountSummary(
                account, balances.getOrDefault(accountId, Money.zero(account.currency()))))
    }

    void withBalance(AccountId accountId, Money balance) {
        balances[accountId] = balance
    }

    int size() {
        accounts.size()
    }
}

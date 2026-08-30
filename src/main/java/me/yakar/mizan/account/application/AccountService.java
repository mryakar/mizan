package me.yakar.mizan.account.application;

import java.util.Currency;
import me.yakar.mizan.account.domain.Account;
import me.yakar.mizan.account.domain.AccountId;
import me.yakar.mizan.account.domain.AccountNotFoundException;
import me.yakar.mizan.account.domain.AccountRepository;
import me.yakar.mizan.account.domain.AccountSummary;
import me.yakar.mizan.account.domain.OwnerName;
import me.yakar.mizan.shared.money.Money;
import me.yakar.mizan.shared.time.Clock;

public class AccountService {

  private final AccountRepository accounts;
  private final Clock clock;

  public AccountService(AccountRepository accounts, Clock clock) {
    this.accounts = accounts;
    this.clock = clock;
  }

  public AccountSummary open(OwnerName ownerName, Currency currency) {
    Account account = Account.open(ownerName, currency, clock.now());
    accounts.save(account);
    return new AccountSummary(account, Money.zero(currency));
  }

  public AccountSummary summaryOf(AccountId accountId) {
    return accounts
        .findSummary(accountId)
        .orElseThrow(() -> new AccountNotFoundException(accountId));
  }
}

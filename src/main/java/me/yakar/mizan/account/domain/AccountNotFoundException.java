package me.yakar.mizan.account.domain;

import me.yakar.mizan.shared.error.NotFoundException;

public class AccountNotFoundException extends NotFoundException {

  public AccountNotFoundException(AccountId accountId) {
    super("No account exists with id " + accountId.value());
  }
}

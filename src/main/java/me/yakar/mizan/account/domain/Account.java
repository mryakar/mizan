package me.yakar.mizan.account.domain;

import java.time.Instant;
import java.util.Currency;

public record Account(AccountId id, OwnerName ownerName, Currency currency, Instant openedAt) {

  public static Account open(OwnerName ownerName, Currency currency, Instant openedAt) {
    return new Account(AccountId.next(), ownerName, currency, openedAt);
  }
}

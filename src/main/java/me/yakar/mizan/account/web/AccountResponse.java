package me.yakar.mizan.account.web;

import me.yakar.mizan.account.domain.AccountSummary;

public record AccountResponse(
    String id, String ownerName, String currency, String balance, String openedAt) {

  public static AccountResponse from(AccountSummary summary) {
    return new AccountResponse(
        summary.account().id().toString(),
        summary.account().ownerName().value(),
        summary.account().currency().getCurrencyCode(),
        summary.balance().amount().toPlainString(),
        summary.account().openedAt().toString());
  }
}

package me.yakar.mizan.account.domain;

import me.yakar.mizan.shared.money.Money;

public record AccountSummary(Account account, Money balance) {}

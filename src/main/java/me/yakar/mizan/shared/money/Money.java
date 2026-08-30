package me.yakar.mizan.shared.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import me.yakar.mizan.shared.error.ValidationException;

/**
 * A monetary amount in a single currency.
 *
 * <p>The amount is normalized to the currency's own number of fraction digits on construction, so
 * that two instances representing the same value are also equal. Without that normalisation {@code
 * 10.0} and {@code 10.00} would be unequal, and the ledger's "entries sum to zero" invariant is
 * built on equality.
 */
public record Money(BigDecimal amount, Currency currency) {

  public Money {
    if (amount == null) {
      throw new ValidationException("amount", "Amount is required");
    }
    if (currency == null) {
      throw new ValidationException("currency", "Currency is required");
    }
    amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
  }

  public static Money zero(Currency currency) {
    return new Money(BigDecimal.ZERO, currency);
  }

  public static Money of(BigDecimal amount, Currency currency) {
    return new Money(amount, currency);
  }
}

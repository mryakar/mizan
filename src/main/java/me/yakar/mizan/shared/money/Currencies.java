package me.yakar.mizan.shared.money;

import java.util.Currency;
import me.yakar.mizan.shared.error.ValidationException;

public final class Currencies {

  private Currencies() {}

  public static Currency parse(String candidate) {
    if (candidate == null || candidate.isBlank()) {
      throw new ValidationException("currency", "Currency is required");
    }
    try {
      return Currency.getInstance(candidate.strip().toUpperCase());
    } catch (IllegalArgumentException unknown) {
      throw new ValidationException("currency", "Currency must be a valid ISO-4217 code");
    }
  }
}

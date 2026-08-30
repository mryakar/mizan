package me.yakar.mizan.account.domain;

import java.util.UUID;
import me.yakar.mizan.shared.error.ValidationException;
import org.jetbrains.annotations.NotNull;

public record AccountId(UUID value) {

  public AccountId {
    if (value == null) {
      throw new ValidationException("id", "Account id is required");
    }
  }

  public static AccountId next() {
    return new AccountId(UUID.randomUUID());
  }

  public static AccountId parse(String candidate) {
    if (candidate == null || candidate.isBlank()) {
      throw new ValidationException("id", "Account id is required");
    }

    try {
      return new AccountId(UUID.fromString(candidate));
    } catch (IllegalArgumentException malformed) {
      throw new ValidationException("id", "Account id must be a UUID");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}

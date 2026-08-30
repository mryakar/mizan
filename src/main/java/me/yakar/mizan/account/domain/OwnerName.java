package me.yakar.mizan.account.domain;

import me.yakar.mizan.shared.error.ValidationException;

public record OwnerName(String value) {

  private static final int MAX_LENGTH = 100;

  public OwnerName {
    if (value == null || value.isBlank()) {
      throw new ValidationException("ownerName", "Owner name is required");
    }
    value = value.strip();
    if (value.length() > MAX_LENGTH) {
      throw new ValidationException(
          "ownerName", "Owner name must be at most " + MAX_LENGTH + " characters");
    }
  }

  @Override
  public String toString() {
    return value;
  }
}

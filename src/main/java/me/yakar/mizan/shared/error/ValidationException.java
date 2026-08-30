package me.yakar.mizan.shared.error;

public class ValidationException extends RuntimeException {

  private final String field;

  public ValidationException(String field, String message) {
    super(message);
    this.field = field;
  }

  public String field() {
    return field;
  }
}

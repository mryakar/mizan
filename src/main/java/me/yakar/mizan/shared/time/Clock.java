package me.yakar.mizan.shared.time;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@FunctionalInterface
public interface Clock {

  Instant now();

  /**
   * Truncated to microseconds, which is the resolution a PostgreSQL timestamp keeps. Without the
   * truncation an instant handed back to a caller would not equal the one read back from the
   * database a moment later.
   */
  static Clock systemUtc() {
    return () -> Instant.now().truncatedTo(ChronoUnit.MICROS);
  }
}

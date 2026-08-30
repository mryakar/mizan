package me.yakar.mizan.platform.config;

public record DatabaseConfiguration(
    String url, String username, String password, int maximumPoolSize) {

  private static final int DEFAULT_POOL_SIZE = 8;

  public static DatabaseConfiguration from(Environment environment) {
    return new DatabaseConfiguration(
        environment.require("MIZAN_DB_URL"),
        environment.require("MIZAN_DB_USERNAME"),
        environment.require("MIZAN_DB_PASSWORD"),
        environment.intOrDefault("MIZAN_DB_POOL_SIZE", DEFAULT_POOL_SIZE));
  }
}

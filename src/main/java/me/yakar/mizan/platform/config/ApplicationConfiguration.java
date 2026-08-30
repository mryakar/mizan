package me.yakar.mizan.platform.config;

public record ApplicationConfiguration(DatabaseConfiguration database, int httpPort) {

  private static final int DEFAULT_HTTP_PORT = 8080;

  public static ApplicationConfiguration from(Environment environment) {
    return new ApplicationConfiguration(
        DatabaseConfiguration.from(environment),
        environment.intOrDefault("MIZAN_HTTP_PORT", DEFAULT_HTTP_PORT));
  }
}

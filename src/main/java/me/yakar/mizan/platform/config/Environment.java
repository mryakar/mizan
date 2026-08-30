package me.yakar.mizan.platform.config;

import java.util.Map;
import java.util.Optional;

@FunctionalInterface
public interface Environment {

  Optional<String> lookup(String name);

  static Environment system() {
    return name -> Optional.ofNullable(System.getenv(name));
  }

  static Environment of(Map<String, String> values) {
    return name -> Optional.ofNullable(values.get(name));
  }

  default String require(String name) {
    return present(name)
        .orElseThrow(
            () ->
                new MissingConfigurationException("Environment variable " + name + " is not set"));
  }

  default int intOrDefault(String name, int fallback) {
    String value = present(name).orElse(String.valueOf(fallback));
    try {
      return Integer.parseInt(value.strip());
    } catch (NumberFormatException notANumber) {
      throw new MissingConfigurationException(
          "Environment variable " + name + " must be an integer, but was '" + value + "'");
    }
  }

  private Optional<String> present(String name) {
    return lookup(name).filter(value -> !value.isBlank());
  }
}

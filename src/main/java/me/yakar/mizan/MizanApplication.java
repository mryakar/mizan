package me.yakar.mizan;

import me.yakar.mizan.platform.config.ApplicationConfiguration;
import me.yakar.mizan.platform.config.Environment;

public final class MizanApplication {

  private MizanApplication() {}

  public static void main(String[] args) {
    Mizan mizan = Mizan.start(ApplicationConfiguration.from(Environment.system()));
    Runtime.getRuntime().addShutdownHook(new Thread(mizan::close, "mizan-shutdown"));
  }
}

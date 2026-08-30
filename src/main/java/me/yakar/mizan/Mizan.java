package me.yakar.mizan;

import java.util.List;
import me.yakar.mizan.account.application.AccountService;
import me.yakar.mizan.account.persistence.JooqAccountRepository;
import me.yakar.mizan.account.web.AccountEndpoints;
import me.yakar.mizan.platform.config.ApplicationConfiguration;
import me.yakar.mizan.platform.database.Database;
import me.yakar.mizan.platform.http.HttpServer;
import me.yakar.mizan.shared.time.Clock;

public class Mizan implements AutoCloseable {

  private final Database database;
  private final HttpServer http;
  private final int port;

  private Mizan(Database database, HttpServer http, int port) {
    this.database = database;
    this.http = http;
    this.port = port;
  }

  public static Mizan start(ApplicationConfiguration configuration) {
    Database database = Database.connect(configuration.database());
    try {
      database.migrate();
      AccountService accounts =
          new AccountService(new JooqAccountRepository(database.dsl()), Clock.systemUtc());
      HttpServer http =
          HttpServer.start(
              List.of(new AccountEndpoints(accounts)::register), configuration.httpPort());
      return new Mizan(database, http, configuration.httpPort());
    } catch (RuntimeException startupFailure) {
      database.close();
      throw startupFailure;
    }
  }

  public int port() {
    return port;
  }

  @Override
  public void close() {
    http.close();
    database.close();
  }
}

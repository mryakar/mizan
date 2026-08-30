package me.yakar.mizan.platform.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.yakar.mizan.platform.config.DatabaseConfiguration;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class Database implements AutoCloseable {

  private final HikariDataSource dataSource;

  private Database(HikariDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public static Database connect(DatabaseConfiguration configuration) {
    HikariConfig hikari = new HikariConfig();
    hikari.setJdbcUrl(configuration.url());
    hikari.setUsername(configuration.username());
    hikari.setPassword(configuration.password());
    hikari.setMaximumPoolSize(configuration.maximumPoolSize());
    hikari.setPoolName("mizan");
    return new Database(new HikariDataSource(hikari));
  }

  public void migrate() {
    Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").load().migrate();
  }

  public DSLContext dsl() {
    return DSL.using(dataSource, SQLDialect.POSTGRES);
  }

  @Override
  public void close() {
    dataSource.close();
  }
}

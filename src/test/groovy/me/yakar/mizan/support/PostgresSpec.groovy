package me.yakar.mizan.support

import me.yakar.mizan.platform.config.DatabaseConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

/**
 * Base class for tests that need a real PostgreSQL.
 *
 * <p>Testcontainers' JUnit 5 annotations do not apply to Spock, so the container is managed here
 * explicitly. It is started once per JVM rather than once per specification: on a four-core machine
 * that difference dominates the test suite's runtime. Ryuk removes the container when the JVM exits.
 */
abstract class PostgresSpec extends Specification {

    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>('postgres:18.6')

    static {
        POSTGRES.start()
    }

    protected static DatabaseConfiguration databaseConfiguration() {
        new DatabaseConfiguration(POSTGRES.jdbcUrl, POSTGRES.username, POSTGRES.password, 4)
    }
}

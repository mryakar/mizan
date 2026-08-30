package me.yakar.mizan

import me.yakar.mizan.platform.config.ApplicationConfiguration
import me.yakar.mizan.support.PostgresSpec

/**
 * Exercises the composition root itself: every collaborator can be wired, the service comes up on
 * the port it was given, and closing it releases both the HTTP server and the connection pool.
 */
class MizanSpec extends PostgresSpec {

    def "the application starts on its configured port and closes cleanly"() {
        given:
        def configuration = new ApplicationConfiguration(databaseConfiguration(), freePort())

        expect:
        Mizan.start(configuration).withCloseable { mizan ->
            mizan.port() == configuration.httpPort()
        }
    }

    def "the port it was started on is free again once it is closed"() {
        given:
        def port = freePort()

        when:
        Mizan.start(new ApplicationConfiguration(databaseConfiguration(), port)).close()

        then:
        Mizan.start(new ApplicationConfiguration(databaseConfiguration(), port)).withCloseable {
            it.port() == port
        }
    }

    private static int freePort() {
        new ServerSocket(0).withCloseable { it.localPort }
    }
}

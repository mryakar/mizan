package me.yakar.mizan.platform.config

import spock.lang.Specification

class ConfigurationSpec extends Specification {

    static final Map<String, String> COMPLETE = [
            MIZAN_DB_URL     : 'jdbc:postgresql://localhost:5432/mizan',
            MIZAN_DB_USERNAME: 'mizan',
            MIZAN_DB_PASSWORD: 'secret',
    ]

    def "configuration is read from the environment"() {
        when:
        def configuration = ApplicationConfiguration.from(Environment.of(
                COMPLETE + [MIZAN_DB_POOL_SIZE: '16', MIZAN_HTTP_PORT: '9000']))

        then:
        configuration.database().url() == 'jdbc:postgresql://localhost:5432/mizan'
        configuration.database().username() == 'mizan'
        configuration.database().password() == 'secret'
        configuration.database().maximumPoolSize() == 16
        configuration.httpPort() == 9000
    }

    def "optional settings fall back to their defaults"() {
        when:
        def configuration = ApplicationConfiguration.from(Environment.of(COMPLETE))

        then:
        configuration.database().maximumPoolSize() == 8
        configuration.httpPort() == 8080
    }

    def "a blank optional setting is treated as absent"() {
        when:
        def configuration = ApplicationConfiguration.from(Environment.of(
                COMPLETE + [MIZAN_HTTP_PORT: '   ']))

        then:
        configuration.httpPort() == 8080
    }

    def "a missing required setting is reported by name"() {
        when:
        ApplicationConfiguration.from(Environment.of(COMPLETE - [(missing): COMPLETE[missing]]))

        then:
        def failure = thrown(MissingConfigurationException)
        failure.message.contains(missing)

        where:
        missing << ['MIZAN_DB_URL', 'MIZAN_DB_USERNAME', 'MIZAN_DB_PASSWORD']
    }

    def "a blank required setting is reported as missing"() {
        when:
        ApplicationConfiguration.from(Environment.of(COMPLETE + [MIZAN_DB_URL: '  ']))

        then:
        thrown(MissingConfigurationException)
    }

    def "a numeric setting that is not a number is reported with the offending value"() {
        when:
        ApplicationConfiguration.from(Environment.of(COMPLETE + [MIZAN_HTTP_PORT: 'eight']))

        then:
        def failure = thrown(MissingConfigurationException)
        failure.message.contains('eight')
    }

    def "the system environment is readable"() {
        expect:
        Environment.system().lookup('PATH').isPresent()
        Environment.system().lookup('MIZAN_A_VARIABLE_NOBODY_SETS').isEmpty()
    }
}

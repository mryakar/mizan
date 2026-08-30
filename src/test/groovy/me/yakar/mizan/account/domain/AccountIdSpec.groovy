package me.yakar.mizan.account.domain

import me.yakar.mizan.shared.error.ValidationException
import spock.lang.Specification

class AccountIdSpec extends Specification {

    def "each generated id is distinct"() {
        expect:
        AccountId.next() != AccountId.next()
    }

    def "an id is parsed from its textual form"() {
        given:
        def id = AccountId.next()

        expect:
        AccountId.parse(id.toString()) == id
    }

    def "an id is rendered as a plain uuid"() {
        given:
        def uuid = UUID.randomUUID()

        expect:
        new AccountId(uuid).toString() == uuid.toString()
    }

    def "a missing id value is rejected"() {
        when:
        new AccountId(null)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'id'
    }

    def "an absent id is reported as missing rather than malformed"() {
        when:
        AccountId.parse(candidate)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'id'
        failure.message == 'Account id is required'

        where:
        candidate << [null, '', '   ', '\t\n']
    }

    def "a malformed id is reported as unusable rather than missing"() {
        when:
        AccountId.parse(candidate)

        then:
        def failure = thrown(ValidationException)
        failure.field() == 'id'
        failure.message == 'Account id must be a UUID'

        where:
        candidate << ['not-a-uuid', '123', 'x' * 36]
    }
}

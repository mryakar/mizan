package me.yakar.mizan.platform.http

import io.javalin.http.Context
import me.yakar.mizan.shared.error.NotFoundException
import me.yakar.mizan.shared.error.ValidationException
import spock.lang.Specification

class ProblemDetailHandlersSpec extends Specification {

    ProblemDetailHandlers handlers = new ProblemDetailHandlers()
    Context context = Mock(Context)
    ProblemDetail answered

    def setup() {
        context.status(_) >> context
        context.json(_) >> { arguments -> answered = arguments[0]; context }
        context.contentType(_) >> context
    }

    def "an unexpected failure is answered as an internal error without leaking its cause"() {
        when:
        handlers.unexpected(new IllegalStateException('connection to shard 7 refused'), context)

        then:
        answered.status() == 500
        answered.title() == 'Internal server error'
        !answered.detail().contains('shard 7')
        answered.field() == null
    }

    def "a rejected request is answered with the field at fault"() {
        when:
        handlers.invalidRequest(new ValidationException('ownerName', 'Owner name is required'), context)

        then:
        answered.status() == 400
        answered.title() == 'Invalid request'
        answered.detail() == 'Owner name is required'
        answered.field() == 'ownerName'
    }

    def "a missing resource is answered with what could not be found"() {
        when:
        handlers.notFound(new NotFoundException('No account exists with id 42'), context)

        then:
        answered.status() == 404
        answered.title() == 'Not found'
        answered.detail() == 'No account exists with id 42'
    }

    def "every problem is answered as problem json"() {
        when:
        handlers.unexpected(new IllegalStateException('boom'), context)

        then:
        1 * context.contentType('application/problem+json') >> context
    }
}

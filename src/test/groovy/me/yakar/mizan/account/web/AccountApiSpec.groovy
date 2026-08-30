package me.yakar.mizan.account.web

import com.atlassian.oai.validator.OpenApiInteractionValidator
import com.atlassian.oai.validator.model.Request
import com.atlassian.oai.validator.model.SimpleResponse
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import me.yakar.mizan.Mizan
import me.yakar.mizan.platform.config.ApplicationConfiguration
import me.yakar.mizan.support.PostgresSpec
import spock.lang.Shared

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Drives the running service over HTTP and holds every response against the published OpenAPI
 * contract, so that a response which drifts from the contract fails the build even when its
 * behaviour looks right.
 */
class AccountApiSpec extends PostgresSpec {

    @Shared
    Mizan mizan

    @Shared
    HttpClient client = HttpClient.newHttpClient()

    @Shared
    OpenApiInteractionValidator contract

    def setupSpec() {
        mizan = Mizan.start(new ApplicationConfiguration(databaseConfiguration(), freePort()))
        contract = OpenApiInteractionValidator
                .createFor(getClass().getResource('/openapi/mizan.yaml').toString())
                .build()
    }

    def cleanupSpec() {
        mizan?.close()
    }

    def "opening an account answers with the created account and its location"() {
        when:
        def response = openAccount(ownerName: 'Ahmet Yakar', currency: 'TRY')

        then:
        response.statusCode() == 201
        response.headers().firstValue('Location').get() == "/accounts/${body(response).id}"

        and:
        def account = body(response)
        account.ownerName == 'Ahmet Yakar'
        account.currency == 'TRY'
        account.openedAt != null

        and:
        honoursContract('/accounts', Request.Method.POST, response)
    }

    def "a newly opened account has a zero balance"() {
        expect:
        body(openAccount(ownerName: 'Ahmet Yakar', currency: 'TRY')).balance == '0.00'
    }

    def "an opened account can be read back by its id"() {
        given:
        def opened = body(openAccount(ownerName: 'Ahmet Yakar', currency: 'TRY'))

        when:
        def response = showAccount(opened.id)

        then:
        response.statusCode() == 200
        body(response) == opened

        and:
        honoursContract('/accounts/{id}', Request.Method.GET, response)
    }

    def "reading an account that does not exist answers with a problem document"() {
        when:
        def response = showAccount(UUID.randomUUID().toString())

        then:
        response.statusCode() == 404
        contentType(response).startsWith('application/problem+json')
        body(response).detail.contains('No account exists')

        and:
        honoursContract('/accounts/{id}', Request.Method.GET, response)
    }

    def "reading an account with a malformed id is rejected"() {
        when:
        def response = showAccount('not-a-uuid')

        then:
        response.statusCode() == 400
        contentType(response).startsWith('application/problem+json')
        body(response).field == 'id'

        and:
        honoursContract('/accounts/{id}', Request.Method.GET, response)
    }

    def "an unusable owner name is rejected and names the offending field"() {
        when:
        def response = openAccount(ownerName: ownerName, currency: 'TRY')

        then:
        response.statusCode() == 400
        body(response).field == 'ownerName'

        and:
        honoursContract('/accounts', Request.Method.POST, response)

        where:
        ownerName << [null, '', '   ', 'y' * 101]
    }

    def "an unusable currency is rejected and names the offending field"() {
        when:
        def response = openAccount(ownerName: 'Ahmet Yakar', currency: currency)

        then:
        response.statusCode() == 400
        body(response).field == 'currency'

        and:
        honoursContract('/accounts', Request.Method.POST, response)

        where:
        currency << [null, '', '   ', 'TR', 'abc', 'XX1']
    }

    def "a body that is not valid json is rejected"() {
        when:
        def response = post('/accounts', '{ this is not json')

        then:
        response.statusCode() == 400
        body(response).title == 'Invalid request'

        and:
        honoursContract('/accounts', Request.Method.POST, response)
    }

    private HttpResponse<String> openAccount(Map<String, String> request) {
        post('/accounts', JsonOutput.toJson(request))
    }

    private HttpResponse<String> post(String path, String body) {
        client.send(
                HttpRequest.newBuilder(uri(path))
                        .header('Content-Type', 'application/json')
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString())
    }

    private HttpResponse<String> showAccount(String id) {
        client.send(
                HttpRequest.newBuilder(uri("/accounts/${id}")).GET().build(),
                HttpResponse.BodyHandlers.ofString())
    }

    private URI uri(String path) {
        URI.create("http://localhost:${mizan.port()}${path}")
    }

    private static Object body(HttpResponse<String> response) {
        new JsonSlurper().parseText(response.body())
    }

    private static String contentType(HttpResponse<String> response) {
        response.headers().firstValue('Content-Type').orElse('')
    }

    /**
     * Validates the response only. The requests these tests send are deliberately invalid in the
     * rejection cases, so validating them against the contract would report the very thing the
     * test is arranging.
     */
    private void honoursContract(String path, Request.Method method, HttpResponse<String> response) {
        def builder = SimpleResponse.Builder
                .status(response.statusCode())
                .withBody(response.body())
        response.headers().map().each { name, values ->
            values.each { builder.withHeader(name, it) }
        }
        def report = contract.validateResponse(path, method, builder.build())
        assert !report.hasErrors(): report.toString()
    }

    private static int freePort() {
        new ServerSocket(0).withCloseable { it.localPort }
    }
}

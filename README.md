# mizan

A double-entry ledger service: account balances and money movement that can be audited, and that cannot silently go
wrong.

> **mizan** *(Turkish, from Arabic — "scales")* — in accounting, the trial balance: the check that
> every entry has its counterpart and the books sum to zero. That check is this project's core
> invariant.

**Work in progress.** One goal of eight is done; the table below is the honest state of the repo.

## Goals

| # | Goal                                                                                    | Status         |
|---|-----------------------------------------------------------------------------------------|----------------|
| 1 | Open an account and query its balance                                                   | ✅ Done         |
| 2 | Record deposits as double-entry postings — every movement has two sides that cancel out | Not started    |
| 3 | Transfer between accounts; a balance never goes negative                                | Not started    |
| 4 | Move money exactly once per request, however many times a client retries                | Not started    |
| 5 | Stay correct under concurrency — money neither lost nor created                         | Not started    |
| 6 | Account statements: every movement traceable, nothing deleted or rewritten              | Not started    |
| 7 | Publish events reliably — money never moves while the event vanishes                    | Not started    |
| 8 | Correct mistakes with reversing entries, leaving history intact                         | Not started    |

## API

| | |
|---|---|
| `POST /accounts` | Opens an account. Answers `201` with the account and its `Location`. |
| `GET /accounts/{id}` | Reads an account and its balance. |

Rejections answer `application/problem+json` ([RFC 7807](https://www.rfc-editor.org/rfc/rfc7807))
and name the field at fault. The full contract is in
[`src/main/resources/openapi/mizan.yaml`](src/main/resources/openapi/mizan.yaml), and every
response the tests receive is validated against it.

Balances are never stored. An account's balance is the sum of its entries, so the ledger cannot
disagree with itself.

## Running it

Start PostgreSQL, then the service:

```bash
docker compose up -d
./gradlew run
```

The service listens on `8080`. Settings are read from the environment — see
[`.env.example`](.env.example) for the full list; `./gradlew run` falls back to local defaults for
the database.

```bash
curl -i -X POST localhost:8080/accounts \
  -H 'Content-Type: application/json' \
  -d '{"ownerName":"Ada Lovelace","currency":"TRY"}'
```

## Building it

```bash
./gradlew build
```

Code generation, tests and coverage all need Docker: jOOQ classes are generated from the Flyway
migrations against a throwaway database, and the tests run against a real PostgreSQL through
Testcontainers. Coverage is enforced at 90%; `./gradlew pitest` runs mutation testing.

## Built with

| Part             | Technology                                    |
|------------------|-----------------------------------------------|
| Language         | Java 21                                       |
| Build            | Gradle                                        |
| REST layer       | Javalin                                       |
| Database         | PostgreSQL                                    |
| Database access  | jOOQ                                          |
| Migrations       | Flyway                                        |
| Tests            | Spock                                         |
| Test database    | Testcontainers                                |
| Event publishing | Kafka                                         |
| Supporting       | HikariCP · Jackson · SLF4J/Logback · Spotless |

No Spring, and no dependency injection container: wiring, transaction boundaries and the request
lifecycle are meant to be visible in the code that runs them.

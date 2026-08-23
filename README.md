# mizan

A double-entry ledger service: account balances and money movement that can be audited, and that cannot silently go
wrong.

> **mizan** *(Turkish, from Arabic — "scales")* — in accounting, the trial balance: the check that
> every entry has its counterpart and the books sum to zero. That check is this project's core
> invariant.

**Work in progress.** Nothing below is finished yet.

## Goals

| # | Goal                                                                                    | Status         |
|---|-----------------------------------------------------------------------------------------|----------------|
| 1 | Open an account and query its balance                                                   | 🚧 In progress |
| 2 | Record deposits as double-entry postings — every movement has two sides that cancel out | Not started    |
| 3 | Transfer between accounts; a balance never goes negative                                | Not started    |
| 4 | Move money exactly once per request, however many times a client retries                | Not started    |
| 5 | Stay correct under concurrency — money neither lost nor created                         | Not started    |
| 6 | Account statements: every movement traceable, nothing deleted or rewritten              | Not started    |
| 7 | Publish events reliably — money never moves while the event vanishes                    | Not started    |
| 8 | Correct mistakes with reversing entries, leaving history intact                         | Not started    |

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

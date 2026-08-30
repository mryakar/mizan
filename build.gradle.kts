buildscript {
    repositories {
        mavenCentral()
    }
    configurations.classpath {
        resolutionStrategy.force(
            "org.jooq:jooq:3.20.18",
            "org.jooq:jooq-meta:3.20.18",
            "org.jooq:jooq-codegen:3.20.18",
        )
    }
}

plugins {
    java
    groovy
    jacoco
    application
    alias(libs.plugins.spotless)
    alias(libs.plugins.jooq.docker)
    alias(libs.plugins.pitest)
}

group = "me.yakar"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass.set("me.yakar.mizan.MizanApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    jdbc(libs.postgresql)

    implementation(libs.jooq)
    implementation(libs.flyway.core)
    implementation(libs.hikaricp)
    implementation(libs.javalin)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.jsr310)

    runtimeOnly(libs.flyway.postgresql)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.groovy.core)
    testImplementation(libs.groovy.json)
    testImplementation(libs.spock.core)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.swagger.request.validator)
    testRuntimeOnly(libs.junit.platform.launcher)
}

jooq {
    image {
        tag = "18.6"
    }
}

tasks {
    generateJooqClasses {
        schemas = arrayOf("public")
        basePackageName = "me.yakar.mizan.db"
        outputDirectory.set(layout.buildDirectory.dir("generated-jooq"))
    }
}

tasks.named<JavaExec>("run") {
    listOf(
        "MIZAN_DB_URL" to "jdbc:postgresql://localhost:5432/mizan",
        "MIZAN_DB_USERNAME" to "mizan",
        "MIZAN_DB_PASSWORD" to "mizan",
    ).forEach { (name, fallback) ->
        environment(name, providers.environmentVariable(name).getOrElse(fallback))
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
    }
    finalizedBy(tasks.jacocoTestReport)
}

private val generatedPackages = listOf("me/yakar/mizan/db/**")

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map { fileTree(it) { exclude(generatedPackages) } }),
    )
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    classDirectories.setFrom(
        files(classDirectories.files.map { fileTree(it) { exclude(generatedPackages) } }),
    )
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                minimum = "0.90".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

pitest {
    targetClasses.set(listOf("me.yakar.mizan.*"))
    excludedClasses.set(listOf("me.yakar.mizan.db.*"))
    junit5PluginVersion.set("1.2.3")
    mutators.set(listOf("STRONGER"))
    timestampedReports.set(false)
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

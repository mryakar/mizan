plugins {
    java
    groovy
    jacoco
    application
    alias(libs.plugins.spotless)
    alias(libs.plugins.jooq.docker)
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

    runtimeOnly(libs.flyway.postgresql)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.groovy)
    testImplementation(libs.spock.core)
    testImplementation(libs.testcontainers.postgresql)
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

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
    }
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
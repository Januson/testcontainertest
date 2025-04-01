import com.adarshr.gradle.testlogger.theme.ThemeType
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("application")
    id("java")
    id("io.micronaut.application") version "4.4.4"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    alias(libs.plugins.gradle.test.logger)
}

group = "org.test.wiremock"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.test.testcontainers.*")
    }
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
//    compileOnly("io.micronaut:micronaut-http-client")
//    runtimeOnly("ch.qos.logback:logback-classic")

    testImplementation(libs.assertj)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.wiremock)
    testImplementation(libs.wiremock)

    testRuntimeOnly(libs.junit.launcher)
}

javaToolchains {
    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("org.test.testcontainers.Application")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.test.testcontainers.Application"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

testlogger {
    theme = ThemeType.STANDARD
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = true
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showOnlySlow = false
    showStandardStreams = true
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel = LogLevel.LIFECYCLE
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("app")
    archiveClassifier.set("")
}

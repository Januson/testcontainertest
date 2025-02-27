plugins {
    id("java")
    alias(libs.plugins.gradle.test.logger)
}

group = "org.test.wiremock"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
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

tasks.test {
    useJUnitPlatform()
}

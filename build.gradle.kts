import org.gradle.api.tasks.testing.logging.TestLogEvent


plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.6"
}

group = "it.danielemegna"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("it.danielemegna.tennis.web.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-jetty")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("io.ktor:ktor-server-status-pages")
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging.events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    outputs.upToDateWhen { false }
}

kotlin {
    jvmToolchain(8)
}
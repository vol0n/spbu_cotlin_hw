import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.*

plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    application
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.6.0")
    implementation("com.charleskorn.kaml:kaml:0.28.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
    implementation("com.squareup:kotlinpoet:1.7.2")

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.14.2")
}

detekt {
    failFast = true // fail build on any finding
    autoCorrect = false
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events(TestLogEvent.STANDARD_ERROR, TestLogEvent.STARTED, TestLogEvent.PASSED,
        TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Werror")
    }
}

application {
    mainClass.set("MainKt")
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.*

plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    id("org.jetbrains.dokka") version "1.4.20"
    id("org.openjfx.javafxplugin") version "0.0.8"
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.2")

    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.openjfx:javafx-base:11.0.2")
    implementation("org.openjfx:javafx:11.0.2")
    implementation("org.openjfx:javafx-controls:11.0.2")

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.14.2")

    // client
    implementation("io.ktor:ktor-client-websockets:1.5.4")
    implementation("io.ktor:ktor-client-cio:1.5.4")
    implementation("io.ktor:ktor-serialization:1.5.4")
    implementation("io.ktor:ktor-client-gson:1.5.4")
    implementation("io.ktor:ktor-client-serialization:1.5.3")

    // server
    implementation("io.ktor:ktor-server-netty:1.5.4")
    implementation("io.ktor:ktor-websockets:1.5.4")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

javafx {
    modules("javafx.controls", "javafx.graphics")
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

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Werror")
    }
}

application {
    mainClass.set("homework1.Task_3Kt")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
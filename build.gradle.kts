import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

description = "Changelog Gradle Plugin"
group = "top.abosen.plugins"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains:markdown:0.2.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

gradlePlugin {
    plugins.create("changelog") {
        id = "top.abosen.plugins.changelog"
        implementationClass = "top.abosen.plugins.changelog.ChangelogPlugin"
        displayName = "Changelog Gradle Plugin"
        description = "Provides tasks and helper methods for handling changelog in the Project."
    }
}

detekt {
    config.from(file("detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    listOf("compileKotlin", "compileTestKotlin").forEach {
        getByName<KotlinCompile>(it) {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    withType<Detekt>().configureEach {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

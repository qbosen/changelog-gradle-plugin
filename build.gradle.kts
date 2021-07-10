import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    kotlin("jvm") version "1.5.20"
}

description = "Changelog Gradle Plugin"
group = "top.abosen.plugins"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    implementation("org.jetbrains:markdown:0.2.4")
}

gradlePlugin {
    plugins.create("changelog") {
        id = "top.abosen.plugins.changelog"
        implementationClass = "top.abosen.plugins.changelog.ChangelogPlugin"
        displayName = "Changelog Gradle Plugin"
        description = "Provides tasks and helper methods for handling changelog in the Project."
    }
}

tasks {
    listOf("compileKotlin", "compileTestKotlin").forEach {
        getByName<KotlinCompile>(it) {
            kotlinOptions.apiVersion = "1.5"
            kotlinOptions.languageVersion = "1.5"
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}

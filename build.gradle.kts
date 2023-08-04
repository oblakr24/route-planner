// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories { mavenCentral() }

    dependencies {
        classpath("app.cash.molecule:molecule-gradle-plugin:1.1.0")
        val kotlinVersion = "1.8.21"
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    id("com.google.dagger.hilt.android") version "2.46.1" apply false
}
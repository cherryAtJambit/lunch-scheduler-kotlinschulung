rootProject.name = "kotlin-jambit"

pluginManagement {
    plugins {
        val kotlinVersion = "1.3.72"

        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.jetbrains.gradle.plugin.idea-ext") version "0.7"
    }
}

include(":exposed")
include(":koin")


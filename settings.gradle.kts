rootProject.name = "kotlin-jambit-2"

pluginManagement {
    plugins {
        val kotlinVersion = "1.3.72"

        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.jetbrains.gradle.plugin.idea-ext") version "0.7"
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        id("org.springframework.boot") version "2.3.0.RELEASE"
        id("io.spring.dependency-management") version "1.0.9.RELEASE"
    }
}

include(":exposed")
include(":koin")
include(":spring")
include(":spring-reactive")
include(":coroutines")
include(":r2dbc")
include(":misc")
include(":ktor")
include(":lunch-scheduler")
//include(":mpp-chat")


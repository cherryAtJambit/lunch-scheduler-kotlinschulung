import org.jetbrains.gradle.ext.ModuleSettings
import org.jetbrains.gradle.ext.PackagePrefixContainer

val kotestVersion: String by rootProject
val log4jVersion: String by rootProject
val mockkVersion: String by rootProject
val junitVersion: String by rootProject
val ktorVersion: String by rootProject
val jerseyClientVersion: String by rootProject

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

idea {
    module {
        this as ExtensionAware
        configure<ModuleSettings> {
            this as ExtensionAware
            configure<PackagePrefixContainer> {
                this["src/main/kotlin"] = "de.e2.ktor"
                this["src/test/kotlin"] = "de.e2.ktor"
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")

    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")

    implementation("io.ktor:ktor-websockets:$ktorVersion")

    implementation("org.glassfish.jersey.core:jersey-client:$jerseyClientVersion")
    implementation("org.glassfish.jersey.inject:jersey-hk2:$jerseyClientVersion")
    implementation("org.glassfish.jersey.media:jersey-media-sse:$jerseyClientVersion")
    implementation("io.github.microutils:kotlin-logging:1.7.9")


    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
}


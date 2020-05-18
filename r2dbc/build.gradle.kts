import org.jetbrains.gradle.ext.ModuleSettings
import org.jetbrains.gradle.ext.PackagePrefixContainer

val kotestVersion: String by rootProject
val h2Version: String by rootProject
val r2dbcVersion: String by rootProject
val r2dbcPoolVersion: String by rootProject
val r2dbcH2Version: String by rootProject
val log4jVersion: String by rootProject

plugins {
    kotlin("jvm")
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
                this["src/main/kotlin"] = "de.e2.exposed"
                this["src/test/kotlin"] = "de.e2.exposed"
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.h2database:h2:$h2Version")
    implementation("io.r2dbc:r2dbc-h2:$r2dbcH2Version")
    implementation("io.r2dbc:r2dbc-spi:$r2dbcVersion")
    implementation("io.r2dbc:r2dbc-pool:$r2dbcPoolVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.6")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")

    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
}


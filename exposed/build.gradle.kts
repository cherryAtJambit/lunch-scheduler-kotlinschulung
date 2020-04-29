import org.jetbrains.gradle.ext.ModuleSettings
import org.jetbrains.gradle.ext.PackagePrefixContainer

val kotestVersion: String by rootProject
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
            }
        }
    }
}

dependencies {
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")

    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
}


import org.jetbrains.gradle.ext.ModuleSettings
import org.jetbrains.gradle.ext.PackagePrefixContainer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotestVersion: String by rootProject
val log4jVersion: String by rootProject
val mockkVersion: String by rootProject

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
                this["src/main/kotlin"] = "de.e2.misc"
                this["src/test/kotlin"] = "de.e2.misc"
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
}

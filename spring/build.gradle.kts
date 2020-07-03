import org.jetbrains.gradle.ext.ModuleSettings
import org.jetbrains.gradle.ext.PackagePrefixContainer

val kotestVersion: String by rootProject
val mockkVersion: String by rootProject
val springMockkVersion: String by rootProject

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.jetbrains.gradle.plugin.idea-ext")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
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
                this["src/main/kotlin"] = "de.e2.spring"
                this["src/test/kotlin"] = "de.e2.spring"
            }
        }
    }
}

configurations.all {
    exclude(module = "spring-boot-starter-logging")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")

}

springBoot {
    mainClassName ="de.e2.spring.annotations.AdresseAnnotationApplicationKt"
}

import org.jetbrains.gradle.ext.ModuleSettings
import org.jetbrains.gradle.ext.PackagePrefixContainer

val kotestVersion: String by rootProject
val log4jVersion: String by rootProject
val koinVersion: String by rootProject
val mockkVersion: String by rootProject
val junitVersion: String by rootProject
val ktorVersion: String by rootProject
val exposedVersion: String by rootProject
val h2Version: String by rootProject
val kotlinLoggingVersion: String by rootProject


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
                this["src/main/kotlin"] = "de.e2.lunch_scheduler"
                this["src/test/kotlin"] = "de.e2.lunch_scheduler"
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.koin:koin-core-ext:$koinVersion")
    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation("org.koin:koin-test:$koinVersion")
    
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.junit.vintage:junit-vintage-engine:$junitVersion")

    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
}


import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinCoroutineVersion by extra("1.3.5")
val ktorVersion: String by extra { "1.3.2" }
val junitVersion: String by extra { "5.6.2" }
val kotestVersion: String by extra { "4.1.0.RC2" }
val log4jVersion: String by extra { "2.13.1" }
val jsonpathVersion: String by extra { "2.4.0" }
val mockkVersion: String by extra { "1.10.0" }
val springMockkVersion: String by extra { "2.0.1" }
val exposedVersion: String by extra { "0.23.1" }
val h2Version: String by extra { "1.4.200" }
val koinVersion: String by extra {"2.1.5"}
val jsonPathVersion: String by extra {"2.4.0"}
val jerseyClientVersion: String by extra {"2.30.1"}
val r2dbcVersion: String by extra {"0.8.1.RELEASE"}
val r2dbcPoolVersion: String by extra {"0.8.2.RELEASE"}
val r2dbcH2Version: String by extra {"0.8.3.RELEASE"}
val jaxbVersion: String by extra {"2.3.1"}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://kotlin.bintray.com/kotlinx")
    }
    group = "de.e2"
    version = "1.0.0-SNAPSHOT"

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.Experimental","-XXLanguage:+InlineClasses")
    }

    tasks.withType<Test>().configureEach() {
        useJUnitPlatform()
    }

    configurations.all {
        exclude(group = "ch.qos.logback")
    }
}

plugins {
    kotlin("jvm") apply false
}






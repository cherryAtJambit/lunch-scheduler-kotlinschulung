import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinCoroutineVersion by extra("1.3.5")
val ktorVersion: String by extra { "1.3.2" }
val junitVersion: String by extra { "5.6.2" }
val kotestVersion: String by extra { "4.0.5" }
val log4jVersion: String by extra { "2.13.1" }
val jsonpathVersion: String by extra { "2.4.0" }
val mockkVersion: String by extra { "1.10.0" }

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
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.Experimental")
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






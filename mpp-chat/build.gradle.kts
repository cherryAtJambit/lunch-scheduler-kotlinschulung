import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val ktorVersion: String by extra {"1.2.5"} //Mit 1.3.2 läuft es nicht
val kotlinCoroutineVersion: String by rootProject.extra
val serializationVersion: String by extra {"0.20.0"}


kotlin {
    jvm()
    js {

        browser {
        }
    }
    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
            }
        }
        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutineVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")

                runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.12.0")
                runtimeOnly("org.apache.logging.log4j:log4j-core:2.12.0")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
            }
        }
        getByName("jsMain") {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
                implementation(npm("text-encoding", "^0.7.0"))
                implementation(npm("abort-controller", "^3.0.0"))
            }
        }
    }
}


tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
}

tasks {
    val jsBrowserProductionWebpack by getting(KotlinWebpack::class) {
        outputFileName = "main.js"
    }
    val jvmJar by getting(Jar::class) {
        dependsOn(jsBrowserProductionWebpack)
        from(jsBrowserProductionWebpack.outputFile)
    }

    val runChatServer by creating(JavaExec::class) {
        group = "chat"
        main = "de.e2.ktor.multiplatform.ChatServerKt"
        dependsOn(jvmJar)
        classpath(configurations.getByName("jvmRuntimeClasspath"), jvmJar)
    }
}
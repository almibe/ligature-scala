plugins {
    kotlin("multiplatform")// version "1.7.10"
//    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotest.multiplatform)
}

group = "dev.ligature"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    targets {
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }
//        js(IR) {
//            browser()
//            //nodejs()
//        }
//        linuxX64()
//        macosX64()
//        mingwX64()
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                verbose = true
            }
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":ligature"))
                implementation(project(":lig"))
                implementation(project(":wander"))
                implementation(project(":idgen"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.arrow.core)
                implementation("io.ktor:ktor-server-core:2.0.3")
                implementation("io.ktor:ktor-server-netty:2.0.3")
                implementation("io.ktor:ktor-server-test-host:2.0.3")
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.framework.datatest)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.framework.datatest)
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    kotlinOptions {
//        apiVersion = "1.5"
//    }
//}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
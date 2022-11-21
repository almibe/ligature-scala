plugins {
  id("dev.ligature.kotlin-library-conventions")
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
    jvm { compilations.all { kotlinOptions { jvmTarget = "1.8" } } }
    //        js(IR) {
    //            browser()
    //            //nodejs()
    //        }
    //        linuxX64()
    //        macosX64()
    //        mingwX64()
  }

  targets.all { compilations.all { kotlinOptions { verbose = true } } }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":ligature"))
        implementation(project(":idgen"))
        implementation(project(":gaze"))
        implementation(project(":lig"))
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.arrow.core)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.kotest.assertions.core)
        implementation(libs.kotest.framework.engine)
        implementation(libs.kotest.framework.datatest)
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }

    val jvmTest by getting { dependencies { implementation(libs.kotest.runner.junit5) } }
  }
}

// tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    kotlinOptions {
//        apiVersion = "1.5"
//    }
// }

tasks.named<Test>("jvmTest") {
  useJUnitPlatform()
  filter { isFailOnNoMatchingTests = false }
  testLogging {
    showExceptions = true
    showStandardStreams = true
    events =
        setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}

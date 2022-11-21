plugins {
  //TODO this shouldn't be a multiplatform project
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
  targets { jvm { compilations.all { kotlinOptions { jvmTarget = "1.8" } } } }

  targets.all { compilations.all { kotlinOptions { verbose = true } } }

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
  }
}

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

plugins {
  //TODO this isn't a multiplatform project, but still uses multiple platform
  // based on this plugin
  id("dev.ligature.kotlin-library-conventions")
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
        implementation("ch.qos.logback:logback-classic:1.2.11")
      }
    }
  }
}

// tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    kotlinOptions {
//        apiVersion = "1.5"
//    }
// }

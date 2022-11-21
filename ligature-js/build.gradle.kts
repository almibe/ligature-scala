plugins {
  id("dev.ligature.kotlin-library-conventions")
  id("dev.petuska.npm.publish") version "3.1.0"
}

group = "dev.ligature"

version = "0.1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

kotlin {
  js(IR) {
    browser()
    binaries.library()
  }

  targets.all { compilations.all { kotlinOptions { verbose = true } } }

  sourceSets {
    val commonMain by getting {
      dependencies {
//        implementation(project(":ligature"))
//        implementation(project(":lig"))
//        implementation(project(":wander"))
//        implementation(project(":ligature-in-memory"))
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.arrow.core)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.kotest.assertions.core)
        implementation(libs.kotest.framework.engine)
        implementation(libs.kotest.framework.datatest)
      }
    }
  }
}

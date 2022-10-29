plugins {
//    alias(libs.plugins.kotlin.multiplatform)
//    alias(libs.plugins.kotest.multiplatform)
  kotlin
  application
}

group = "dev.ligature"
version = "0.1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
  implementation(project(":ligature"))
  implementation(project(":idgen"))
  implementation(project(":ligature-in-memory"))
  implementation(project(":ligature-http"))
  implementation("io.ktor:ktor-server-core:2.0.3")
  implementation("io.ktor:ktor-server-netty:2.0.3")
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.arrow.core)
  testImplementation(project(":ligature-http-test-suite"))
  testImplementation(libs.kotest.assertions.core)
  testImplementation(libs.kotest.framework.engine)
  testImplementation(libs.kotest.framework.datatest)
  testImplementation(libs.kotest.runner.junit5)
}

//        val jvmTest by getting {
//            dependencies {
//                implementation(project(":ligature-http-test-suite"))
//                implementation(libs.kotest.assertions.core)
//                implementation(libs.kotest.framework.engine)
//                implementation(libs.kotest.framework.datatest)
//                implementation(libs.kotest.runner.junit5)
//            }
//        }
//    }
//}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

application {
  mainClass.set("dev.ligature.http.memory.LigatureHttpInMemoryKt")
}

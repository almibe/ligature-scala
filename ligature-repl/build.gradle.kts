plugins {
//    alias(libs.plugins.kotlin.multiplatform)
//    alias(libs.plugins.kotest.multiplatform)
  kotlin
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
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
    implementation(project(":ligature-xodus"))
    implementation(project(":ligature-http"))
    implementation("io.ktor:ktor-server-core:2.0.3")
    implementation("io.ktor:ktor-server-netty:2.0.3")
    //runtimeOnly("org.fusesource.jansi:jansi:2.4.0")
    runtimeOnly("net.java.dev.jna:jna:5.3.1")
    implementation("org.jline:jline:3.21.0")
    runtimeOnly("org.jline:jline-terminal-jna:3.21.0")
//    implementation("org.graalvm.js:js:22.0.0")
//    implementation("org.graalvm.js:js-scriptengine:22.0.0")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.6.21") //TODO don't hard code version
//    runtimeOnly("org.jetbrains.kotlin:kotlin-main-kts:1.6.21")
    //implementation("org.apache.groovy:groovy-all:4.0.4")
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
  mainClass.set("dev.ligature.repl.LigatureReplKt")
}

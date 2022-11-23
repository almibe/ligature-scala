plugins {
  kotlin("js")
}

kotlin {
  js(IR) {
    browser()
    binaries.library()
  }
}

repositories {
  mavenCentral()
}

dependencies {
  //  // Align versions of all Kotlin components
  //  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  //
  //  // Use the Kotlin JDK 8 standard library.
  //  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  //
  //  // Align versions of all Kotlin components
  //  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
}

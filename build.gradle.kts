plugins { id("org.jetbrains.kotlinx.kover") version "0.5.0" }

repositories {
  gradlePluginPortal()
}

buildscript { repositories { mavenCentral() } }

allprojects { repositories { mavenCentral() } }

plugins {
  id("dev.ligature.kotlin-library-conventions")
  alias(libs.plugins.kotest.multiplatform)
  id("io.gitlab.arturbosch.detekt").version("1.21.0")
}

group = "dev.ligature"

version = "0.1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
  gradlePluginPortal()
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

// Test code below
detekt {
  // Version of Detekt that will be used. When unspecified the latest detekt
  // version found will be used. Override to stay on the same version.
  toolVersion = "1.21.0"

  // The directories where detekt looks for source files.
  // Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
  source = files("src/commonMain/kotlin")

  // Builds the AST in parallel. Rules are always executed in parallel.
  // Can lead to speedups in larger projects. `false` by default.
  parallel = false

  // Define the detekt configuration(s) you want to use.
  // Defaults to the default detekt configuration.
  //    config = files("path/to/config.yml")

  // Applies the config files on top of detekt's default config file. `false` by default.
  buildUponDefaultConfig = false

  // Turns on all the rules. `false` by default.
  allRules = false

  // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
  //    baseline = file("path/to/baseline.xml")

  // Disables all default detekt rulesets and will only run detekt with custom rules
  // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
  disableDefaultRuleSets = false

  // Adds debug output during task execution. `false` by default.
  debug = false

  // If set to `true` the build does not fail when the
  // maxIssues count was reached. Defaults to `false`.
  ignoreFailures = false

  // Android: Don't create tasks for the specified build types (e.g. "release")
  //    ignoredBuildTypes = listOf("release")

  // Android: Don't create tasks for the specified build flavor (e.g. "production")
  //    ignoredFlavors = listOf("production")

  // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
  //    ignoredVariants = listOf("productionRelease")

  // Specify the base path for file paths in the formatted reports.
  // If not set, all file paths reported will be absolute file path.
  //    basePath = projectDir
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach { this.jvmTarget = "1.8" }

tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
  this.jvmTarget = "1.8"
}

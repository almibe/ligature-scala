lazy val rcVersion = "3.1.0"

ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "ligature"

val vertxWeb = "io.vertx" % "vertx-web" % "4.1.5"
val vertxWebClient = "io.vertx" % "vertx-web-client" % "4.1.5"
val gson = "com.google.code.gson" % "gson" % "2.8.6"

lazy val ligature = crossProject(JSPlatform, JVMPlatform).in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := rcVersion,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.0",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test,
  )

lazy val lig = crossProject(JSPlatform, JVMPlatform).in(file("lig"))
  .settings(
      name := "lig",
      scalaVersion := rcVersion,
      libraryDependencies += "dev.ligature" %%% "gaze" % "0.1.0-SNAPSHOT",
      libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.0",
      libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test,
  ).dependsOn(ligature)

lazy val ligatureTestSuite = crossProject(JSPlatform, JVMPlatform).in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := rcVersion,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.0",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1",
  ).dependsOn(ligature)

lazy val ligatureInMemory = crossProject(JSPlatform, JVMPlatform).in(file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    scalaVersion := rcVersion,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.0",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test,
  ).dependsOn(ligature, ligatureTestSuite % Test)

// lazy val ligatureIndexedDB = (project in file("ligature-indexeddb"))
//   .enablePlugins(ScalaJSPlugin)
//   .settings(
//     name := "ligature-indexeddb",
//     scalaVersion := rcVersion,
//     scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
//     scalaJSUseMainModuleInitializer := true,

//     libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.0",
//     libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test,
// //    testFrameworks += new TestFramework(""org.scalameta" %%% "munit" % "1.0.0-M1".Framework"),
//   ).dependsOn(ligature, ligatureTestSuite % Test)

// lazy val slonky = (project in file("slonky"))
//   .settings(
//     name := "slonky",
//     scalaVersion := rcVersion,
//     libraryDependencies += fs2,
//     libraryDependencies += vertxWeb,
//     libraryDependencies += vertxWebClient,
//     libraryDependencies += gson,
//     libraryDependencies += munit % Test,
//     testFrameworks += new TestFramework("munit.Framework"),
//     mainClass in (Compile, run) := Some("dev.ligature.slonky.Slonky"),
//   ).dependsOn(ligature, ligatureInMemory)

//addCommandAlias("run", "slonky/run")

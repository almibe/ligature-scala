lazy val rcVersion = "3.1.0"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val vertxWeb = "io.vertx" % "vertx-web" % "4.1.5"
val vertxWebClient = "io.vertx" % "vertx-web-client" % "4.1.5"
val gson = "com.google.code.gson" % "gson" % "2.8.6"

lazy val ligature = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := rcVersion,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.3",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )

lazy val gaze = crossProject(JSPlatform, JVMPlatform)
  .in(file("gaze"))
  .settings(
    name := "gaze",
    scalaVersion := rcVersion,
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )

lazy val idgen = crossProject(JSPlatform, JVMPlatform)
  .in(file("idgen"))
  .settings(
    name := "idgen",
    scalaVersion := rcVersion,
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )

lazy val ligatureRepl = crossProject(JVMPlatform)
  .in(file("ligature-repl"))
  .settings(
    name := "ligature-repl",
    scalaVersion := rcVersion,
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )

lazy val lig = crossProject(JSPlatform, JVMPlatform)
  .in(file("lig"))
  .settings(
    name := "lig",
    scalaVersion := rcVersion,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.3",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )
  .dependsOn(ligature, gaze, idgen)

lazy val wander = crossProject(JSPlatform, JVMPlatform)
  .in(file("wander"))
  .settings(
    name := "wander",
    scalaVersion := rcVersion,
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )
  .dependsOn(ligature, lig, gaze)

lazy val ligatureTestSuite = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := rcVersion,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.3",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1",
    libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"
  )
  .dependsOn(ligature)

lazy val ligatureInMemory = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    scalaVersion := rcVersion,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.3",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test,
    libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"
  )
  .dependsOn(ligature, ligatureTestSuite % Test)

lazy val ligatureIndexedDB = crossProject(JSPlatform)
  .in(file("ligature-indexeddb"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "ligature-indexeddb",
    scalaVersion := rcVersion,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.3",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )
  .dependsOn(ligature, ligatureTestSuite % Test)

lazy val ligatureJS = crossProject(JSPlatform)
  .in(file("ligature-js"))
  .enablePlugins(ScalaJSPlugin)
  .jsSettings(
    name := "ligature-js",
    scalaVersion := rcVersion,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M1" % Test
  )
  .dependsOn(ligature, wander, ligatureTestSuite % Test)

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

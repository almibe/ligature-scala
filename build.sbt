lazy val scala3Version = "3.1.2"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

lazy val ligature = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.7",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val gaze = crossProject(JSPlatform, JVMPlatform)
  .in(file("gaze"))
  .settings(
    name := "gaze",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val idgen = crossProject(JSPlatform, JVMPlatform)
  .in(file("idgen"))
  .settings(
    name := "idgen",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val ligatureRepl = crossProject(JVMPlatform)
  .in(file("ligature-repl"))
  .settings(
    name := "ligature-repl",
    scalaVersion := scala3Version,
    libraryDependencies += "org.jline" % "jline-terminal-jansi" % "3.21.0",
    libraryDependencies += "org.jline" % "jline-reader" % "3.21.0",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val lig = crossProject(JSPlatform, JVMPlatform)
  .in(file("lig"))
  .settings(
    name := "lig",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.7",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .dependsOn(ligature, gaze, idgen)
  .disablePlugins(RevolverPlugin)

lazy val wander = crossProject(JSPlatform, JVMPlatform)
  .in(file("wander"))
  .settings(
    name := "wander",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .dependsOn(ligature, lig, gaze)
  .disablePlugins(RevolverPlugin)

lazy val ligatureTestSuite = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.7",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4",
    libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"
  )
  .dependsOn(ligature)
  .disablePlugins(RevolverPlugin)

lazy val ligatureInMemory = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.7",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test,
    libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"
  )
  .dependsOn(ligature, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

lazy val ligatureIndexedDB = crossProject(JSPlatform)
  .in(file("ligature-indexeddb"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "ligature-indexeddb",
    scalaVersion := scala3Version,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.7",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .dependsOn(ligature, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

lazy val ligatureJS = crossProject(JSPlatform)
  .in(file("ligature-js"))
  .enablePlugins(ScalaJSPlugin)
  .jsSettings(
    name := "ligature-js",
    scalaVersion := scala3Version,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test
  )
  .dependsOn(ligature, wander, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

val http4sVersion = "1.0.0-M32"

lazy val ligatureHttp = crossProject(JVMPlatform)
  .in(file("ligature-http"))
  .settings(
    name := "ligature-http",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % "3.2.7",
    libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion,
    libraryDependencies += "org.http4s" %% "http4s-ember-server" % http4sVersion,
    libraryDependencies += "org.http4s" %% "http4s-ember-client" % http4sVersion,
    libraryDependencies += "com.google.code.gson" % "gson" % "2.9.0",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M4" % Test,
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / run / mainClass := Some("dev.ligature.http.MainLigatureHttp")
  )
  .dependsOn(ligature, lig, wander, ligatureInMemory)

addCommandAlias("serve", "ligature-httpJVM/run")

disablePlugins(RevolverPlugin)

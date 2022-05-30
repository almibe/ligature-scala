lazy val scala3Version = "3.1.2"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion = "0.7.29"
val fs2Version = "3.2.7"

lazy val ligature = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % fs2Version,
    libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val gaze = crossProject(JSPlatform, JVMPlatform)
  .in(file("gaze"))
  .settings(
    name := "gaze",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val idgen = crossProject(JSPlatform, JVMPlatform)
  .in(file("idgen"))
  .settings(
    name := "idgen",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val lig = crossProject(JSPlatform, JVMPlatform)
  .in(file("lig"))
  .settings(
    name := "lig",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % fs2Version,
    libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test
  )
  .dependsOn(ligature, gaze, idgen)
  .disablePlugins(RevolverPlugin)

lazy val wander = crossProject(JSPlatform, JVMPlatform)
  .in(file("wander"))
  .settings(
    name := "wander",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test
  )
  .dependsOn(ligature, lig, gaze)
  .disablePlugins(RevolverPlugin)

lazy val ligatureTestSuite = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % fs2Version,
    libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test,
    libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"
  )
  .dependsOn(ligature)
  .disablePlugins(RevolverPlugin)

lazy val ligatureInMemory = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    scalaVersion := scala3Version,
  )
  .dependsOn(ligature, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

lazy val ligatureLmdb = crossProject(JVMPlatform)
  .in(file("ligature-lmdb"))
  .settings(
      name := "ligature-lmdb",
      scalaVersion := scala3Version,
      libraryDependencies += "co.fs2" %%% "fs2-core" % fs2Version,
      libraryDependencies += "org.lmdbjava" % "lmdbjava" % "0.8.2",
      libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test,
      libraryDependencies += "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7"
  )
  .dependsOn(ligature, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

//lazy val ligatureJS = crossProject(JSPlatform)
//  .in(file("ligature-js"))
//  .enablePlugins(ScalaJSPlugin)
//  .jsSettings(
//    name := "ligature-js",
//    scalaVersion := scala3Version,
//    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
//  )
//  .dependsOn(ligature, wander)
//  .disablePlugins(RevolverPlugin)

val http4sVersion = "1.0.0-M32"

lazy val ligatureHttp = crossProject(JVMPlatform)
  .in(file("ligature-http"))
  .settings(
    name := "ligature-http",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %%% "fs2-core" % fs2Version,
    libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion,
    libraryDependencies += "org.http4s" %% "http4s-ember-server" % http4sVersion,
    libraryDependencies += "org.http4s" %% "http4s-ember-client" % http4sVersion,
    libraryDependencies += "com.google.code.gson" % "gson" % "2.9.0",
    libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test,
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / run / mainClass := Some("dev.ligature.http.MainLigatureHttp")
  )
  .dependsOn(ligature, lig, wander, ligatureInMemory)

addCommandAlias("serve", "ligature-httpJVM/run")

disablePlugins(RevolverPlugin)

lazy val scala3Version = "3.4.3"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion = "1.0.0"
val jeromqVersion = "0.6.0"
val scalaLoggingVersion = "3.9.5"
val logBackVersion = "1.5.6"
val tsidVersion = "1.1.0"
val ulidVersion = "5.2.3"
val gsonVerison = "2.11.0"
val furyVersion = "0.4.1"
val lmdbVersion = "0.9.0"
val xodusVersion = "2.0.1"

lazy val gaze = project
  .in(file("gaze"))
  .settings(
    name := "gaze",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val idgen = project
  .in(file("idgen"))
  .settings(
    name := "idgen",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val ligature = project
  .in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "com.google.code.gson" % "gson" % gsonVerison,
    libraryDependencies += "org.furyio" % "fury-core" % furyVersion,
    libraryDependencies += "com.github.f4b6a3" % "ulid-creator" % ulidVersion,
    libraryDependencies += "io.hypersistence" % "tsid" % tsidVersion,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logBackVersion,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    libraryDependencies += "org.lmdbjava" % "lmdbjava" % lmdbVersion,
    libraryDependencies += "org.jetbrains.xodus" % "xodus-openAPI" % xodusVersion,
    libraryDependencies += "org.jetbrains.xodus" % "xodus-environment" % xodusVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .dependsOn(gaze)
  .disablePlugins(RevolverPlugin)

lazy val ligatureRepl = project
  .in(file("ligature-repl"))
  .settings(
    name := "ligature-repl",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logBackVersion,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    fork := true
  )
  .dependsOn(ligature, ligatureInMemory)

lazy val ligatureZeroMQ = project
  .in(file("ligature-zeromq"))
  .settings(
    name := "ligature-zeromq",
    scalaVersion := scala3Version,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logBackVersion,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    libraryDependencies += "org.zeromq" % "jeromq" % jeromqVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    fork := true
  )
  .dependsOn(ligature, ligatureInMemory)

lazy val ligatureTestSuite = project
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion
  )
  .dependsOn(ligature)
  .disablePlugins(RevolverPlugin)

lazy val ligatureInMemory = project
  .in(file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    scalaVersion := scala3Version
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

disablePlugins(RevolverPlugin)

addCommandAlias("cd", "project")

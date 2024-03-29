lazy val scala3Version = "3.3.3"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion = "1.0.0-M11"
val jeromqVersion = "0.6.0"
val scalaLoggingVersion = "3.9.5"
val logBackVersion = "1.5.1"
val tsidVersion = "1.1.0"
val ulidVersion = "5.2.3"
val gsonVerison = "2.10.1"
val furyVersion = "0.4.1"
val sqliteDep = "3.45.2.0"

lazy val ligature = project
  .in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

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

lazy val bend = project
  .in(file("bend"))
  .settings(
    name := "bend",
    scalaVersion := scala3Version,
    libraryDependencies += "com.google.code.gson" % "gson" % gsonVerison,
    libraryDependencies += "org.furyio" % "fury-core" % furyVersion,
    libraryDependencies += "com.github.f4b6a3" % "ulid-creator" % ulidVersion,
    libraryDependencies += "io.hypersistence" % "tsid" % tsidVersion,
    libraryDependencies += "org.jetbrains.xodus" % "xodus-environment" % xodusVersion,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logBackVersion,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .dependsOn(gaze, ligature, ligatureInMemory)
  .disablePlugins(RevolverPlugin)

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
  .dependsOn(bend, ligature, ligatureInMemory, ligatureXodus)

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

lazy val ligatureSqlite = project
  .in(file("ligature-sqlite"))
  .settings(
    name := "ligature-sqlite",
    scalaVersion := scala3Version,
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % sqliteVersion,
    libraryDependencies += "org.furyio" % "fury-core" % furyVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

disablePlugins(RevolverPlugin)

addCommandAlias("cd", "project")

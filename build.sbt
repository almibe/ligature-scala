lazy val scala3Version = "3.3.3"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion = "1.0.0-M11"
val xodusVersion = "2.0.1"
val jeromqVersion = "0.5.4"
val jlineVersion = "3.23.0"
val scalafxVersion = "16.0.0-R24"
val jansiVersion = "2.4.1"
val scalaLoggingVersion = "3.9.5"
val logBackVersion = "1.2.13"

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

lazy val ligatureXodus = project
  .in(file("ligature-xodus"))
  .settings(
    name := "ligature-xodus",
    scalaVersion := scala3Version,
    libraryDependencies += "org.jetbrains.xodus" % "xodus-entity-store" % xodusVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

disablePlugins(RevolverPlugin)

addCommandAlias("cd", "project")

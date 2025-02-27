lazy val scala3Version = "3.4.2"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion = "1.0.4"
val munitCatsEffectVersion = "2.0.0"
val jeromqVersion = "0.6.0"
val scalaLoggingVersion = "3.9.5"
val logBackVersion = "1.5.17"
val tsidVersion = "1.1.0"
val ulidVersion = "5.2.3"
val lmdbVersion = "0.9.0"
val scodecVersion = "2.3.2"
val fs2Version = "3.11.0"
val catsEffectVersion = "3.5.7"
val http4sVersion = "0.23.30"

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
    libraryDependencies += "com.github.f4b6a3" % "ulid-creator" % ulidVersion,
    libraryDependencies += "io.hypersistence" % "tsid" % tsidVersion,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logBackVersion,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    libraryDependencies += "org.lmdbjava" % "lmdbjava" % lmdbVersion,
    libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
    libraryDependencies += "org.typelevel" %% "cats-effect" % catsEffectVersion,
    libraryDependencies += "org.scodec" % "scodec-core_3" % scodecVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffectVersion % Test
  )
  .dependsOn(gaze)
  .disablePlugins(RevolverPlugin)

lazy val ligatureHttp = project
  .in(file("ligature-http"))
  .settings(
    name := "ligature-http",
    scalaVersion := scala3Version,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logBackVersion,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    libraryDependencies += "org.http4s" %% "http4s-ember-client" % http4sVersion,
    libraryDependencies += "org.http4s" %% "http4s-ember-server" % http4sVersion,
    libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    fork := true
  )
  .dependsOn(ligature)

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
  .dependsOn(ligature)

lazy val ligatureTestSuite = project
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffectVersion
  )
  .dependsOn(ligature)
  .disablePlugins(RevolverPlugin)

lazy val ligatureLMDB = project
  .in(file("ligature-lmdb"))
  .settings(
    name := "ligature-lmdb",
    scalaVersion := scala3Version,
    libraryDependencies += "org.lmdbjava" % "lmdbjava" % "0.9.0",
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

disablePlugins(RevolverPlugin)

addCommandAlias("cd", "project")
addCommandAlias("ls", "projects")

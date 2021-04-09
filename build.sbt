lazy val rcVersion = "3.0.0-RC2"

ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "ligature"

val fs2 = "co.fs2" %% "fs2-core" % "3.0.1"
val munit = "org.scalameta" %% "munit" % "0.7.23"
val vertxWeb = "io.vertx" % "vertx-web" % "4.0.3"
val gson = "com.google.code.gson" % "gson" % "2.8.6"

lazy val ligature = (project in file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := rcVersion,
    libraryDependencies += fs2,
    libraryDependencies += munit % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  )

lazy val ligatureTestSuite = (project in file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := rcVersion,
    libraryDependencies += fs2,
    libraryDependencies += munit,
  ).dependsOn(ligature)

lazy val ligatureInMemory = (project in file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    scalaVersion := rcVersion,
    libraryDependencies += fs2,
    libraryDependencies += munit % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  ).dependsOn(ligature, ligatureTestSuite % Test)

lazy val slonky = (project in file("slonky"))
  .settings(
    name := "slonky",
    scalaVersion := rcVersion,
    libraryDependencies += fs2,
    libraryDependencies += vertxWeb,
    libraryDependencies += gson,
    libraryDependencies += munit % Test,
    testFrameworks += new TestFramework("munit.Framework"),
    mainClass in (Compile, run) := Some("dev.ligature.slonky.Slonky"),
  ).dependsOn(ligature, ligatureInMemory)

addCommandAlias("run", "slonky/run")

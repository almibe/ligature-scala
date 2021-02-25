ThisBuild / scalaVersion     := "2.13.5"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "ligature"

scalacOptions += "-Xsource:3"

val monix = "io.monix" %% "monix" % "3.3.0"
val munit = "org.scalameta" %% "munit" % "0.7.22"
val vertxWeb = "io.vertx" % "vertx-web" % "4.0.2"

lazy val ligature = (project in file("ligature"))
  .settings(
    name := "ligature",
    libraryDependencies += monix,
    libraryDependencies += munit % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  )

lazy val ligatureInMemory = (project in file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    libraryDependencies += monix,
    libraryDependencies += munit % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  ).dependsOn(ligature, ligatureTestSuite % Test)

lazy val ligatureTestSuite = (project in file("ligature-test-suite"))
  .settings(
    name := "ligature-in-memory",
    libraryDependencies += monix,
    libraryDependencies += munit,
  ).dependsOn(ligature)

lazy val slonky = (project in file("slonky"))
  .settings(
    name := "slonky",
    libraryDependencies += monix,
    libraryDependencies += vertxWeb,
    libraryDependencies += munit % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  ).dependsOn(ligature, ligatureInMemory)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

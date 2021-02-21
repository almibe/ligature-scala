ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "ligature"

scalacOptions += "-Xsource:3"

lazy val ligature = (project in file("ligature"))
  .settings(
    name := "ligature",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "2.3.1",
    libraryDependencies += "co.fs2" %% "fs2-core" % "2.5.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.22" % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  )

lazy val ligatureInMemory = (project in file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "2.3.1",
    libraryDependencies += "co.fs2" %% "fs2-core" % "2.5.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.22" % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  ).dependsOn(ligature, ligatureTestSuite % Test)

lazy val ligatureTestSuite = (project in file("ligature-test-suite"))
  .settings(
    name := "ligature-in-memory",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "2.3.1",
    libraryDependencies += "co.fs2" %% "fs2-core" % "2.5.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.22",
  ).dependsOn(ligature)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

import sbt.Keys.testFrameworks

ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.almibe"
ThisBuild / organizationName := "almibe.dev"
ThisBuild / scalaVersion     := "2.13.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "slonky",
    libraryDependencies += "io.monix" %% "monix" % "3.2.2",
    libraryDependencies += "org.scodec" %% "scodec-bits" % "1.1.20",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.12" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )

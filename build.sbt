import Dependencies._

ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "ligature"

lazy val ligature = (project in file("ligature"))
  .settings(
    name := "ligature",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "2.3.1",
    libraryDependencies += "co.fs2" %% "fs2-core" % "2.5.0",
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

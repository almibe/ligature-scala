lazy val rcVersion = "3.0.0-RC1"

ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.ligature"
ThisBuild / organizationName := "ligature"

val fs2 = "co.fs2" %% "fs2-core" % "3.0-209-20193dc"
val munit = "org.scalameta" %% "munit" % "0.7.22"
val vertxWeb = "io.vertx" % "vertx-web" % "4.0.2"

lazy val ligature = (project in file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := rcVersion,
    libraryDependencies += fs2,
    libraryDependencies += munit % Test,
    testFrameworks += new TestFramework("munit.Framework"),
  )

//lazy val ligatureInMemory = (project in file("ligature-in-memory"))
//  .settings(
//    name := "ligature-in-memory",
//    libraryDependencies += monix,
//    libraryDependencies += munit % Test,
//    testFrameworks += new TestFramework("munit.Framework"),
//  ).dependsOn(ligature, ligatureTestSuite % Test)
//
//lazy val ligatureTestSuite = (project in file("ligature-test-suite"))
//  .settings(
//    name := "ligature-in-memory",
//    libraryDependencies += monix,
//    libraryDependencies += munit,
//  ).dependsOn(ligature)
//
//lazy val slonky = (project in file("slonky"))
//  .settings(
//    name := "slonky",
//    libraryDependencies += monix,
//    libraryDependencies += vertxWeb,
//    libraryDependencies += munit % Test,
//    testFrameworks += new TestFramework("munit.Framework"),
//  ).dependsOn(ligature, ligatureInMemory)
//
//lazy val root = project
//  .in(file("."))
//  .settings(
//    name := "scala3-simple",
//    version := "0.1.0",
//
//    scalaVersion := scala3Version,
//
//    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
//  )

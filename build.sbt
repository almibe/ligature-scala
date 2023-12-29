lazy val scala3Version = "3.3.0"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion    = "1.0.0-M7"
val xodusVersion    = "2.0.1"
val jeromqVersion   = "0.5.3"
val jlineVersion    = "3.23.0"
val scalafxVersion  = "16.0.0-R24"
val jansiVersion    = "2.4.1"

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

// lazy val lig = project
//   .in(file("lig"))
//   .settings(
//     name := "lig",
//     scalaVersion := scala3Version,
//     libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
//   )
//   .dependsOn(ligature, gaze, idgen)
//   .disablePlugins(RevolverPlugin)

lazy val wander = project
  .in(file("wander"))
  .settings(
    name := "wander",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  )
  .dependsOn(gaze)
  .disablePlugins(RevolverPlugin)

lazy val wanderLigature = project
  .in(file("wander-ligature"))
  .settings(
    name := "wander-ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  )
  .dependsOn(gaze, wander, ligature, ligatureInMemory % Test)
  .disablePlugins(RevolverPlugin)

lazy val ligatureTestSuite = project
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion,
  )
  .dependsOn(ligature)
  .disablePlugins(RevolverPlugin)

lazy val ligatureInMemory = project
  .in(file("ligature-in-memory"))
  .settings(
    name := "ligature-in-memory",
    scalaVersion := scala3Version,
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

lazy val wanderLsp = project
  .in(file("wander-lsp"))
  .settings(
    name := "wander-lsp",
    scalaVersion := scala3Version,
    libraryDependencies += "io.vertx" % "vertx-core" % "4.5.1",
    libraryDependencies += "io.vertx" % "vertx-web" % "4.5.1",
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion,
  )
  .dependsOn(ligature, wander, ligatureInMemory, ligatureXodus)

lazy val ligatureXodus = project
  .in(file("ligature-xodus"))
  .settings(
    name := "ligature-xodus",
    scalaVersion := scala3Version,
    libraryDependencies += "org.jetbrains.xodus" % "xodus-entity-store" % xodusVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

lazy val wanderZeroMQ = project
  .in(file("wander-zeromq"))
  .settings(
    name := "wander-zeromq",
    scalaVersion := scala3Version,
    libraryDependencies += "org.zeromq" % "jeromq" % jeromqVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    fork := true,
  )
  .dependsOn(ligature, wander, ligatureInMemory, ligatureXodus)

lazy val wanderCli = project
  .in(file("wander-cli"))
  .settings(
    name := "wander-cli",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  )
  .dependsOn(ligature, wander, ligatureInMemory, ligatureXodus)

disablePlugins(RevolverPlugin)

addCommandAlias("cd", "project")

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

lazy val wanderRepl = project
  .in(file("wander-repl"))
  .settings(
    name := "wander-repl",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    libraryDependencies += "org.jline" % "jline" % jlineVersion,
    libraryDependencies += "org.jline" % "jline-terminal-jansi" % jlineVersion,
    libraryDependencies += "org.fusesource.jansi" % "jansi" % jansiVersion,
  )
  .dependsOn(gaze, wander, wanderLigature, ligatureInMemory)
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

lazy val ligatureHttp = project
  .in(file("ligature-http"))
  .settings(
    name := "ligature-http",
    scalaVersion := scala3Version,
    libraryDependencies += "io.vertx" % "vertx-core" % "4.5.1",
    libraryDependencies += "io.vertx" % "vertx-web" % "4.5.1",
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion,
  )
  .dependsOn(ligature, wander, ligatureInMemory, ligatureXodus)

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

//lazy val ligatureLmdb = crossProject(JVMPlatform)
//  .in(file("ligature-lmdb"))
//  .settings(
//      name := "ligature-lmdb",
//      scalaVersion := scala3Version,
//      libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
//      libraryDependencies += "org.lmdbjava" % "lmdbjava" % "0.8.2",
//      libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
//      libraryDependencies += "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"
//  )
//  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
//  .disablePlugins(RevolverPlugin)

lazy val ligatureXodus = project
  .in(file("ligature-xodus"))
  .settings(
    name := "ligature-xodus",
    scalaVersion := scala3Version,
    libraryDependencies += "org.jetbrains.xodus" % "xodus-environment" % xodusVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

// lazy val ligatureZeroMQ = project
//   .in(file("ligature-zeromq"))
//   .settings(
//     name := "ligature-zeromq",
//     scalaVersion := scala3Version,
//     libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
//     libraryDependencies += "org.zeromq" % "jeromq" % jeromqVersion,
//     libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffect3Version % Test,
//   )
//   .dependsOn(ligature, lig, wander, ligatureInMemory, ligatureXodus)

//addCommandAlias("serve", "ligatureZeroMQ/run")

disablePlugins(RevolverPlugin)

addCommandAlias("cd", "project")

lazy val scala3Version = "3.3.0"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion = "1.0.0-M7"
val catsEffectVersion = "3.5.0"
val fs2Version = "3.7.0"
val munitCatsEffect3Version = "2.0.0-M3"
val jlineVersion = "3.23.0"
val scodecVersion = "2.2.1"
val xodusVersion = "2.0.1"
val jeromqVersion = "0.5.3"

lazy val ligature = project
  .in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
    libraryDependencies += "org.typelevel" %% "cats-effect" % catsEffectVersion,
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

lazy val lig = project
  .in(file("lig"))
  .settings(
    name := "lig",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .dependsOn(ligature, gaze, idgen)
  .disablePlugins(RevolverPlugin)

lazy val wander = project
  .in(file("wander"))
  .settings(
    name := "wander",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffect3Version
  )
  .dependsOn(ligature, lig, gaze, ligatureInMemory % Test)
  .disablePlugins(RevolverPlugin)

lazy val ligatureTestSuite = project
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffect3Version
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
    libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
    libraryDependencies += "org.jetbrains.xodus" % "xodus-environment" % xodusVersion,
    libraryDependencies += "org.scodec" % "scodec-core_3" % scodecVersion,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffect3Version % Test
  )
  .dependsOn(ligature, idgen, ligatureTestSuite % Test)
  .disablePlugins(RevolverPlugin)

// lazy val ligatureArcadeDb = crossProject(JVMPlatform)
//   .in(file("ligature-arcadedb"))
//   .settings(
//     name := "ligature-arcadedb",
//     scalaVersion := scala3Version,
//     libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
//     libraryDependencies += "com.arcadedb" % "arcadedb-engine" % "23.4.1",
//     libraryDependencies += "org.scodec" % "scodec-core_3" % "2.1.0",
//     libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
//     libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffect3Version % Test
//   )
//   .dependsOn(ligature, idgen, ligatureTestSuite % Test)
//   .disablePlugins(RevolverPlugin)

lazy val ligatureZeroMQ = project
  .in(file("ligature-zeromq"))
  .settings(
    name := "ligature-zeromq",
    scalaVersion := scala3Version,
    libraryDependencies += "co.fs2" %% "fs2-core" % fs2Version,
    libraryDependencies += "org.zeromq" % "jeromq" % jeromqVersion,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % munitCatsEffect3Version % Test
  )
  .dependsOn(ligature, lig, wander, ligatureInMemory, ligatureXodus)
  .disablePlugins(RevolverPlugin)

lazy val ligatureRepl = project
  .in(file("ligature-repl"))
  .settings(
    name := "ligature-repl",
    scalaVersion := scala3Version,
    libraryDependencies += "org.jline" % "jline" % jlineVersion,
    libraryDependencies += "org.jline" % "jline-terminal-jansi" % jlineVersion
  )
  .dependsOn(ligature, lig, wander, ligatureInMemory, ligatureXodus)
  .disablePlugins(RevolverPlugin)

lazy val ligaturePad = project
  .in(file("ligature-pad"))
  .settings(
    name := "ligature-pad",
    scalaVersion := scala3Version,
    libraryDependencies += "org.jline" % "jline" % jlineVersion,
    libraryDependencies += "org.jline" % "jline-terminal-jansi" % jlineVersion
  )
  .dependsOn(ligature, lig, wander, ligatureInMemory, ligatureXodus)
  .disablePlugins(RevolverPlugin)

addCommandAlias("serve", "ligature-zeromq/run")

disablePlugins(RevolverPlugin)

addCommandAlias("cd", "project")

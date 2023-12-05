lazy val scala3Version = "3.3.0"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "dev.ligature"
ThisBuild / organizationName := "ligature"

val munitVersion = "1.0.0-M7"
val xodusVersion = "2.0.1"
val jeromqVersion = "0.5.3"
val jlineVersion = "3.24.1"
val scalafxVersion = "16.0.0-R24"
val jansiVersion = "2.4.1"

lazy val ligature = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature"))
  .settings(
    name := "ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val gaze = crossProject(JSPlatform, JVMPlatform)
  .in(file("gaze"))
  .settings(
    name := "gaze",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val idgen = crossProject(JSPlatform, JVMPlatform)
  .in(file("idgen"))
  .settings(
    name := "idgen",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .disablePlugins(RevolverPlugin)

lazy val lig = crossProject(JSPlatform, JVMPlatform)
  .in(file("lig"))
  .settings(
    name := "lig",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
  )
  .dependsOn(ligature, gaze, idgen)
  .disablePlugins(RevolverPlugin)

lazy val wander = crossProject(JSPlatform, JVMPlatform)
  .in(file("wander"))
  .settings(
    name := "wander",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    // scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
    }
  )
  .dependsOn(gaze)
  .disablePlugins(RevolverPlugin)

lazy val wanderLigature = crossProject(JSPlatform, JVMPlatform)
  .in(file("wander-ligature"))
  .settings(
    name := "wander-ligature",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    // scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
    }
  )
  .dependsOn(gaze, wander, ligature, ligatureInMemory % Test)
  .disablePlugins(RevolverPlugin)

lazy val wanderPad = crossProject(JVMPlatform)
  .in(file("wander-pad"))
  .settings(
    fork := true,
    name := "wander-pad",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    libraryDependencies += "org.scalafx" %% "scalafx" % scalafxVersion,
    libraryDependencies ++= {
      lazy val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case n if n.startsWith("Windows") => "win"
        case _                            => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => ("org.openjfx" % s"javafx-$m" % "16").classifier(osName))
    }
  )
  .dependsOn(gaze, wander, wanderLigature, ligatureInMemory)
  .disablePlugins(RevolverPlugin)

lazy val wanderRepl = crossProject(JVMPlatform)
  .in(file("wander-repl"))
  .settings(
    name := "wander-repl",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
    libraryDependencies += "org.jline" % "jline" % jlineVersion,
    libraryDependencies += "org.jline" % "jline-terminal-jansi" % jlineVersion,
    libraryDependencies += "org.fusesource.jansi" % "jansi" % jansiVersion
  )
  .dependsOn(gaze, wander, wanderLigature, ligatureInMemory)
  .disablePlugins(RevolverPlugin)

lazy val ligatureTestSuite = crossProject(JSPlatform, JVMPlatform)
  .in(file("ligature-test-suite"))
  .settings(
    name := "ligature-test-suite",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion
  )
  .dependsOn(ligature)
  .disablePlugins(RevolverPlugin)

lazy val ligatureInMemory = crossProject(JSPlatform, JVMPlatform)
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

// lazy val ligatureXodus = crossProject(JVMPlatform)
//   .in(file("ligature-xodus"))
//   .settings(
//     name := "ligature-xodus",
//     scalaVersion := scala3Version,
//     libraryDependencies += "org.jetbrains.xodus" % "xodus-environment" % xodusVersion,
//     libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
//   )
//   .dependsOn(ligature, idgen, ligatureTestSuite % Test)
//   .disablePlugins(RevolverPlugin)

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

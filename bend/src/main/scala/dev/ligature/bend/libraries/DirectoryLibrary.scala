/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.libraries

import dev.ligature.bend.FieldPath
import dev.ligature.bend.BendValue
import java.nio.file.Path
import dev.ligature.bend.Environment
import dev.ligature.bend.WanderError
import scala.util.boundary
import scala.util.boundary.break
import java.nio.file.Files
import scala.jdk.CollectionConverters.IteratorHasAsScala
import dev.ligature.bend.Field
import dev.ligature.bend.TaggedField
import dev.ligature.bend.run
import scala.util.Success
import scala.util.Failure
import scala.io.Source
import scala.util.Using
import dev.ligature.bend.modules.std
import dev.ligature.bend.Tag

final class DirectoryLibrary(path: Path) extends ModuleLibrary {
  var modules: Map[ModuleId, BendValue.Module] = Map()
  override def lookup(id: ModuleId): Either[WanderError, Option[BendValue.Module]] =
    boundary:
      if (modules.isEmpty) {
        modules = loadFromPath(path) match {
          case Left(value)  => break(Left(value))
          case Right(value) => value
        }
      }
    Right(modules.get(id))
}

val wanderExt = ".wander"
val wanderTextExt = ".test.wander"

private def loadFromPath(path: Path): Either[WanderError, Map[ModuleId, BendValue.Module]] =
  boundary:
    var results = scala.collection.mutable.HashMap[ModuleId, BendValue.Module]()
    Files
      .walk(path)
      .iterator()
      .asScala
      .filter(Files.isRegularFile(_))
      .filter(f =>
        f.getFileName().toString().endsWith(wanderExt)
          &&
            !f.getFileName().toString().endsWith(wanderTextExt)
      )
      .foreach { file =>
        val modname = file.toFile().getName().dropRight(wanderExt.length())
        val module = scala.collection.mutable.HashMap[Field, BendValue]()
        Using(Source.fromFile(file.toFile()))(_.mkString) match
          case Failure(exception) =>
            break(Left(WanderError(s"Error reading $file\n${exception.getMessage()}")))
          case Success(script) =>
            run(script, std()) match
              case Left(err) => break(Left(err))
              case Right((BendValue.Module(values), _)) =>
                values.foreach((name, value) => module.put(name, value))
              case x => break(Left(WanderError("Unexpected value from load result. $x")))
        results += (modname -> BendValue.Module(module.toMap))
      }
    Right(results.toMap)

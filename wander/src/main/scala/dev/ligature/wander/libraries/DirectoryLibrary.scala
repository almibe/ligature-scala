/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.FieldPath
import dev.ligature.wander.WanderValue
import java.nio.file.Path
import dev.ligature.wander.Environment
import dev.ligature.wander.WanderError
import scala.util.boundary
import scala.util.boundary.break
import java.nio.file.Files
import scala.jdk.CollectionConverters.IteratorHasAsScala
import dev.ligature.wander.Field
import dev.ligature.wander.TaggedField
import dev.ligature.wander.run
import scala.util.Success
import scala.util.Failure
import scala.io.Source
import scala.util.Using
import dev.ligature.wander.modules.std
import dev.ligature.wander.Tag
import dev.ligature.wander.modules.wmdn

final class DirectoryLibrary(path: Path) extends ModuleLibrary {
  var modules: Map[ModuleId, WanderValue.Module] = Map()
  override def lookup(id: ModuleId): Either[WanderError, Option[WanderValue.Module]] =
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

private def loadFromPath(path: Path): Either[WanderError, Map[ModuleId, WanderValue.Module]] =
  boundary:
    var results = scala.collection.mutable.HashMap[ModuleId, WanderValue.Module]()
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
        val module = scala.collection.mutable.HashMap[Field, WanderValue]()
        Using(Source.fromFile(file.toFile()))(_.mkString) match
          case Failure(exception) =>
            break(Left(WanderError(s"Error reading $file\n${exception.getMessage()}")))
          case Success(script) =>
            run(script, std()) match
              case Left(err) => break(Left(err))
              case Right((WanderValue.Module(values), _)) =>
                values.foreach((name, value) => module.put(name, value))
              case x => break(Left(WanderError("Unexpected value from load result. $x")))
        results += (modname -> WanderValue.Module(module.toMap))
      }
    Right(results.toMap)

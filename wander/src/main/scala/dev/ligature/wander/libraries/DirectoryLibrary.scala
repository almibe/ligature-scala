/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.BendValue
import java.nio.file.Path
import dev.ligature.wander.Environment
import dev.ligature.wander.BendError
import scala.util.boundary
import scala.util.boundary.break
import java.nio.file.Files
import scala.jdk.CollectionConverters.IteratorHasAsScala
import dev.ligature.wander.Field
import dev.ligature.wander.run
import scala.util.Success
import scala.util.Failure
import scala.io.Source
import scala.util.Using
import dev.ligature.wander.modules.std

final class DirectoryLibrary(path: Path) extends ModuleLibrary {
  var modules: Map[ModuleId, BendValue.Module] = Map()
  override def lookup(id: ModuleId): Either[BendError, Option[BendValue.Module]] =
    boundary:
      if (modules.isEmpty) {
        modules = loadFromPath(path) match {
          case Left(value)  => break(Left(value))
          case Right(value) => value
        }
      }
    Right(modules.get(id))
}

val bendExt = ".wander"
val bendTextExt = ".test.wander"

private def loadFromPath(path: Path): Either[BendError, Map[ModuleId, BendValue.Module]] =
  boundary:
    val results = scala.collection.mutable.HashMap[ModuleId, BendValue.Module]()
    Files
      .walk(path)
      .iterator()
      .asScala
      .filter(Files.isRegularFile(_))
      .filter(f =>
        f.getFileName().toString().endsWith(bendExt)
          &&
            !f.getFileName().toString().endsWith(bendTextExt)
      )
      .foreach { file =>
        val modname = file.toFile().getName().dropRight(bendExt.length())
        val module = scala.collection.mutable.HashMap[Field, BendValue]()
        Using(Source.fromFile(file.toFile()))(_.mkString) match
          case Failure(exception) =>
            break(Left(BendError(s"Error reading $file\n${exception.getMessage()}")))
          case Success(script) =>
            run(script, std()) match
              case Left(err) => break(Left(err))
              case Right((BendValue.Module(values), _)) =>
                values.foreach((name, value) => module.put(name, value))
              case x => break(Left(BendError("Unexpected value from load result. $x")))
        results += (modname -> BendValue.Module(module.toMap))
      }
    Right(results.toMap)

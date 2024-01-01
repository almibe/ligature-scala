/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.WanderValue
import scala.collection.mutable.ListBuffer
import dev.ligature.wander.*
import dev.ligature.wander.Environment
import java.nio.file.Path
import java.nio.file.Files
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.Using
import scala.io.Source
import scala.util.Failure
import scala.util.Success

/** Create the "default" environment for working with Wander.
  */
def std(): Environment =
  Environment()
    .addHostFunctions(arrayLibrary)
    .addHostFunctions(boolLibrary)
    .addHostFunctions(boolLibrary)
    .addHostFunctions(coreLibrary)
    .addHostFunctions(intLibrary)
    .addHostFunctions(recordLibrary)
    .addHostFunctions(shapeLibrary)
    .addHostFunctions(stringLibrary)
    .addHostFunctions(testingLibrary)

/** Load Wander modules from the path provided using the environment provided as a base.
  */
def loadFromPath(path: Path, environment: Environment): Either[WanderError, Environment] =
  var resultEnvironment = environment
  Files
    .walk(path)
    .iterator()
    .asScala
    .filter(Files.isRegularFile(_))
    .filter(_.getFileName().toString().endsWith("wander"))
    .foreach { file =>
      Using(Source.fromFile(file.toFile()))(_.mkString) match
        case Failure(exception) =>
          Left(WanderError(s"Error reading $file\n${exception.getMessage()}"))
        case Success(script) =>
          load(script, std()) match
            case Left(err) => Left(err)
            case Right(values) =>
              values.foreach((name, value) =>
                resultEnvironment =
                  resultEnvironment.bindVariable(TaggedName(name, Tag.Untagged), value) match
                    case Left(value)  => ???
                    case Right(value) => value
              )
    }
  Right(resultEnvironment)

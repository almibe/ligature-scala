/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

import dev.ligature.Statement
import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.Gaze
import dev.ligature.gaze.takeWhile
import dev.ligature.Identifier
import dev.ligature.Value

def interpret(input: List[DLigModel]): Either[DLigError, List[Statement]] = {
  val gaze = Gaze(input.toVector)
  for {
    prefixes <- readPrefixes(gaze)
    result <- readStatements(gaze, prefixes)
  } yield result
}

def readPrefixes(model: Gaze[DLigModel]): Either[DLigError, List[PrefixDefinition]] = {
  //TODO maybe add a takeWhileMap Nibbler?
  //TODO needs to check that prefix names aren't duplicated
  val res = model.attempt(takeWhile { m => m.isInstanceOf[PrefixDefinition] })
  val finalRes: Seq[PrefixDefinition] = res match {
    case None => List[PrefixDefinition]()
    case Some(model) => {
      model.map{ m => m.asInstanceOf[PrefixDefinition] }
    }
  }
  Right(finalRes.toList)
}

def readStatements(model: Gaze[DLigModel], prefixed: List[PrefixDefinition]) : Either[DLigError, List[Statement]] = {
  val result = ArrayBuffer[Statement]()
  while (!model.isComplete()) {
    val entity = handleIdentifier(model.next())
    val attribute = handleIdentifier(model.next())
    val value = handleValue(model.next())
  }
  Right(result.toList)
}

def handleIdentifier(identifier: Option[DLigModel]): Either[DLigError, Identifier] = {
  identifier match {
    case None => Left(DLigError("Could not read Identifier."))
    case Some(model) => {
      model match {
        case RegularIdentifier(identifier) => ???
        case PrefixedIdentifier(prefixName, remainder) => ???
        case GenIdentifier(genIdentifier) => ???
        case GenPrefixedIdentifier(prefixName, genRemainder) => ???
        case _ => Left(DLigError("Could not read Identifier."))
      }
    }
  }
}

def handleValue(value: Option[DLigModel]): Either[DLigError, Value] = {
  value match {
    case None => Left(DLigError("Could not read Value."))
    case Some(model) => {
      model match {
        case DLigValue(value) => Right(value)
        case _ => Left(DLigError("Could not read Value."))
      }
    }
  }
}

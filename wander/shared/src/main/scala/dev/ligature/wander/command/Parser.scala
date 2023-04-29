/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.command

import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  optional,
  take,
  takeAll,
  takeCond,
  takeFirst,
  takeString,
  takeUntil,
  takeWhile,
  repeat
}
import dev.ligature.lig.LigNibblers

enum Command:
  case Literal(value: Boolean | String | Long)
  // case Identifier(value: String)
  // case Integer(value: Long)
  // case StringLiteral(value: String)
  // case Name(value: String)
  // case Tuple(commands: Seq[Command])
  // case Form(commands: Seq[Command])
  case Datasets
  case CreateDataset(datasetName: String)
  case RemoveDataset(datasetName: String)
  case Statements
  case AddStatement
  case RemoveStatement

val datasetsNib: Nibbler[Token, Command] = { gaze => ??? }

val createDatasetNib: Nibbler[Token, Command] = { gaze => ??? }

val removeDatasetNib: Nibbler[Token, Command] = { gaze => ??? }

val commandNib: Nibbler[Token, Command] = repeat(
  takeFirst(
    datasetsNib,
    createDatasetNib,
    removeDatasetNib
    // statementsNib,
    // addStatementNib,
    // removeStatementNib
  )
)

def parse(input: Seq[Token]): Either[WanderError, Seq[Command]] = {
  val tokens = input.filter {
    _ match
      case Token.Space | Token.NewLine | Token.Comment(_) => false
      case _                                              => true
  }
  val gaze = Gaze(tokens)
  gaze.attempt(commandsNib) match {
    case None =>
      if gaze.isComplete then Right(List())
      else Left(WanderError(s"Error P1 - Could not parse ${gaze.peek()}"))
    case Some(res) =>
      if gaze.isComplete then Right(res)
      else Left(WanderError(s"Error P2 - Could not parser ${gaze.peek()}"))
  }
}

// val tupleCommandNib: Nibbler[Token, Command] = gaze =>
//   val res = for {
//     _ <- gaze.attempt(take(Token.OpenSquare))
//     contents <- gaze.attempt(optional(commandsNib))
//     _ <- gaze.attempt(take(Token.CloseSquare))
//   } yield contents
//   res match
//     case Some(commands) => Some(Seq(Command.Tuple(commands)))
//     case _ => None

val integerCommandNib: Nibbler[Token, Command] = gaze =>
  gaze.next() match
    case Some(Token.Integer(i)) => Some(List(Command.Literal(i)))
    case _                      => None

val stringCommandNib: Nibbler[Token, Command] = gaze =>
  gaze.next() match
    case Some(Token.StringLiteral(s)) => Some(List(Command.Literal(s)))
    case _                            => None

val booleanCommandNib: Nibbler[Token, Command] = gaze =>
  gaze.next() match
    case Some(Token.BooleanLiteral(b)) => Some(List(Command.Literal(b)))
    case _                             => None

// val nameCommandNib: Nibbler[Token, Command] = gaze =>
//   gaze.next() match
//     case Some(Token.Name(n)) => Some(List(Command.Name(n)))
//     case _ => None

val identifierCommandNib: Nibbler[Token, Command] = gaze =>
  gaze.next() match
    case Some(Token.Identifier(i)) => Some(List(Command.Literal(i)))
    case _                         => None

val commandsNib: Nibbler[Token, Command] =
  repeat(
    takeFirst(
      integerCommandNib,
      stringCommandNib,
//      nameCommandNib,
      booleanCommandNib,
      identifierCommandNib
//      tupleCommandNib,
    )
  )

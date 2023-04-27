/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.`new`

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

enum Element:
  case BooleanLiteral(value: Boolean)
  case Identifier(value: String)
  case Integer(value: Long)
  case StringLiteral(value: String)
  case Name(value: String)
  case Form(elements: Seq[Element])

def parse(input: Seq[Token]): Either[WanderError, Seq[Element]] = {
  val tokens = input.filter {
    _ match
      case Token.Space | Token.NewLine | Token.Comment(_) => false
      case _                                              => true
  }
  val gaze = Gaze(tokens)
  gaze.attempt(elementsNib) match {
    case None =>
      if gaze.isComplete then Right(List())
      else Left(WanderError("Error"))
    case Some(res) =>
      if gaze.isComplete then Right(res)
      else Left(WanderError("Error"))
  }
}

val integerElementNib: Nibbler[Token, Element] = gaze =>
  gaze.next() match
    case Some(Token.Integer(i)) => Some(List(Element.Integer(i)))
    case _                      => None

val stringElementNib: Nibbler[Token, Element] = gaze =>
  gaze.next() match
    case Some(Token.StringLiteral(s)) => Some(List(Element.StringLiteral(s)))
    case _                            => None

val booleanElementNib: Nibbler[Token, Element] = gaze =>
  gaze.next() match
    case Some(Token.BooleanLiteral(b)) => Some(List(Element.BooleanLiteral(b)))
    case _                             => None

val nameElementNib: Nibbler[Token, Element] = gaze =>
  gaze.next() match
    case Some(Token.Name(n)) => Some(List(Element.Name(n)))
    case _                   => None

val identifierElementNib: Nibbler[Token, Element] = gaze =>
  gaze.next() match
    case Some(Token.Identifier(i)) => Some(List(Element.Identifier(i)))
    case _                         => None

val elementsNib: Nibbler[Token, Element] =
  repeat(
    takeFirst(
      integerElementNib,
      stringElementNib,
      nameElementNib,
      booleanElementNib,
      identifierElementNib
    )
  )

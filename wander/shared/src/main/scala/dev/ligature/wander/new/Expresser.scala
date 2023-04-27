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
import dev.ligature.Identifier

enum Expression:
  case BooleanLiteral(value: Boolean)
  case Identifier(value: String)
  case Integer(value: Long)
  case StringLiteral(value: String)
  case Name(value: String)
  case FunctionCall(elements: Seq[Element])
  case Nothing

def expressionize(input: Seq[Element]): Either[WanderError, Seq[Expression]] = {
  val gaze = Gaze(input)
  gaze.attempt(expressionsNib) match {
    case None =>
      if (gaze.isComplete) {
        Right(List())
      } else {
        Left(WanderError("Error"))
      }
    case Some(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(WanderError("Error"))
      }
  }
}

private val integerExpressionNib: Nibbler[Element, Expression] = gaze =>
  gaze.next() match
    case Some(Element.Integer(i)) => Some(Seq(Expression.Integer(i)))
    case _                        => None

private val stringExpressionNib: Nibbler[Element, Expression] = gaze =>
  gaze.next() match
    case Some(Element.StringLiteral(s)) => Some(Seq(Expression.StringLiteral(s)))
    case _                              => None

private val nothingExpressionNib: Nibbler[Element, Expression] = gaze =>
  gaze.next() match
    case Some(Element.Name("nothing")) => Some(Seq(Expression.Nothing))
    case _                             => None

private val booleanExpressionNib: Nibbler[Element, Expression] = gaze =>
  gaze.next() match
    case Some(Element.BooleanLiteral(b)) => Some(Seq(Expression.BooleanLiteral(b)))
    case _                               => None

private val identifierExpressionNib: Nibbler[Element, Expression] = gaze =>
  gaze.next() match
    case (Some(Element.Identifier(i))) => Some(Seq(Expression.Identifier(i)))
    case _                             => None

val expressionsNib: Nibbler[Element, Expression] =
  repeat(
    takeFirst(
      integerExpressionNib,
      stringExpressionNib,
      nothingExpressionNib,
      booleanExpressionNib,
      identifierExpressionNib
    )
  )

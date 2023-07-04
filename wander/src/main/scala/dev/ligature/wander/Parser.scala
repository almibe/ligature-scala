/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  optional,
  take,
  takeAll,
  takeCond,
  takeFirst,
  takeString,
  repeat
}
import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
import dev.ligature.wander.Token

case class Name(name: String)

enum Term:
  case NameTerm(value: Name)
  case IdentifierLiteral(value: Identifier)
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String)
  case BooleanLiteral(value: Boolean)
  case NothingLiteral
  case QuestionMark
  case List(value: Seq[Term])
  case LetBinding(name: Name, value: Term)
  case FunctionCall(name: Name, arguments: Seq[Term])
  case Scope(elements: Seq[Term])
  case WanderFunction(
    parameters: Seq[Name],
    body: Seq[Term])
  case IfExpression(
    conditional: Term,
    ifBody: Term,
    elseBody: Term)

def parse(script: Seq[Token]): Either[LigatureError, Seq[Term]] = {
  val filteredInput = script.filter {
    _ match
      case Token.Spaces(_) | Token.NewLine | Token.Comment => false
      case _ => true
  }
  val gaze = Gaze(filteredInput)
  val res: Option[Seq[Term]] =  gaze.attempt(scriptNib)
  res match {
    case None =>
      if (gaze.isComplete) {
        Right(Seq())
      } else {
        Left(LigatureError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
    // TODO some case also needs to check if gaze is complete
    case Some(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(LigatureError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
  }
}

val nothingNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.NothingKeyword) => Some(List(Term.NothingLiteral))
    case _ => None

val questionMarkTermNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.QuestionMark) => Some(List(Term.QuestionMark))
    case _ => None

val booleanNib: Nibbler[Token, Term.BooleanLiteral] = gaze =>
  gaze.next() match
    case Some(Token.BooleanLiteral(b)) => Some(List(Term.BooleanLiteral(b)))
    case _ => None

val identifierNib: Nibbler[Token, Term.IdentifierLiteral] = gaze =>
  gaze.next() match
    case Some(Token.Identifier(i)) => Some(List(Term.IdentifierLiteral(i)))
    case _ => None

val integerNib: Nibbler[Token, Term.IntegerLiteral] = gaze =>
  gaze.next() match
    case Some(Token.IntegerLiteral(i)) => Some(List(Term.IntegerLiteral(i)))
    case _ => None

val stringNib: Nibbler[Token, Term.StringLiteral] = gaze =>
  gaze.next() match
    case Some(Token.StringLiteral(s)) => Some(List(Term.StringLiteral(s)))
    case _ => None

val nameNib: Nibbler[Token, Term.NameTerm] = gaze =>
  gaze.next() match
    case Some(Token.Name(n)) => Some(List(Term.NameTerm(Name(n))))
    case _ => None

val scopeNib: Nibbler[Token, Term.Scope] = { gaze =>
  for {
    _ <- gaze.attempt(take(Token.OpenBrace))
    expression <- gaze.attempt(optional(repeat(elementNib)))
    _ <- gaze.attempt(take(Token.CloseBrace))
  } yield Seq(Term.Scope(expression.toList))
}

val parameterNib: Nibbler[Token, Name] = { gaze =>
  for {
    names <- gaze.attempt(
      nameNib
    )
  } yield names.map(_.value)
}

val wanderFunctionNib: Nibbler[Token, Term.WanderFunction] = { gaze =>
  for {
    _ <- gaze.attempt(take(Token.OpenBrace))
    parameters <- gaze.attempt(optional(repeat(parameterNib)))
    _ <- gaze.attempt(take(Token.Arrow))
    body <- gaze.attempt(expressionNib)
    _ <- gaze.attempt(take(Token.CloseBrace))
  } yield Seq(Term.WanderFunction(parameters, body))
}

// val typeNib: Nibbler[Token, WanderType] = takeFirst(
//   take(Token("Integer", TokenType.Name)).map(_ => List(WanderType.Integer)),
//   take(Token("Identifier", TokenType.Name)).map { _ =>
//     List(WanderType.Identifier)
//   },
//   take(Token("Value", TokenType.Name)).map(_ => List(WanderType.Value))
// )

val elseExpressionNib: Nibbler[Token, Term] = { gaze =>
  for {
    _ <- gaze.attempt(take(Token.ElseKeyword))
    body <- gaze.attempt(expressionNib)
  } yield body
}

val ifExpressionNib: Nibbler[Token, Term.IfExpression] = { gaze =>
  for {
    _ <- gaze.attempt(take(Token.IfKeyword))
    condition <- gaze.attempt(expressionNib)
    body <- gaze.attempt(expressionNib)
    `else` <- gaze.attempt(optional(elseExpressionNib))
  } yield Seq(
    Term.IfExpression(
      condition.head,
      body.head,
      `else`.head
    )
  )
}

val functionCallNib: Nibbler[Token, Term] = { gaze =>
  for
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(take(Token.OpenParen))
    parameters <- gaze.attempt(optional(repeat(expressionNib)))
    _ <- gaze.attempt(take(Token.CloseParen))
  yield Seq(Term.FunctionCall(name.head.value, parameters))
}

val listNib: Nibbler[Token, Term] = { gaze =>
  for
    _ <- gaze.attempt(take(Token.OpenBracket))
    values <- gaze.attempt(optional(repeat(expressionNib)))
    _ <- gaze.attempt(take(Token.CloseBracket))
  yield Seq(Term.List(values))
}

val expressionNib =
  takeFirst(
    ifExpressionNib,
    functionCallNib,
    nameNib,
    scopeNib,
    identifierNib,
    wanderFunctionNib,
    stringNib,
    integerNib,
    listNib,
    booleanNib,
    nothingNib,
    questionMarkTermNib
  )

val letStatementNib: Nibbler[Token, Term] = { gaze =>
  for {
    _ <- gaze.attempt(take(Token.LetKeyword))
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(take(Token.EqualSign))
    expression <- gaze.attempt(expressionNib)
  } yield Seq(Term.LetBinding(name.head.value, expression.head))
}

val elementNib = takeFirst(expressionNib, letStatementNib)

val scriptNib =
  optional(
    repeat(
      elementNib
    )
  )
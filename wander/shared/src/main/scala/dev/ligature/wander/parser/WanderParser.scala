/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

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
import dev.ligature.{IntegerLiteral, Identifier, StringLiteral}
import dev.ligature.wander.lexer.{Token, TokenType}

def parse(script: Seq[Token]): Either[String, Script] = {
  val filteredInput = script.filter { (token: Token) =>
    token.tokenType != TokenType.Comment && token.tokenType != TokenType.Spaces && token.tokenType != TokenType.NewLine
  }.toVector
  val gaze = Gaze(filteredInput)
  val res = gaze.attempt(scriptNib)
  res match {
    case None =>
      if (gaze.isComplete()) {
        Right(Script(Seq()))
      } else {
        Left("No Match")
      }
    // TODO some case also needs to check if gaze is complete
    case Some(res) =>
      if (gaze.isComplete()) {
        Right(Script(res)) // .filter(_.isDefined).map(_.get)))
      } else {
        Left("No Match")
      }
  }
}

val booleanNib: Nibbler[Token, Expression] = takeCond[Token] {
  _.tokenType == TokenType.Boolean
}.map { token => Seq(BooleanValue(token.head.content.toBoolean)) }

val identifierNib: Nibbler[Token, Expression] = takeCond[Token] {
  _.tokenType == TokenType.Identifier
}.map { token =>
  Seq(LigatureValue(Identifier.fromString(token.head.content).getOrElse(???)))
}

val integerNib: Nibbler[Token, Expression] = takeCond[Token] {
  _.tokenType == TokenType.Integer
}.map { token => Seq(LigatureValue(IntegerLiteral(token.head.content.toInt))) }

val stringNib: Nibbler[Token, Expression] = takeCond[Token] {
  _.tokenType == TokenType.String
}.map { token => Seq(LigatureValue(StringLiteral(token.head.content))) }

val nameNib = takeCond[Token] {
  _.tokenType == TokenType.Name
}.map { token =>
  Seq(Name(token.head.content))
}

val openBraceNib = takeCond[Token] { _.tokenType == TokenType.OpenBrace }.map {
  token => Seq(OpenBrace)
}

val closeBraceNib =
  takeCond[Token] { _.tokenType == TokenType.CloseBrace }.map { _ =>
    Seq(CloseBrace)
  }

val openParenNib = takeCond[Token] { _.tokenType == TokenType.OpenParen }.map {
  _ => Seq(OpenParen)
}

val closeParenNib =
  takeCond[Token] { _.tokenType == TokenType.CloseParen }.map { _ =>
    Seq(CloseParen)
  }

val arrowNib = takeCond[Token] { _.tokenType == TokenType.Arrow }.map { _ =>
  Seq(Arrow)
}

val scopeNib: Nibbler[Token, Scope] = { gaze =>
  for {
    _ <- gaze.attempt(openBraceNib)
    expression <- gaze.attempt(optional(repeat(elementNib)))
    _ <- gaze.attempt(closeBraceNib)
  } yield Seq(Scope(expression.toList))
}

val parameterNib: Nibbler[Token, Parameter] = { gaze =>
  gaze.attempt(nameNib).map { name => name.map(name => Parameter(name)) }
}

val wanderFunctionNib: Nibbler[Token, FunctionDefinition] = { gaze =>
  for {
    _ <- gaze.attempt(openParenNib)
    parameters <- gaze.attempt(optional(repeat(parameterNib)))
    _ <- gaze.attempt(closeParenNib)
    _ <- gaze.attempt(arrowNib)
    returnType <- gaze.attempt(typeNib)
    body <- gaze.attempt(scopeNib)
  } yield Seq(WanderFunction(parameters.toList, returnType.head, body.head))
}

val typeNib: Nibbler[Token, WanderType] = take(Token("Integer", TokenType.Name)).map {_ => List(WanderType.Integer)}

val ifKeywordNib =
  takeCond[Token] { _.tokenType == TokenType.IfKeyword }.map { _ =>
    Seq(LetKeyword)
  }

val elseKeywordNib =
  takeCond[Token] { _.tokenType == TokenType.ElseKeyword }.map { _ =>
    Seq(LetKeyword)
  }

val elseIfExpressionNib: Nibbler[Token, ElseIf] = { gaze =>
  for {
    _ <- gaze.attempt(elseKeywordNib)
    _ <- gaze.attempt(ifKeywordNib)
    condition <- gaze.attempt(expressionNib)
    body <- gaze.attempt(expressionNib)
  } yield Seq(ElseIf(condition.head, body.head))
}

val elseExpressionNib: Nibbler[Token, Else] = { gaze =>
  for {
    _ <- gaze.attempt(elseKeywordNib)
    body <- gaze.attempt(expressionNib)
  } yield Seq(Else(body.head))
}

val ifExpressionNib: Nibbler[Token, IfExpression] = { gaze =>
  for {
    _ <- gaze.attempt(ifKeywordNib)
    condition <- gaze.attempt(expressionNib)
    body <- gaze.attempt(expressionNib)
    elseIfs <- gaze.attempt(optional(repeat(elseIfExpressionNib)))
    `else` <- gaze.attempt(optional(elseExpressionNib))
  } yield Seq(
    IfExpression(
      condition.head,
      body.head,
      elseIfs.toList,
      `else`.toList.find(_ => true)
    )
  )
}

val functionCallNib: Nibbler[Token, FunctionCall] = { gaze =>
  for {
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(openParenNib)
    parameters <- gaze.attempt(optional(repeat(expressionNib)))
    _ <- gaze.attempt(closeParenNib)
  } yield Seq(FunctionCall(name.head, parameters.toList))
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
    booleanNib
  )

val equalSignNib = takeCond[Token] { _.tokenType == TokenType.EqualSign }.map {
  _ => Seq(EqualSign)
}

val letKeywordNib =
  takeCond[Token] { _.tokenType == TokenType.LetKeyword }.map { _ =>
    Seq(LetKeyword)
  }

val letStatementNib: Nibbler[Token, LetStatement] = { gaze =>
  {
    for {
      _ <- gaze.attempt(letKeywordNib)
      name <- gaze.attempt(nameNib)
      _ <- gaze.attempt(equalSignNib)
      expression <- gaze.attempt(expressionNib)
    } yield Seq(LetStatement(name.head, expression.head))
  }
}

val elementNib = takeFirst(expressionNib, letStatementNib)

val scriptNib =
  optional(
    repeat(
      elementNib
    )
  )

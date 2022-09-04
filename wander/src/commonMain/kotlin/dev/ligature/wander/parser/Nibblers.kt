/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import arrow.core.Option
import arrow.core.none
import arrow.core.Some
import dev.ligature.BytesLiteral
import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.StringLiteral
import dev.ligature.gaze.*
import dev.ligature.wander.lexer.Token

object Nibblers {
  //TODO this code might be better if takeCond allowed you to map values
  //TODO try to use takeCondMap, this might get rid of all the `as`'s in this code
  val booleanNib: Nibbler<Token, Expression> = takeCond<Token> {
    it is Token.Boolean
  }.map { tokens: List<Token> ->
    val token = tokens.first() as Token.Boolean
    listOf(BooleanValue((token).value))
  }

  val identifierNib: Nibbler<Token, Expression> = takeCond<Token> {
    it is Token.Identifier
  }.map { tokens: List<Token> ->
    val token = tokens.first() as Token.Identifier
    listOf(LigatureValue(Identifier(token.value)))
  }

  val integerNib: Nibbler<Token, Expression> = takeCond<Token> {
    it is Token.Integer
  }.map { tokens: List<Token> ->
    val token = tokens.first() as Token.Integer
    listOf(LigatureValue(IntegerLiteral(token.value.toLong())))
  }

  val stringNib: Nibbler<Token, Expression> = takeCond<Token> {
    it is Token.StringLiteral
  }.map { tokens: List<Token> ->
    val token = tokens.first() as Token.StringLiteral
    listOf(LigatureValue(StringLiteral(token.value)))
  }

  val bytesNib: Nibbler<Token, Expression> = takeCond<Token> {
    it is Token.BytesLiteral
  }.map { tokens: List<Token> ->
    val token = tokens.first() as Token.BytesLiteral
    listOf(LigatureValue(BytesLiteral(byteArrayOf()))) //TODO set actual value
  }

  val nameNib = takeCond<Token> {
    it is Token.Name
  }.map { tokens ->
    val token = tokens.first() as Token.Name
    listOf(Name(token.value))
  }

  val openBraceNib = takeCond<Token> {
    it is Token.OpenBrace }.map {
    listOf(OpenBrace)
  }

  val closeBraceNib = takeCond<Token> {
    it is Token.CloseBrace }.map {
      listOf(CloseBrace)
    }

  val openParenNib = takeCond<Token> {
    it is Token.OpenParen }.map {
    listOf(OpenParen)
  }

  val closeParenNib = takeCond<Token> {
    it is Token.CloseParen }.map {
      listOf(CloseParen)
    }

  val arrowNib = takeCond<Token> {
    it is Token.Arrow }.map {
      listOf(Arrow)
    }

//  val colonNib = takeCond<Token> { it.tokenType == TokenType.Colon }.map {
//    listOf(Arrow)
//  }
//


//    { gaze ->
//  for {
//    _ <- gaze.attempt(openBraceNib)
//    expression <- gaze.attempt(optional(repeat(elementNib)))
//    _ <- gaze.attempt(closeBraceNib)
//  } yield Seq(Scope(expression.toList))
//  }



  val parameterNib: Nibbler<Token, Parameter> = nameNib.map { listOf(Parameter(it[0])) }
    //{ gaze ->
    //TODO()
//  for {
//    name <- gaze.attempt(
//      nameNib
//    ) // .map { name => name.map(name => Parameter(name)) }
//    _ <- gaze.attempt(colonNib)
//    typeName <- gaze.attempt(typeNib)
//  } yield Seq(Parameter(name.first(), typeName.first()))
  //}

  val wanderFunctionNib: Nibbler<Token, FunctionDefinition> = takeAllGrouped(
    openParenNib,
    optional(repeat(parameterNib)),
    closeParenNib,
    arrowNib,
    ::expressionNib // or scopeNib?
  ).map { tokens: List<List<Element>> ->
    val parameters = tokens[1].map { it as Parameter }
    val body = tokens[4][0] as Expression
    listOf(WanderFunction(parameters, body))
  }

//{ gaze ->
//    TODO()
//  for {
//    _ <- gaze.attempt(openParenNib)
//    parameters <- gaze.attempt(optional(repeat(parameterNib)))
//    _ <- gaze.attempt(closeParenNib)
//    _ <- gaze.attempt(arrowNib)
//    returnType <- gaze.attempt(typeNib)
//    body <- gaze.attempt(scopeNib)
//  } yield Seq(WanderFunction(parameters.toList, returnType.first(), body.first()))
//  }

//  val typeNib: Nibbler<Token, WanderType> = takeFirst(
//    take(Token("Integer", TokenType.Name)).map { listOf(SimpleType.Integer) },
//    take(Token("Identifier", TokenType.Name)).map {
//      listOf(SimpleType.Identifier)
//    },
//    take(Token("Value", TokenType.Name)).map { listOf(SimpleType.Value) }
//  )
//
  val ifKeywordNib = takeCond<Token> {
    it is Token.IfKeyword } .map {
    listOf(LetKeyword) //TODO should probably add an IfKeyword
  }

  val elseKeywordNib =
    takeCond<Token> {
      it is Token.ElseKeyword }.map {
      listOf(LetKeyword) //TODO should probably add an ElseKeyword
    }

  val elseIfExpressionNib: Nibbler<Token, ElseIf> = takeAllGrouped(
    elseKeywordNib,
    ifKeywordNib,
    ::expressionNib,
    ::expressionNib
  ).map { tokens: List<List<Element>> ->
    listOf(ElseIf(tokens[2][0] as Expression, tokens[3][0] as Expression))
  }
    //{ gaze ->
    //TODO()
//  for {
//    _ <- gaze.attempt(elseKeywordNib)
//    _ <- gaze.attempt(ifKeywordNib)
//    condition <- gaze.attempt(expressionNib)
//    body <- gaze.attempt(expressionNib)
//  } yield Seq(ElseIf(condition.first(), body.first()))
//  }

  val elseExpressionNib: Nibbler<Token, Else> = takeAllGrouped(
    elseKeywordNib,
    ::expressionNib
  ). map { tokens: List<List<Element>> ->
    listOf(Else(tokens[1][0] as Expression))
  }

    //{ gaze ->
    //TODO()
//  for {
//    _ <- gaze.attempt(elseKeywordNib)
//    body <- gaze.attempt(expressionNib)
//  } yield Seq(Else(body.first()))
//  }

  val ifExpressionNib: Nibbler<Token, IfExpression> = takeAllGrouped(
    ifKeywordNib,
    ::expressionNib,
    ::expressionNib,
    optional(repeat(elseIfExpressionNib)),
    optional(elseExpressionNib)
  ).map { tokens: List<List<Element>> ->
    val elseIfs = tokens[3].map { it as ElseIf }
    val elseValue = if (tokens[4].size == 1) {
      Some(tokens[4][0] as Else)
    } else {
      none()
    }
    listOf(
      IfExpression(
        tokens[1][0] as Expression,
        tokens[2][0] as Expression,
        elseIfs,
        elseValue
      )
    )
  }
    //{ gaze ->
   /// TODO()
//  for {
//    _ <- gaze.attempt(ifKeywordNib)
//    condition <- gaze.attempt(expressionNib)
//    body <- gaze.attempt(expressionNib)
//    elseIfs <- gaze.attempt(optional(repeat(elseIfExpressionNib)))
//    `else` <- gaze.attempt(optional(elseExpressionNib))
//  } yield Seq(
//    IfExpression(
//      condition.first(),
//      body.first(),
//      elseIfs.toList,
//      `else`.toList.find(_ => true)
//    )
//  )
//  }

  val functionCallNib: Nibbler<Token, FunctionCall> = takeAllGrouped(
      nameNib,
      openParenNib,
      optional(repeat(::expressionNib)),
      closeParenNib,
  ).map { tokens: List<List<Element>> ->
    val parameters = tokens[2].map { it as Expression }
    listOf(FunctionCall(tokens[0][0] as Name, parameters))
  }

  val equalSignNib = takeCond<Token> {
    it is Token.EqualSign }.map {
      listOf(EqualSign)
    }

  val letKeywordNib = takeCond<Token> {
    it is Token.LetKeyword }.map {
      listOf(LetKeyword)
    }

  val letStatementNib: Nibbler<Token, LetStatement> = takeAllGrouped(
    letKeywordNib,
    nameNib,
    equalSignNib,
    ::expressionNib
  ).map { tokens: List<List<Element>> ->
    listOf(LetStatement(tokens[1][0] as Name, tokens[3][0] as Expression))
  }
    //{ gaze ->
//    TODO()
//  for {
//    _ <- gaze.attempt(letKeywordNib)
//    name <- gaze.attempt(nameNib)
//    _ <- gaze.attempt(equalSignNib)
//    expression <- gaze.attempt(expressionNib)
//  } yield Seq(LetStatement(name.first(), expression.first()))
//  }

  val elementNib = takeFirst(::expressionNib, letStatementNib)

  val scopeNib: Nibbler<Token, Scope> = between(
    openBraceNib,
    optional(repeat(elementNib)),
    closeBraceNib
  ).map { tokens: List<Element> ->
    listOf(Scope(tokens))
  }

  fun expressionNib(gaze: Gaze<Token>): List<Expression>? =
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
      //TODO bytes literal
    )(gaze)

  val scriptNib: Nibbler<Token, Element> =
    optional(
      repeat(
        elementNib
      )
    )
}

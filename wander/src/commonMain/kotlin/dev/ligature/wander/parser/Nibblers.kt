/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.BytesLiteral
import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.StringLiteral
import dev.ligature.gaze.*
import dev.ligature.wander.lexer.Token

object Nibblers {
//  //TODO this code might be better if takeCond allowed you to map values
//  //TODO try to use takeCondMap, this might get rid of all the `as`'s in this code
  val booleanNib: Nibbler<Token, Element.BooleanLiteral> = takeCond<Token> {
    it is Token.Boolean
  }.map { tokens: List<Token> ->
    val token = tokens.first() as Token.Boolean
    listOf(Element.BooleanLiteral(token.value))
  }
//
//  val identifierNib: Nibbler<Token, Element> = takeCond<Token> {
//    it is Token.Identifier
//  }.map { tokens: List<Token> ->
//    val token = tokens.first() as Token.Identifier
//    listOf(Element.IdentifierLiteral(Identifier(token.value)))
//  }
//
  val integerNib: Nibbler<Token, Element.IntegerLiteral> = takeCond<Token> {
    it is Token.Integer
  }.map { tokens: List<Token> ->
    val token = tokens.first() as Token.Integer
    listOf(Element.IntegerLiteral(token.value.toLong()))
  }
//
//  val stringNib: Nibbler<Token, Element> = takeCond<Token> {
//    it is Token.StringLiteral
//  }.map { tokens: List<Token> ->
//    val token = tokens.first() as Token.StringLiteral
//    listOf(Element.StringLiteral(token.value))
//  }
//
////TODO add back
////  val bytesNib: Nibbler<Token, Element> = takeCond<Token> {
////    it is Token.BytesLiteral
////  }.map { tokens: List<Token> ->
////    val token = tokens.first() as Token.BytesLiteral
////    listOf(LigatureValue(BytesLiteral(byteArrayOf()))) //TODO set actual value
////  }
//
//  val nameNib = takeCond<Token> {
//    it is Token.Name
//  }.map { tokens ->
//    val token = tokens.first() as Token.Name
//    listOf(Element.Name(token.value))
//  }
//
//  val openSquareNib = takeCond<Token> {
//    it is Token.OpenSquare }.map {
//    listOf(Token.OpenSquare)
//  }
//
//  val closeSquareNib = takeCond<Token> {
//    it is Token.CloseSquare }.map {
//    listOf(Token.CloseSquare)
//  }
//
//  val openBraceNib = takeCond<Token> {
//    it is Token.OpenBrace }.map {
//    listOf(Token.OpenBrace)
//  }
//
//  val closeBraceNib = takeCond<Token> {
//    it is Token.CloseBrace }.map {
//      listOf(Token.CloseBrace)
//    }
//
//  val openParenNib = takeCond<Token> {
//    it is Token.OpenParen }.map {
//    listOf(Token.OpenParen)
//  }
//
//  val closeParenNib = takeCond<Token> {
//    it is Token.CloseParen }.map {
//      listOf(Token.CloseParen)
//    }
//
//  val arrowNib = takeCond<Token> {
//    it is Token.Arrow }.map {
//      listOf(Token.Arrow)
//    }
//
////TODO probably get rid of?
////  val parameterNib: Nibbler<Token, Parameter> = nameNib.map { listOf(Parameter(it[0])) }
////    //{ gaze ->
////    //TODO()
//////  for {
//////    name <- gaze.attempt(
//////      nameNib
//////    ) // .map { name => name.map(name => Parameter(name)) }
//////    _ <- gaze.attempt(colonNib)
//////    typeName <- gaze.attempt(typeNib)
//////  } yield Seq(Parameter(name.first(), typeName.first()))
////  //}
//
//  val equalSignNib = takeCond<Token> {
//    it is Token.EqualSign }.map {
//    listOf(Token.EqualSign)
//  }
//
//  val letKeywordNib = takeCond<Token> {
//    it is Token.LetKeyword }.map {
//    listOf(Token.LetKeyword)
//  }
//
//  val letStatementNib: Nibbler<Token, Element.LetStatement> = takeAllGrouped(
//    letKeywordNib,
//    nameNib,
//    equalSignNib,
//    ::expressionNib
//  ).map { tokens: List<List<Element>> ->
//    listOf(LetStatement(tokens[1][0] as String, tokens[3][0] as Expression))
//  }
//  //{ gaze ->
////    TODO()
////  for {
////    _ <- gaze.attempt(letKeywordNib)
////    name <- gaze.attempt(nameNib)
////    _ <- gaze.attempt(equalSignNib)
////    expression <- gaze.attempt(expressionNib)
////  } yield Seq(LetStatement(name.first(), expression.first()))
////  }
//
  val elementNib = takeFirst(::expressionNib)//, letStatementNib)
//
//  /**
//   * Nib the handles lambda definitions
//   * eg:
//   * { x -> x }
//   */
//  val wanderFunctionNib: Nibbler<Token, FunctionDefinition> = takeAllGrouped(
//      openBraceNib,
//      optional(repeat(parameterNib)),
//      arrowNib,
//      optional(repeat(elementNib)),
//      closeBraceNib
//    ).map { tokens: List<List<Element>> ->
//    val parameters = tokens[1].map { it as Parameter }
//    val body = tokens[3]
//    listOf(WanderFunction(parameters, Scope(body)))
//  }
//
////{ gaze ->
////    TODO()
////  for {
////    _ <- gaze.attempt(openParenNib)
////    parameters <- gaze.attempt(optional(repeat(parameterNib)))
////    _ <- gaze.attempt(closeParenNib)
////    _ <- gaze.attempt(arrowNib)
////    returnType <- gaze.attempt(typeNib)
////    body <- gaze.attempt(scopeNib)
////  } yield Seq(WanderFunction(parameters.toList, returnType.first(), body.first()))
////  }
//
////  val typeNib: Nibbler<Token, WanderType> = takeFirst(
////    take(Token("Integer", TokenType.Name)).map { listOf(SimpleType.Integer) },
////    take(Token("Identifier", TokenType.Name)).map {
////      listOf(SimpleType.Identifier)
////    },
////    take(Token("Value", TokenType.Name)).map { listOf(SimpleType.Value) }
////  )
////
//  val ifKeywordNib = takeCond<Token> {
//    it is Token.IfKeyword } .map {
//    listOf(LetKeyword) //TODO add new element
//  }
//
//  val elsifKeywordNib = takeCond<Token> {
//    it is Token.ElsifKeyword } .map {
//    listOf(LetKeyword) //TODO add new element
//  }
//
//  val elseKeywordNib = takeCond<Token> {
//      it is Token.ElseKeyword }.map {
//      listOf(LetKeyword) //TODO add new element
//    }
//
//  val elsifExpressionNib: Nibbler<Token, Elsif> = takeAllGrouped(
//    elsifKeywordNib,
//    ::expressionNib,
//    ::expressionNib
//  ).map { tokens: List<List<Element>> ->
//    listOf(Elsif(tokens[1][0] as Expression, tokens[2][0] as Expression))
//  }
//    //{ gaze ->
//    //TODO()
////  for {
////    _ <- gaze.attempt(elseKeywordNib)
////    _ <- gaze.attempt(ifKeywordNib)
////    condition <- gaze.attempt(expressionNib)
////    body <- gaze.attempt(expressionNib)
////  } yield Seq(ElseIf(condition.first(), body.first()))
////  }
//
//  val elseExpressionNib: Nibbler<Token, Else> = takeAllGrouped(
//    elseKeywordNib,
//    ::expressionNib
//  ). map { tokens: List<List<Element>> ->
//    listOf(Else(tokens[1][0] as Expression))
//  }
//
//    //{ gaze ->
//    //TODO()
////  for {
////    _ <- gaze.attempt(elseKeywordNib)
////    body <- gaze.attempt(expressionNib)
////  } yield Seq(Else(body.first()))
////  }
//
//  val ifExpressionNib: Nibbler<Token, IfExpression> = takeAllGrouped(
//    ifKeywordNib,
//    ::expressionNib,
//    ::expressionNib,
//    optional(repeat(elsifExpressionNib)),
//    optional(elseExpressionNib)
//  ).map { tokens: List<List<Element>> ->
//    val elsifs = tokens[3].map { it as Elsif }
//    val elseValue: Else? = if (tokens[4].size == 1) {
//      tokens[4][0] as Else
//    } else {
//      null
//    }
//    listOf(
//      IfExpression(
//        tokens[1][0] as Expression,
//        tokens[2][0] as Expression,
//        elsifs,
//        elseValue
//      )
//    )
//  }
//    //{ gaze ->
//   /// TODO()
////  for {
////    _ <- gaze.attempt(ifKeywordNib)
////    condition <- gaze.attempt(expressionNib)
////    body <- gaze.attempt(expressionNib)
////    elseIfs <- gaze.attempt(optional(repeat(elseIfExpressionNib)))
////    `else` <- gaze.attempt(optional(elseExpressionNib))
////  } yield Seq(
////    IfExpression(
////      condition.first(),
////      body.first(),
////      elseIfs.toList,
////      `else`.toList.find(_ => true)
////    )
////  )
////  }
//
//  val functionCallNib: Nibbler<Token, FunctionCall> = takeAllGrouped(
//      nameNib,
//      openParenNib,
//      optional(repeat(::expressionNib)),
//      closeParenNib,
//  ).map { tokens: List<List<Element>> ->
//    val parameters = tokens[2].map { it as Expression }
//    listOf(FunctionCall(tokens[0][0] as Name, parameters))
//  }
//
//  val seqNib: Nibbler<Token, Expression> = between(
//    openSquareNib,
//    optional(repeat(::expressionNib)),
//    closeSquareNib
//  ).map { tokens: List<Element> ->
//    val contents: List<Expression> = tokens as List<Expression>
//    listOf(Seq(contents))
//  }
//
//  val scopeNib: Nibbler<Token, Scope> = between(
//    openBraceNib,
//    optional(repeat(elementNib)),
//    closeBraceNib
//  ).map { tokens: List<Element> ->
//    listOf(Scope(tokens))
//  }
//
  fun expressionNib(gaze: Gaze<Token>): List<Element.Expression>? =
    takeFirst(
//      ifExpressionNib,
//      functionCallNib,
//      nameNib,
//      scopeNib,
//      identifierNib,
//      wanderFunctionNib,
//      stringNib,
      integerNib,
      booleanNib,
      //TODO bytes literal
//      seqNib
    )(gaze)

  val scriptNib: Nibbler<Token, Element> =
    optional(
      repeat(
        elementNib
      )
    )
}

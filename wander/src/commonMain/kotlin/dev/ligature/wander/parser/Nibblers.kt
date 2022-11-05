/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import dev.ligature.Identifier
import dev.ligature.gaze.*
import dev.ligature.wander.lexer.Token
import dev.ligature.wander.model.Element
import dev.ligature.wander.model.Parameter
import dev.ligature.wander.model.WanderType

object Nibblers {
  //  //TODO this code might be better if takeCond allowed you to map values
  //  //TODO try to use takeCondMap, this might get rid of all the `as`'s in this code
  val booleanNib: Nibbler<Token, Element.BooleanLiteral> =
      takeCond<Token> { it is Token.Boolean }
          .map { tokens: List<Token> ->
            val token = tokens.first() as Token.Boolean
            listOf(Element.BooleanLiteral(token.value))
          }

  val identifierNib: Nibbler<Token, Element.IdentifierLiteral> =
      takeCond<Token> { it is Token.Identifier }
          .map { tokens: List<Token> ->
            val token = tokens.first() as Token.Identifier
            listOf(Element.IdentifierLiteral(Identifier(token.value)))
          }

  val integerNib: Nibbler<Token, Element.IntegerLiteral> =
      takeCond<Token> { it is Token.Integer }
          .map { tokens: List<Token> ->
            val token = tokens.first() as Token.Integer
            listOf(Element.IntegerLiteral(token.value.toLong()))
          }

  val stringNib: Nibbler<Token, Element.StringLiteral> =
      takeCond<Token> { it is Token.StringLiteral }
          .map { tokens: List<Token> ->
            val token = tokens.first() as Token.StringLiteral
            listOf(Element.StringLiteral(token.value))
          }

  //// TODO add back
  ////  val bytesNib: Nibbler<Token, Element> = takeCond<Token> {
  ////    it is Token.BytesLiteral
  ////  }.map { tokens: List<Token> ->
  ////    val token = tokens.first() as Token.BytesLiteral
  ////    listOf(LigatureValue(BytesLiteral(byteArrayOf()))) //TODO set actual value
  ////  }

  val nameNib =
      takeCond<Token> { it is Token.Name }
          .map { tokens ->
            val token = tokens.first() as Token.Name
            listOf(Element.Name(token.value))
          }

  val questionMarkNib =
      takeCond<Token> { it is Token.QuestionMark }.map { listOf<Element.Nothing>(Element.Nothing) }

  val openSquareNib = takeCond<Token> { it is Token.OpenSquare }.map { listOf<Element>() }

  val closeSquareNib = takeCond<Token> { it is Token.CloseSquare }.map { listOf<Element>() }

  val openBraceNib = takeCond<Token> { it is Token.OpenBrace }.map { listOf<Element>() }

  val closeBraceNib = takeCond<Token> { it is Token.CloseBrace }.map { listOf<Element>() }

  val openParenNib = takeCond<Token> { it is Token.OpenParen }.map { listOf<Element>() }

  val closeParenNib = takeCond<Token> { it is Token.CloseParen }.map { listOf<Element>() }

  val dotNib = takeCond<Token> { it is Token.Dot }.map { listOf<Element>() }

  val arrowNib = takeCond<Token> { it is Token.Arrow }.map { listOf<Element>() }

  val equalSignNib = takeCond<Token> { it is Token.EqualSign }.map { listOf<Element>() }

  val letKeywordNib = takeCond<Token> { it is Token.LetKeyword }.map { listOf<Element>() }

  val letStatementNib: Nibbler<Token, Element.LetStatement> =
      takeAllGrouped(letKeywordNib, nameNib, equalSignNib, ::expressionNib).map {
          tokens: List<List<Element>> ->
        listOf(
            Element.LetStatement(
                (tokens[1][0] as Element.Name).name, tokens[3][0] as Element.Expression))
      }

  val elementNib = takeFirst(::expressionNib, letStatementNib)

  /** Nib the handles lambda definitions eg: { x -> x } */
  val lambdaDefinitionNib: Nibbler<Token, Element.LambdaDefinition> =
      takeAllGrouped(
              openBraceNib,
              optional(repeat(nameNib)),
              arrowNib,
              optional(repeat(elementNib)),
              closeBraceNib)
          .map { tokens: List<List<Element>> ->
            // TODO update below with actual types not just Any
            val parameters = tokens[1].map { Parameter((it as Element.Name).name, WanderType.Any) }
            val body = tokens[3]
            listOf(Element.LambdaDefinition(parameters, body))
          }

  val ifExpressionNib: Nibbler<Token, Element.IfExpression> =
      nib@{ gaze ->
        if (gaze.next() != Token.IfKeyword) {
          return@nib null
        }
        val ifCondition = gaze.attempt(::expressionNib)?.first()
        val ifBody = gaze.attempt(::expressionNib)?.first()
        if (ifCondition == null || ifBody == null) {
          return@nib null
        }
        val ifConditional = Element.Conditional(ifCondition, ifBody)
        val elsifConditionals = mutableListOf<Element.Conditional>()
        while (gaze.peek() == Token.ElsifKeyword) {
          gaze.next()
          val condition = gaze.attempt(::expressionNib)?.first()
          val body = gaze.attempt(::expressionNib)?.first()
          if (condition == null || body == null) {
            return@nib null
          }
          elsifConditionals.add(Element.Conditional(condition, body))
        }
        val elseBody =
            if (gaze.peek() == Token.ElseKeyword) {
              gaze.next()
              gaze.attempt(::expressionNib)?.first()
            } else {
              null
            }
        listOf(Element.IfExpression(ifConditional, elsifConditionals, elseBody))
      }

  val functionCallNib: Nibbler<Token, Element.FunctionCall> =
      takeAllGrouped(
              nameNib,
              openParenNib,
              optional(repeat(::expressionNib)),
              closeParenNib,
          )
          .map { tokens: List<List<Element>> ->
            val parameters = tokens[2].map { it as Element.Expression }
            listOf(Element.FunctionCall((tokens[0][0] as Element.Name).name, parameters))
          }

  /**
   * This function does the following
   * - read .
   * - read functionName
   * - read (
   * - read remainingArguments
   * - read )
   * - returns Element.FunctionCall(functionName, listOf(firstArg, remainingArguments)
   */
  fun readDotCall(gaze: Gaze<Token>, firstArg: Element.Expression): Element.FunctionCall? {
    val parameters = mutableListOf<Element.Expression>()
    parameters.add(firstArg)
    if (gaze.next() != Token.Dot) {
      return null
    }
    if (gaze.peek() !is Token.Name) {
      return null
    }
    val name = (gaze.next() as Token.Name).value
    if (gaze.next() != Token.OpenParen) {
      return null
    }
    val remainingParameters = optional(repeat(::expressionNib))(gaze) ?: listOf()
    parameters.addAll(remainingParameters)
    if (gaze.next() != Token.CloseParen) {
      return null
    }
    return Element.FunctionCall(name, parameters)
  }

  val methodCallNib: Nibbler<Token, Element.FunctionCall> =
      nib@{ gaze ->
        var firstParameter: Element.Expression = parameterNib(gaze)?.first() ?: return@nib null
        if (gaze.peek() != Token.Dot) {
          return@nib null
        }
        while (gaze.peek() == Token.Dot) {
          firstParameter = readDotCall(gaze, firstParameter) ?: return@nib null
        }
        listOf(firstParameter as Element.FunctionCall)
      }

  val seqNib: Nibbler<Token, Element.Seq> =
      between(openSquareNib, optional(repeat(::expressionNib)), closeSquareNib).map {
          tokens: List<Element> ->
        val contents: List<Element.Expression> = tokens as List<Element.Expression>
        listOf(Element.Seq(contents))
      }

  val scopeNib: Nibbler<Token, Element.Scope> =
      between(openBraceNib, optional(repeat(elementNib)), closeBraceNib).map { tokens: List<Element>
        ->
        listOf(Element.Scope(tokens))
      }

  // this is only used currently for method call syntax
  fun parameterNib(gaze: Gaze<Token>): List<Element.Expression>? =
      takeFirst(
          ifExpressionNib,
          functionCallNib,
          nameNib,
          scopeNib,
          identifierNib,
          lambdaDefinitionNib,
          stringNib,
          integerNib,
          booleanNib,
          questionMarkNib,
          // TODO bytes literal
          seqNib,
      )(gaze)

  fun expressionNib(gaze: Gaze<Token>): List<Element.Expression>? =
      takeFirst(
          methodCallNib,
          ifExpressionNib,
          functionCallNib,
          nameNib,
          scopeNib,
          identifierNib,
          lambdaDefinitionNib,
          stringNib,
          integerNib,
          booleanNib,
          questionMarkNib,
          // TODO bytes literal
          seqNib,
      )(gaze)

  val scriptNib: Nibbler<Token, Element> = optional(repeat(elementNib))
}

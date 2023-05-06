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
import dev.ligature.{IntegerLiteral, Identifier, StringLiteral}
import dev.ligature.wander.Token

def parse(script: Seq[Token]): Either[String, Script] = {
  val filteredInput = script.filter {
    _ match
      case Token.Spaces(_) | Token.NewLine | Token.Comment => false
      case _ => true
  }
  val gaze = Gaze(filteredInput)
  val res: Option[Seq[Element]] =  None //gaze.attempt(scriptNib)
  res match {
    case None =>
      if (gaze.isComplete) {
        Right(Script(Seq()))
      } else {
        Left("No Match")
      }
    // TODO some case also needs to check if gaze is complete
    case Some(res) =>
      if (gaze.isComplete) {
        Right(Script(res)) // .filter(_.isDefined).map(_.get)))
      } else {
        Left("No Match")
      }
  }
}

val booleanNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.BooleanLiteral(b)) => Some(List(Term.BooleanLiteral(b)))
    case _ => None

val identifierNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.Identifier(i)) => Some(List(Term.IdentifierLiteral(i)))
    case _ => None

val intergerNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.IntegerLiteral(i)) => Some(List(Term.IntegerLiteral(i)))
    case _ => None

val stringNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.StringLiteral(s)) => Some(List(Term.StringLiteral(s)))
    case _ => None

val nameNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.Name(n)) => Some(List(Term.Name(n)))
    case _ => None

// val openBraceNib = takeCond[Token](_.tokenType == TokenType.OpenBrace).map { _ =>
//   Seq(OpenBrace)
// }

// val closeBraceNib =
//   takeCond[Token](_.tokenType == TokenType.CloseBrace).map { _ =>
//     Seq(CloseBrace)
//   }

// val openParenNib = takeCond[Token](_.tokenType == TokenType.OpenParen).map { _ =>
//   Seq(OpenParen)
// }

// val closeParenNib =
//   takeCond[Token](_.tokenType == TokenType.CloseParen).map { _ =>
//     Seq(CloseParen)
//   }

// val arrowNib = takeCond[Token](_.tokenType == TokenType.Arrow).map { _ =>
//   Seq(Colon)
// }

// val colonNib = takeCond[Token](_.tokenType == TokenType.Colon).map { _ =>
//   Seq(Arrow)
// }

// val scopeNib: Nibbler[Token, Scope] = { gaze =>
//   for {
//     _ <- gaze.attempt(openBraceNib)
//     expression <- gaze.attempt(optional(repeat(elementNib)))
//     _ <- gaze.attempt(closeBraceNib)
//   } yield Seq(Scope(expression.toList))
// }

// val parameterNib: Nibbler[Token, Parameter] = { gaze =>
//   for {
//     name <- gaze.attempt(
//       nameNib
//     ) // .map { name => name.map(name => Parameter(name)) }
//     _ <- gaze.attempt(colonNib)
//     typeName <- gaze.attempt(typeNib)
//   } yield Seq(Parameter(name.head, typeName.head))
// }

// val wanderFunctionNib: Nibbler[Token, FunctionDefinition] = { gaze =>
//   for {
//     _ <- gaze.attempt(openParenNib)
//     parameters <- gaze.attempt(optional(repeat(parameterNib)))
//     _ <- gaze.attempt(closeParenNib)
//     _ <- gaze.attempt(arrowNib)
//     returnType <- gaze.attempt(typeNib)
//     body <- gaze.attempt(scopeNib)
//   } yield Seq(WanderFunction(parameters.toList, returnType.head, body.head))
// }

// val typeNib: Nibbler[Token, WanderType] = takeFirst(
//   take(Token("Integer", TokenType.Name)).map(_ => List(WanderType.Integer)),
//   take(Token("Identifier", TokenType.Name)).map { _ =>
//     List(WanderType.Identifier)
//   },
//   take(Token("Value", TokenType.Name)).map(_ => List(WanderType.Value))
// )

// val ifKeywordNib =
//   takeCond[Token](_.tokenType == TokenType.IfKeyword).map { _ =>
//     Seq(LetKeyword)
//   }

// val elseKeywordNib =
//   takeCond[Token](_.tokenType == TokenType.ElseKeyword).map { _ =>
//     Seq(LetKeyword)
//   }

// val elseIfExpressionNib: Nibbler[Token, ElseIf] = { gaze =>
//   for {
//     _ <- gaze.attempt(elseKeywordNib)
//     _ <- gaze.attempt(ifKeywordNib)
//     condition <- gaze.attempt(expressionNib)
//     body <- gaze.attempt(expressionNib)
//   } yield Seq(ElseIf(condition.head, body.head))
// }

// val elseExpressionNib: Nibbler[Token, Else] = { gaze =>
//   for {
//     _ <- gaze.attempt(elseKeywordNib)
//     body <- gaze.attempt(expressionNib)
//   } yield Seq(Else(body.head))
// }

// val ifExpressionNib: Nibbler[Token, IfExpression] = { gaze =>
//   for {
//     _ <- gaze.attempt(ifKeywordNib)
//     condition <- gaze.attempt(expressionNib)
//     body <- gaze.attempt(expressionNib)
//     elseIfs <- gaze.attempt(optional(repeat(elseIfExpressionNib)))
//     `else` <- gaze.attempt(optional(elseExpressionNib))
//   } yield Seq(
//     IfExpression(
//       condition.head,
//       body.head,
//       elseIfs.toList,
//       `else`.toList.find(_ => true)
//     )
//   )
// }

// val functionCallNib: Nibbler[Token, FunctionCall] = { gaze =>
//   for {
//     name <- gaze.attempt(nameNib)
//     _ <- gaze.attempt(openParenNib)
//     parameters <- gaze.attempt(optional(repeat(expressionNib)))
//     _ <- gaze.attempt(closeParenNib)
//   } yield Seq(FunctionCall(name.head, parameters.toList))
// }

// val expressionNib =
//   takeFirst(
//     ifExpressionNib,
//     functionCallNib,
//     nameNib,
//     scopeNib,
//     identifierNib,
//     wanderFunctionNib,
//     stringNib,
//     integerNib,
//     booleanNib
//   )

// val equalSignNib = takeCond[Token](_.tokenType == TokenType.EqualSign).map { _ =>
//   Seq(EqualSign)
// }

// val letKeywordNib =
//   takeCond[Token](_.tokenType == TokenType.LetKeyword).map { _ =>
//     Seq(LetKeyword)
//   }

// val letStatementNib: Nibbler[Token, LetStatement] = { gaze =>
//   for {
//     _ <- gaze.attempt(letKeywordNib)
//     name <- gaze.attempt(nameNib)
//     _ <- gaze.attempt(equalSignNib)
//     expression <- gaze.attempt(expressionNib)
//   } yield Seq(LetStatement(name.head, expression.head))
// }

// val elementNib = takeFirst(expressionNib, letStatementNib)

// val scriptNib =
//   optional(
//     repeat(
//       elementNib
//     )
//   )

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
import dev.ligature.{Identifier, LigatureLiteral}
import dev.ligature.wander.Token

enum Term:
  case Name(value: String)
  case IdentifierLiteral(value: Identifier)
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String)
  case BooleanLiteral(value: Boolean)
  case FunctionCall(name: Name, arguments: Seq[Term])

def evalTerm(term: Term, bindings: Bindings): Either[ScriptError, EvalResult] =
  term match
    case Term.BooleanLiteral(value) => Right(EvalResult(WanderValue.BooleanValue(value), bindings))
    case Term.IdentifierLiteral(value) => Right(EvalResult(WanderValue.LigatureValue(value), bindings))
    case Term.IntegerLiteral(value) => Right(EvalResult(WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(value)), bindings))
    case Term.StringLiteral(value) => Right(EvalResult(WanderValue.LigatureValue(LigatureLiteral.StringLiteral(value)), bindings))
    case Term.Name(value) => ???
    case Term.FunctionCall(name, arguments) =>
      //TODO val evaldArgs = evalArguments(arguments)
      bindings.read(WanderValue.Name(name.value)) match {
        case Left(value) => Left(value)
        case Right(value) =>
          value match {
            case WanderValue.NativeFunction(parameters, body, output) => {
              body(arguments, bindings).map { value => EvalResult(value, bindings) }
            }
            case WanderValue.WanderFunction(parameters, body, output) => ???
            case _ => ???
          }
      }

def parse(script: Seq[Token]): Either[String, Script] = {
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

val nameNib: Nibbler[Token, Term.Name] = gaze =>
  gaze.next() match
    case Some(Token.Name(n)) => Some(List(Term.Name(n)))
    case _ => None

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

val functionCallNib: Nibbler[Token, Term] = { gaze =>
  for
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(take(Token.OpenParen))
    parameters <- gaze.attempt(optional(repeat(expressionNib)))
    _ <- gaze.attempt(take(Token.CloseParen))
  yield Seq(Term.FunctionCall(name.head, parameters))
}

val expressionNib =
  takeFirst(
    // ifExpressionNib,
    functionCallNib,
    nameNib,
    // scopeNib,
    identifierNib,
    // wanderFunctionNib,
    stringNib,
    integerNib,
    booleanNib
  )

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

val elementNib = takeFirst(expressionNib)//, letStatementNib)

val scriptNib =
  optional(
    repeat(
      elementNib
    )
  )

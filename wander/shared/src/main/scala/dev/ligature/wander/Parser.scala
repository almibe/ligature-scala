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
import dev.ligature.wander.Token
import dev.ligature.gaze.Result
import dev.ligature.gaze.SeqSource
import dev.ligature.gaze.optionalSeq
import scala.collection.mutable.ListBuffer
import dev.ligature.gaze.repeatSep

case class Name(name: String)

enum Term:
  case NameTerm(value: Name)
  case IdentifierLiteral(value: Identifier)
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String)
  case BooleanLiteral(value: Boolean)
  case NothingLiteral
  case QuestionMark
  case Array(value: Seq[Term])
  case Set(value: Seq[Term])
  case Record(entires: Seq[(Name, Term)])
  case LetExpression(name: Name, term: Term)
  case Application(terms: Seq[Term])
  case Grouping(terms: Seq[Term])
  case Lambda(
    parameters: Seq[Name],
    body: Term)
  case Pipe

def parse(script: Seq[Token]): Either[WanderError, Term] = {
  val filteredInput = script.filter {
    _ match
      case Token.Spaces(_) | Token.NewLine | Token.Comment => false
      case _ => true
  }
  val gaze = Gaze(SeqSource(filteredInput))
  val res: Result[Term] = gaze.attempt(scriptNib)
  res match {
    case Result.NoMatch =>
      if (gaze.isComplete) {
        Right(Term.NothingLiteral)
      } else {
        Left(WanderError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
    case Result.Match(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(WanderError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
    case Result.EmptyMatch => Right(Term.NothingLiteral)
  }
}

val nothingNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.NothingKeyword) => Result.Match(Term.NothingLiteral)
    case _ => Result.NoMatch

val questionMarkTermNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.QuestionMark) => Result.Match(Term.QuestionMark)
    case _ => Result.NoMatch

val booleanNib: Nibbler[Token, Term.BooleanLiteral] = gaze =>
  gaze.next() match
    case Some(Token.BooleanLiteral(b)) => Result.Match(Term.BooleanLiteral(b))
    case _ => Result.NoMatch

val identifierNib: Nibbler[Token, Term.IdentifierLiteral] = gaze =>
  gaze.next() match
    case Some(Token.Identifier(i)) => Result.Match(Term.IdentifierLiteral(i))
    case _ => Result.NoMatch

val integerNib: Nibbler[Token, Term.IntegerLiteral] = gaze =>
  gaze.next() match
    case Some(Token.IntegerLiteral(i)) => Result.Match(Term.IntegerLiteral(i))
    case _ => Result.NoMatch

val stringNib: Nibbler[Token, Term.StringLiteral] = gaze =>
  gaze.next() match
    case Some(Token.StringLiteral(s)) => Result.Match(Term.StringLiteral(s))
    case _ => Result.NoMatch

val nameNib: Nibbler[Token, Term.NameTerm] = gaze =>
  gaze.next() match
    case Some(Token.Name(n)) => Result.Match(Term.NameTerm(Name(n)))
    case _ => Result.NoMatch

val parameterNib: Nibbler[Token, Name] = { gaze =>
  for {
    name <- gaze.attempt(nameNib)
  } yield name.value
}

val lambdaNib: Nibbler[Token, Term.Lambda] = { gaze =>
  for {
    _ <- gaze.attempt(take(Token.Lambda))
    parameters <- gaze.attempt(optionalSeq(repeat(parameterNib)))
    _ <- gaze.attempt(take(Token.Arrow))
    body <- gaze.attempt(expressionNib)
  } yield Term.Lambda(parameters, body) //TODO handle this body better
}

// val typeNib: Nibbler[Token, WanderType] = takeFirst(
//   take(Token("Integer", TokenType.Name)).map(_ => List(WanderType.Integer)),
//   take(Token("Identifier", TokenType.Name)).map { _ =>
//     List(WanderType.Identifier)
//   },
//   take(Token("Value", TokenType.Name)).map(_ => List(WanderType.Value))
// )

// val elseExpressionNib: Nibbler[Token, Term] = { gaze =>
//   for {
//     _ <- gaze.attempt(take(Token.ElseKeyword))
//     body <- gaze.attempt(expressionNib)
//   } yield body
// }

// val ifExpressionNib: Nibbler[Token, Term.IfExpression] = { gaze =>
//   for {
//     _ <- gaze.attempt(take(Token.IfKeyword))
//     condition <- gaze.attempt(expressionNib)
//     body <- gaze.attempt(expressionNib)
//     `else` <- gaze.attempt(optional(elseExpressionNib))
//   } yield
//     Term.IfExpression(
//       condition,
//       body,
//       `else`
//     )
// }

//NOTE: this will return either an Application or a Name.
val applicationNib: Nibbler[Token, Term] = { gaze =>
  val res = for
    name <- gaze.attempt(nameNib) //TODO this should also allow literals
    terms <- gaze.attempt(optionalSeq(repeat(expressionNib)))
  yield (name, terms)
  res match {
    case Result.NoMatch => Result.NoMatch
    case Result.Match((name, terms)) =>
      if terms.isEmpty then
        Result.Match(name)
      else Result.Match(Term.Application(Seq(name) ++ terms))
    case Result.EmptyMatch => Result.EmptyMatch
  }
}

val setNib: Nibbler[Token, Term.Set] = { gaze =>
  for
    _ <- gaze.attempt(take(Token.Hash))
    _ <- gaze.attempt(take(Token.OpenBracket))
    values <- gaze.attempt(optionalSeq(repeat(expressionNib)))
    _ <- gaze.attempt(take(Token.CloseBracket))
  yield Term.Set(values)
}

val arrayNib: Nibbler[Token, Term.Array] = { gaze =>
  for
    _ <- gaze.attempt(take(Token.OpenBracket))
    values <- gaze.attempt(optionalSeq(repeatSep(expressionNib, Token.Comma)))
    _ <- gaze.attempt(take(Token.CloseBracket))
  yield Term.Array(values)
}

val fieldsNib: Nibbler[Token, Seq[(Name, Term)]] = repeat(fieldNib)

val fieldNib: Nibbler[Token, (Name, Term)] = { gaze =>
  val res = for
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(take(Token.EqualSign))
    expression <- gaze.attempt(fieldExpressionNib)
  yield (name, expression)
  res match {
    case Result.Match((Term.NameTerm(name), term: Term)) => Result.Match((name, term))
    case _ => Result.NoMatch
  } 
}

val recordNib: Nibbler[Token, Term.Record] = { gaze =>
  for
    _ <- gaze.attempt(take(Token.OpenBrace))
    decls <- gaze.attempt(optionalSeq(repeatSep(fieldNib, Token.Comma)))
    _ <- gaze.attempt(take(Token.CloseBrace))
  yield Term.Record(decls)
}

val letExpressionNib: Nibbler[Token, Term.LetExpression] = { gaze =>
  for {
    _ <- gaze.attempt(take(Token.LetKeyword))
    name <- gaze.attempt(nameNib)
    value <- gaze.attempt(expressionNib)
  } yield Term.LetExpression(name.value, value)
}

// Reads an expression after the equals sign for a field.
// It should always read at least one term but after the first
// it needs to look ahead for the in keyword or equals signs.
val fieldExpressionNib: Nibbler[Token, Term] = { gaze =>
  val innerExpressionNib = takeFirst(
   // ifExpressionNib,
    nameNib,
    identifierNib,
    lambdaNib,
    stringNib,
    integerNib,
    recordNib,
    letExpressionNib,
    arrayNib,
    setNib,
    booleanNib,
    nothingNib,
    questionMarkTermNib
  )
  var obligatoryPart = gaze.attempt(innerExpressionNib) match
    case Result.Match(value) => value
    case Result.NoMatch | Result.EmptyMatch => ???
  val rest = ListBuffer[Term]()
  var continue = true
  while (continue) {
    //TODO instead of doing peeks here, I should do attempts or checks with innerExpressionNibs
    gaze.peek() match {
      case Some(Token.InKeyword) | Some(Token.EndKeyword) | Some(Token.CloseBrace) => {
        continue = false
      }
      case Some(Token.Name(_)) => {
        gaze.peek(1) match {
          case Some(Token.EqualSign) => continue = false
          case _ => ()
        }
      }
      case Some(Token.EqualSign) => {
        //TODO shouldn't reach this, this case just exists for debugging
        ???
      }
      case _ => ()
    }

    if (continue) {
      gaze.attempt(innerExpressionNib) match {
        case Result.EmptyMatch => ???
        case Result.NoMatch => continue = false
        case Result.Match(value) => {
          rest += value
        }
      }
    }
  }
  if rest.isEmpty then
    Result.Match(obligatoryPart)
  else
    Result.Match(Term.Application(Seq(obligatoryPart) ++ rest))
}

val expressionNib =
  takeFirst(
//    ifExpressionNib,
    applicationNib,
    identifierNib,
    lambdaNib,
    stringNib,
    integerNib,
    recordNib,
    letExpressionNib,
    arrayNib,
    setNib,
    booleanNib,
    nothingNib,
    questionMarkTermNib
  )

val scriptNib = expressionNib

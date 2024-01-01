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
import scala.util.boundary
import scala.util.boundary.break

case class Name(name: String)

case class TaggedName(name: Name, tag: Tag)

enum Tag:
  case Untagged
  case Single(name: Name)
  case Function(names: Seq[Name])

enum Term:
  case Import(name: Name)
  case NameTerm(value: Name)
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String, interpolated: Boolean = false)
  case BooleanLiteral(value: Boolean)
  case NothingLiteral
  case QuestionMark
  case Array(value: Seq[Term])
  case Binding(taggedName: TaggedName, term: Term, exportName: Boolean = false)
  case Record(values: Seq[(Name, Term)])
  case WhenExpression(conditionals: Seq[(Term, Term)])
  case Application(terms: Seq[Term])
  case Grouping(terms: Seq[Term])
  case Lambda(parameters: Seq[Name], body: Term)
  case Pipe

def parse(script: Seq[Token]): Either[WanderError, Seq[Term]] = {
  val filteredInput = script.filter {
    _ match
      case Token.Spaces(_) | Token.NewLine | Token.Comment => false
      case _                                               => true
  }
  val gaze = Gaze(SeqSource(filteredInput))
  val res: Result[Seq[Term]] = gaze.attempt(scriptNib)
  res match {
    case Result.NoMatch =>
      if (gaze.isComplete) {
        Right(Seq(Term.NothingLiteral))
      } else {
        Left(WanderError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
    case Result.Match(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(WanderError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
    case Result.EmptyMatch => Right(Seq(Term.NothingLiteral))
  }
}

val exportNib: Nibbler[Token, Boolean] = gaze =>
  gaze.attempt(optional(take(Token.ExportKeyword))) match
    case Result.NoMatch    => Result.Match(false)
    case Result.EmptyMatch => Result.Match(false)
    case Result.Match(_)   => Result.Match(true)

val importNib: Nibbler[Token, Term] = gaze =>
  for {
    _ <- gaze.attempt(take(Token.ImportKeyword))
    name <- gaze.attempt(importNameNib)
  } yield Term.Import(name) // TODO handle this body better

val nothingNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.NothingKeyword) => Result.Match(Term.NothingLiteral)
    case _                          => Result.NoMatch

val questionMarkTermNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.QuestionMark) => Result.Match(Term.QuestionMark)
    case _                        => Result.NoMatch

val booleanNib: Nibbler[Token, Term.BooleanLiteral] = gaze =>
  gaze.next() match
    case Some(Token.BooleanLiteral(b)) => Result.Match(Term.BooleanLiteral(b))
    case _                             => Result.NoMatch

val integerNib: Nibbler[Token, Term.IntegerLiteral] = gaze =>
  gaze.next() match
    case Some(Token.IntegerLiteral(i)) => Result.Match(Term.IntegerLiteral(i))
    case _                             => Result.NoMatch

val stringNib: Nibbler[Token, Term.StringLiteral] = gaze =>
  gaze.next() match
    case Some(Token.StringLiteral(s, i)) => Result.Match(Term.StringLiteral(s, i))
    case _                               => Result.NoMatch

val importNameNib: Nibbler[Token, Name] = gaze =>
  gaze.next() match
    case Some(Token.Name(n)) => Result.Match(Name(n))
    case _                   => Result.NoMatch

val nameNib: Nibbler[Token, Term.NameTerm] = gaze =>
  gaze.next() match
    case Some(Token.Name(n)) => Result.Match(Term.NameTerm(Name(n)))
    case _                   => Result.NoMatch

val tagNib: Nibbler[Token, Tag] = gaze =>
  val names = ListBuffer[Name]()
  boundary:
    while !gaze.isComplete do
      gaze.next() match
        case Some(Token.Name(name)) =>
          names.append(Name(name))
          gaze.peek() match {
            case Some(Token.Arrow) => gaze.next() // swallow arrow, ouch!
            case _                 => break()
          }
        case _ => break()
  names.toSeq match {
    case Seq()            => Result.NoMatch
    case Seq(name)        => Result.Match(Tag.Single(name))
    case names: Seq[Name] => Result.Match(Tag.Function(names))
  }

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

val conditionalsNib: Nibbler[Token, (Term, Term)] = { gaze =>
  for {
    condition <- gaze.attempt(expressionNib)
    _ <- gaze.attempt(take(Token.WideArrow))
    body <- gaze.attempt(expressionNib)
  } yield (condition, body)
}

val whenExpressionNib: Nibbler[Token, Term.WhenExpression] = { gaze =>
  for {
    _ <- gaze.attempt(takeAll(take(Token.WhenKeyword), take(Token.OpenParen)))
    conditionals <- gaze.attempt(optionalSeq(repeatSep(conditionalsNib, Token.Comma)))
    // `else` <- gaze.attempt(optional(elseExpressionNib))
    _ <- gaze.attempt(take(Token.CloseParen))
  } yield Term.WhenExpression(conditionals)
}

val arrayNib: Nibbler[Token, Term.Array] = { gaze =>
  for
    _ <- gaze.attempt(take(Token.OpenBracket))
    values <- gaze.attempt(optionalSeq(repeatSep(expressionNib, Token.Comma)))
    _ <- gaze.attempt(take(Token.CloseBracket))
  yield Term.Array(values)
}

val fieldNibNameOnly: Nibbler[Token, (Name, Term)] = { gaze =>
  val res =
    for name <- gaze.attempt(nameNib)
    yield name
  res match {
    case Result.Match(Term.NameTerm(name)) => Result.Match((name, Term.NameTerm(name)))
    case _                                 => Result.NoMatch
  }
}

val fieldNibNameValue: Nibbler[Token, (Name, Term)] = { gaze =>
  val res = for
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(take(Token.EqualSign))
    expression <- gaze.attempt(expressionNib)
  yield (name, expression)
  res match {
    case Result.Match((Term.NameTerm(name), term: Term)) => Result.Match((name, term))
    case _                                               => Result.NoMatch
  }
}

val fieldNib: Nibbler[Token, (Name, Term)] = takeFirst(fieldNibNameValue, fieldNibNameOnly)

val recordNib: Nibbler[Token, Term.Record] = { gaze =>
  val res = for
    _ <- gaze.attempt(take(Token.OpenBrace))
    fields <- gaze.attempt(optionalSeq(repeatSep(fieldNib, Token.Comma)))
    _ <- gaze.attempt(take(Token.CloseBrace))
  yield Term.Record(fields)
  res match
    case Result.Match(Term.Record(values)) => Result.Match(Term.Record(values))
    case _                                 => Result.NoMatch
}

val applicationNib: Nibbler[Token, Term] = { gaze =>
  val res =
    for decls <- gaze.attempt(repeat(applicationInternalNib))
    yield Term.Application(decls)
  res match {
    case Result.Match(Term.Application(Seq(singleValue))) =>
      Result.Match(singleValue)
    case _ => res
  }
}

val groupingNib: Nibbler[Token, Term.Grouping] = { gaze =>
  for
    _ <- gaze.attempt(take(Token.OpenParen))
    decls <- gaze.attempt(optionalSeq(repeatSep(expressionNib, Token.Comma)))
    _ <- gaze.attempt(take(Token.CloseParen))
  yield Term.Grouping(decls)
}

val bindingNib: Nibbler[Token, Term.Binding] = { gaze =>
  for {
    exportName <- gaze.attempt(exportNib)
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(take(Token.EqualSign))
    value <- gaze.attempt(expressionNib)
  } yield Term.Binding(TaggedName(name.value, Tag.Untagged), value, exportName)
}

val taggedBindingNib: Nibbler[Token, Term.Binding] = { gaze =>
  for {
    name <- gaze.attempt(nameNib)
    _ <- gaze.attempt(take(Token.Colon))
    tag <- gaze.attempt(tagNib)
    _ <- gaze.attempt(take(Token.EqualSign))
    value <- gaze.attempt(expressionNib)
  } yield Term.Binding(TaggedName(name.value, tag), value)
}

val applicationInternalNib =
  takeFirst(
    bindingNib,
    taggedBindingNib,
    nameNib,
    lambdaNib,
    groupingNib,
    stringNib,
    integerNib,
    whenExpressionNib,
    arrayNib,
    recordNib,
    booleanNib,
    nothingNib,
    questionMarkTermNib
  )

val expressionNib =
  takeFirst(
    importNib,
    bindingNib,
    taggedBindingNib,
    applicationNib,
    lambdaNib,
    groupingNib,
    stringNib,
    integerNib,
    whenExpressionNib,
    arrayNib,
    recordNib,
    booleanNib,
    nothingNib,
    questionMarkTermNib
  )

val scriptNib = optionalSeq(repeatSep(expressionNib, Token.Comma))

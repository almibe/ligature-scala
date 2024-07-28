/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.gaze.{Gaze, Nibbler, take, takeFirst, repeat}
import dev.ligature.gaze.Result
import dev.ligature.gaze.SeqSource
import dev.ligature.gaze.optionalSeq
import dev.ligature.gaze.repeatSep
import scala.util.boundary
import scala.util.boundary.break
import scala.collection.mutable.ArrayBuffer

enum Term:
  case Bytes(value: Seq[Byte])
  case Int(value: Long)
  case StringValue(value: String)
  case Slot(name: String)
  case Quote(value: Seq[Term])
  case Word(value: String)
  case Triple(entity: Term, attribute: Term, value: Term)
  case Network(roots: Seq[Triple])
  case Application(terms: Seq[Term])
  case Grouping(terms: Seq[Term])

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
        Right(Seq())
      } else {
        Left(WanderError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
    case Result.Match(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(WanderError(s"Error Parsing - No Match - Next Token: ${gaze.next()}"))
      }
    case Result.EmptyMatch => ??? //Right(Seq(Term.Module(Seq())))
  }
}

val slotTermNib: Nibbler[Token, Term] = gaze =>
  gaze.next() match
    case Some(Token.Slot(name)) => Result.Match(Term.Slot(name))
    case _                        => Result.NoMatch

val bytesNib: Nibbler[Token, Term.Bytes] = gaze =>
  gaze.next() match
    case Some(Token.Bytes(b)) => Result.Match(Term.Bytes(b))
    case _                    => Result.NoMatch

val integerNib: Nibbler[Token, Term.Int] = gaze =>
  gaze.next() match
    case Some(Token.Int(i)) => Result.Match(Term.Int(i))
    case _                           => Result.NoMatch

val stringNib: Nibbler[Token, Term.StringValue] = gaze =>
  gaze.next() match
    case Some(Token.StringValue(s)) => Result.Match(Term.StringValue(s))
    case _                             => Result.NoMatch

val wordNib: Nibbler[Token, Term.Word] = gaze =>
  gaze.next() match
    case Some(Token.Word(value)) => Result.Match(Term.Word(value))
    case _                        => Result.NoMatch

// val tagNib: Nibbler[Token, Option[Field]] = gaze =>
//   gaze.peek() match
//     case Some(Token.Field(name)) =>
//       gaze.next()
//       Result.Match(Some(Field(name)))
//     case _ =>
//       Result.NoMatch

// val tagNib: Nibbler[Token, Term.TaggedFieldTerm] = gaze =>
//   val names = ListBuffer[Field]()
//   boundary:
//     while !gaze.isComplete do
//       gaze.next() match
//         case Some(Token.Field(name)) =>
//           names.append(Field(name))
//           gaze.peek() match {
//             case Some(Token.Arrow) => gaze.next() // swallow arrow, ouch!
//             case _                 => break()
//           }
//         case _ => break()
//   names.toSeq match {
//     case Seq()                 => Result.NoMatch
//     case Seq(field)             => Result.Match(Term.TaggedFieldTerm(field))
//     case names => ???///Result.Match(Tag.Chain(names))
//   }

// val parameterNib: Nibbler[Token, Name] = { gaze =>
//   gaze.next() match
//     case Some(Token.Field(name)) => Result.Match(Name.from(name).getOrElse(???))
//     case _                      => Result.NoMatch
// }

// val lambdaNib: Nibbler[Token, Term.Lambda] = { gaze =>
//   for {
//     _ <- gaze.attempt(take(Token.Lambda))
//     parameters <- gaze.attempt(optionalSeq(repeat(fieldNib)))
//     _ <- gaze.attempt(take(Token.Arrow))
//     body <- gaze.attempt(expressionNib)
//   } yield Term.Lambda(parameters, body) // TODO handle this body better
// }

val quoteNib: Nibbler[Token, Term.Quote] = { gaze =>
  for
    _ <- gaze.attempt(take(Token.OpenBracket))
    values <- gaze.attempt(optionalSeq(repeat(expressionNib)))
    _ <- gaze.attempt(take(Token.CloseBracket))
  yield Term.Quote(values)
}

val tripleNib: Nibbler[Token, Term.Triple] = { gaze =>
  for
    entity <- gaze.attempt(takeFirst(wordNib, slotTermNib))
    attribute <- gaze.attempt(takeFirst(wordNib, slotTermNib))
    value <- gaze.attempt(takeFirst(wordNib, slotTermNib, integerNib, stringNib, quoteNib))
  yield Term.Triple(entity, attribute, value)
}

val networkNib: Nibbler[Token, Term.Network] = { gaze =>
  val res = for
    _ <- gaze.attempt(take(Token.OpenBrace))
    triples <- gaze.attempt(optionalSeq(repeatSep(tripleNib, Token.Comma)))
    _ <- gaze.attempt(take(Token.CloseBrace))
  yield Term.Network(triples)
  res match
    case Result.Match(Term.Network(values)) => Result.Match(Term.Network(values))
    case _                                => Result.NoMatch
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

val applicationInternalNib =
  takeFirst(
    groupingNib,
    wordNib,
    stringNib,
    bytesNib,
    integerNib,
    quoteNib,
    networkNib,
    slotTermNib,
  )

val expressionNib =
  takeFirst(
    applicationNib,
    groupingNib,
    wordNib,
    stringNib,
    bytesNib,
    integerNib,
    quoteNib,
    networkNib,
  )

//val scriptNib = optionalSeq(repeatSep(expressionNib, Token.Comma))
val scriptNib: Nibbler[Token, Seq[Term]] = { gaze =>
  var pipedValue: Option[Term] = None
  val results = ArrayBuffer[Term]()
  boundary:
    while !gaze.isComplete do
      gaze.attempt(expressionNib) match
        case Result.NoMatch    => break(Result.NoMatch)
        case Result.EmptyMatch => break(Result.NoMatch)
        case Result.Match(value: Term) =>
          gaze.next() match
            case Some(Token.Comma) | None =>
              pipedValue match
                case None => results += value
                case Some(pipedTerm: Term) =>
                  value match
                    case Term.Application(terms) =>
                      results += Term.Application(terms ++ Seq(pipedTerm))
                    case _ => break(Result.NoMatch)
            case Some(Token.Pipe) =>
              pipedValue match
                case None => pipedValue = Some(value)
                case Some(pipedTerm: Term) =>
                  value match
                    case Term.Application(terms) =>
                      pipedValue = Some(Term.Application(terms ++ Seq(pipedTerm)))
                    case _ => break(Result.NoMatch)
            case Some(_) => break(Result.NoMatch)
    break(Result.Match(results.toSeq))
}

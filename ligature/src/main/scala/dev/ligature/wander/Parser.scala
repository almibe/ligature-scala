/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.gaze.{Gaze, Nibbler, takeFirst}
import dev.ligature.gaze.Result
import dev.ligature.gaze.SeqSource
// import dev.ligature.gaze.optionalSeq
import dev.ligature.gaze.repeatSep
import scala.util.boundary
import scala.util.boundary.break
import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.takeUntil

enum Term:
  case Element(value: String)
  case Network(roots: Set[Entry])
  case Application(terms: Seq[Term])
  case Quote(terms: Seq[Term])

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

val elementNib: Nibbler[Token, Term.Element] = gaze =>
  gaze.next() match
    case Some(Token.Element(value)) => Result.Match(Term.Element(value))
    case _                          => Result.NoMatch

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

val networkNib: Nibbler[Token, Term.Network] = { _gaze => Result.NoMatch }
  // val res = for
  //   _ <- gaze.attempt(take(Token.OpenBrace))
  //   triples <- gaze.attempt(optionalSeq(repeatSep(tripleNib, Token.Comma)))
  //   _ <- gaze.attempt(take(Token.CloseBrace))
  // yield Term.Network(triples)
  // res match
  //   case Result.Match(Term.Network(values)) => Result.Match(Term.Network(values))
  //   case _                                => Result.NoMatch
//}

val applicationNib: Nibbler[Token, Term] = 
  takeUntil(Token.Comma).map(tokens => 
    parse(tokens) match {
      case _ => ???
    })
//   { gaze =>
//   val cont = true
//   while (cont) {
//     ???
//   }
//   Result.NoMatch
//   // for
//   //   entity <- gaze.attempt(elementNib) //gaze.attempt(takeFirst(wordNib, slotTermNib))
//   //   attribute <- gaze.attempt(elementNib) //gaze.attempt(takeFirst(wordNib, slotTermNib))
//   //   value <- gaze.attempt(elementNib)
//   // yield Term.Application(Seq())
// }

val expressionNib =
  takeFirst(
    elementNib,
    networkNib,
    applicationNib,
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

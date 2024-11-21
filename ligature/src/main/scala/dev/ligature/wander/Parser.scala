/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.gaze.{Gaze, Nibbler}
import dev.ligature.gaze.Result
import dev.ligature.gaze.SeqSource
import scala.util.boundary
import scala.util.boundary.break
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

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
    case Result.EmptyMatch => ??? // Right(Seq(Term.Module(Seq())))
  }
}

val elementNib: Nibbler[Token, Term.Element] = gaze =>
  gaze.next() match
    case Some(Token.Element(value)) => Result.Match(Term.Element(value))
    case _                          => Result.NoMatch

// Handles parsing a network, assumes the initial "{"" token has already been parsed.
val partialNetworkNib: Nibbler[Token, Term.Network] = { gaze =>
  var cont = true
  val entries = ListBuffer[Entry]()
  val currentEntry = ListBuffer[Element]()
  while cont do
    gaze.next() match {
      case Some(Token.Element(token)) =>
        currentEntry.addOne(Element(token))
      case Some(Token.CloseBrace) => 
        if (currentEntry.size == 3) {
          currentEntry(1) match {
            case Element(":")  => entries.addOne(Extends(currentEntry(0), currentEntry(2)))
            case Element("¬:") => entries.addOne(NotExtends(currentEntry(0), currentEntry(2)))
            case Element(element) =>
              entries.addOne(Role(currentEntry(0), currentEntry(1), currentEntry(2)))
          }
          currentEntry.clear()
        }
        cont = false
      case Some(Token.Comma) => {
        if (currentEntry.size == 3) {
          currentEntry(1) match {
            case Element(":")  => entries.addOne(Extends(currentEntry(0), currentEntry(2)))
            case Element("¬:") => entries.addOne(NotExtends(currentEntry(0), currentEntry(2)))
            case Element(element) =>
              entries.addOne(Role(currentEntry(0), currentEntry(1), currentEntry(2)))
          }
          currentEntry.clear()
        }
      }
      case None                   => ???
      case _                      => ???
    }
  Result.Match(Term.Network(entries.toSet))
}

// Handles parsing a quote, assumes the initial "("" token has already been parsed.
val partialQuoteNib: Nibbler[Token, Term.Quote] = { gaze =>
  var cont = true
  val terms = ListBuffer[Term]()
  while cont do
    gaze.next() match {
      case Some(Token.Element(element)) => terms.addOne(Term.Element(element))
      case Some(Token.CloseParen)       => cont = false
      case None                         => ???
      case _                            => ???
    }
  Result.Match(Term.Quote(terms.toSeq))
}

val applicationNib: Nibbler[Token, Term.Application] = { gaze =>
  val terms = ListBuffer[Term]()
  var cont = true
  while cont do
    gaze.next() match {
      case Some(Token.Element(element)) => terms.addOne(Term.Element(element))
      case Some(Token.Comma)            => cont = false
      case Some(Token.OpenParen) =>
        partialQuoteNib(gaze) match {
          case Result.NoMatch      => ???
          case Result.EmptyMatch   => ???
          case Result.Match(quote) => terms.addOne(quote)
        }
      case Some(Token.OpenBrace) =>
        partialNetworkNib(gaze) match {
          case Result.NoMatch        => ???
          case Result.EmptyMatch     => ???
          case Result.Match(network) => terms.addOne(network)
        }
      case None => cont = false
      case _    => ???
    }
  Result.Match(Term.Application(terms.toSeq))
}

val scriptNib: Nibbler[Token, Seq[Term]] = { gaze =>
  // var pipedValue: Option[Term] = None
  val results = ArrayBuffer[Term]()
  boundary:
    while !gaze.isComplete do
      gaze.attempt(applicationNib) match
        case Result.NoMatch     => break(Result.NoMatch)
        case Result.EmptyMatch  => break(Result.NoMatch)
        case Result.Match(term) => results.addOne(term)
    break(Result.Match(results.toSeq))
}

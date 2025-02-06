/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.gaze.{
  Gaze,
  Nibbler,
  optional,
  take,
  takeAll,
  takeFirst,
  takeString,
  takeUntil,
  takeWhile,
  repeat
}
import dev.ligature.gaze.Result
import dev.ligature.gaze.concat
import scala.collection.mutable.ArrayBuffer
import dev.ligature.wander.LigNibblers.wordNibbler

enum Token:
  case Element(value: String)
  case Literal(value: String)
  case Spaces(value: String)
  case OpenBrace, CloseBrace, OpenParen, CloseParen, NewLine,
    Comment, OpenBracket, CloseBracket, Comma

def tokenize(input: String): Either[WanderError, Seq[Token]] = {
  val gaze = Gaze.from(input)
  gaze.attempt(tokensNib) match {
    case Result.NoMatch =>
      if (gaze.isComplete) {
        Right(List())
      } else {
        Left(WanderError(s"Error tokenizing NoMatch, Next: ${gaze.peek()}."))
      }
    case Result.Match(res) =>
      if (gaze.isComplete) {
        Right(res)
      } else {
        Left(WanderError(s"Error tokenizing not complete, Next: ${gaze.peek()}."))
      }
    case Result.EmptyMatch => ???
  }
}

val stringTokenNib: Nibbler[String, Token] =
  LigNibblers.stringNibbler.map(results =>
    results.size match
      case 2 => Token.Literal(results(1))
      case 3 => Token.Literal(results(2))
      case _ => ???
  )

val newLineTokenNib =
  takeFirst(takeString("\n"), takeString("\r\n")).map(res => Token.NewLine)

val commentTokenNib = takeAll(
  takeString("--"),
  takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))
).map(results => Token.Comment)

// val nameValueNib: Nibbler[String, String] =
//   concat(
//     flatten(
//       takeAll(
//         seq(takeCond((c: String) => c(0).isLetter || c(0).isDigit || c == "_")),
//         optional(takeWhile((c: String) => c(0).isLetter || c(0).isDigit || c == "_"))
//       )
//     )
//   )

// /** This nibbler matches both names and keywords. After the initial match all
//   * keywords are checked and if none match and name is returned.
//   */
val nameTokenNib: Nibbler[String, Token] = wordNibbler.map { values =>
  values match {
    case value: String => Token.Element(value)
  }
}

val openBraceTokenNib =
  takeString("{").map(res => Token.OpenBrace)

val closeBraceTokenNib =
  takeString("}").map(res => Token.CloseBrace)

val openBracketTokenNib =
  takeString("[").map(res => Token.OpenBracket)

val closeBracketTokenNib =
  takeString("]").map(res => Token.CloseBracket)

val openParenTokenNib =
  takeString("(").map(res => Token.OpenParen)

val closeParenTokenNib =
  takeString(")").map(res => Token.CloseParen)

val commaTokenNib =
  takeString(",").map(res => Token.Comma)

val spacesTokenNib =
  concat(takeWhile[String](_ == " "))
    .map(res => Token.Spaces(res.mkString))

val tokensNib: Nibbler[String, Seq[Token]] = repeat(
  takeFirst(
    spacesTokenNib,
    openBraceTokenNib,
    closeBraceTokenNib,
    openBracketTokenNib,
    closeBracketTokenNib,
    stringTokenNib,
    nameTokenNib,
    commaTokenNib,
    openParenTokenNib,
    closeParenTokenNib,
    newLineTokenNib,
    commentTokenNib
  )
)

object LigNibblers {
  val whiteSpaceNibbler = takeAll(take(" "), take("\t"))
  val whiteSpaceAndNewLineNibbler = takeAll(
    takeFirst(takeString(" "), takeString("\n"), takeString("\r\n"), takeString("\t"))
  )

  val wordNibbler: Nibbler[String, String] =
    concat(takeWhile { (c: String) =>
      "[a-zA-Z0-9-._:?!=]".r.matches(c)
    })

  val stringContentNibbler: Nibbler[String, String] =
    (gaze: Gaze[String]) => {
      // Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
      val commandChars = 0x00.toChar to 0x1f.toChar
      val validHexChar: (String) => Boolean = (c: String) =>
        ('0' to '9' contains c) || ('a' to 'f' contains c) || ('A' to 'F' contains c)
      val hexNibbler: Nibbler[String, String] = concat(takeWhile(validHexChar))

      val sb = ArrayBuffer[String]()
      var fail = false
      var complete = false
      while (!complete && !fail && !gaze.isComplete) {
        val c: String = gaze.next() match
          case Some(value) => value
          case _           => ??? // should never reach
        if (commandChars.contains(c)) {
          fail = true
        } else if (c == "\"") {
          complete = true
        } else if (c == "\\") {
          gaze.next() match {
            case None => fail = true
            case Some(c) =>
              c match {
                case "\\" | "\""           => sb.append(c)
                case "n"                   => sb.append("\n")
                case "b" | "f" | "r" | "t" => ??? // sb.append("\\"); sb.append(c)
                case "u" =>
                  sb.append(c)
                  val res = gaze.attempt(hexNibbler)
                  res match {
                    case Result.NoMatch => fail = true
                    case Result.Match(res) =>
                      if (res.length == 4) {
                        sb += res
                      } else {
                        fail = true
                      }
                    case Result.EmptyMatch => ???
                  }
                case _ =>
                  fail = true
              }
          }
        } else {
          sb.append(c)
        }
      }
      if (fail) {
        Result.NoMatch
      } else {
        Result.Match(sb.mkString)
      }
    }

  val stringNibbler = takeAll(
    optional(takeString("i")),
    takeString("\""),
    stringContentNibbler
  ) // TODO should be a between but stringContentNibbler consumes the last " currently

  // private val validPrefixName =
  //   (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList.appended('_')

  // val wordIdGenNibbler = ??? //matches <{}> <prefix:{}> <{}:postfix> <pre:{}:post> etc
  // val prefixedWordNibbler = ??? //matches prefix:value:after:prefix
  // val prefixedIdGenNibbler = ??? // matches prefix:value:after:prefix:{}
}

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
  takeCond,
  takeFirst,
  takeString,
  takeUntil,
  takeWhile,
  repeat
}
import dev.ligature.gaze.Result
import dev.ligature.gaze.seq
import dev.ligature.gaze.flatten
import dev.ligature.gaze.concat
import scala.collection.mutable.ArrayBuffer
import dev.ligature.gaze.between
import dev.ligature.gaze.takeAny
import java.util.HexFormat

enum Token:
  case BooleanLiteral(value: Boolean)
  case Spaces(value: String)
  case Bytes(value: Seq[Byte])
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String, interpolated: Boolean = false)
  case Field(name: String)
  case TaggedField(name: String, tag: String)
  case OpenBrace, CloseBrace, Colon, OpenParen, CloseParen, NewLine,
    Arrow, WideArrow, Dot, At, WhenKeyword, EqualSign, Comment,
    OpenBracket, CloseBracket, QuestionMark, EndKeyword, Period,
    Backtick, Hash, Lambda, Pipe, Comma

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
      case 2 => Token.StringLiteral(results(1))
      case 3 => Token.StringLiteral(results(2), true)
      case _ => ???
  )

val newLineTokenNib =
  takeFirst(takeString("\n"), takeString("\r\n")).map(res => Token.NewLine)

val bytesTokenNib = takeAll(
  seq(takeString("0x")),
  takeWhile((c: String) => c(0).isLetter || c(0).isDigit)
).map(res =>
  val format = HexFormat.of()
  Token.Bytes(format.parseHex(res(1).mkString).toSeq)
)

val commentTokenNib = takeAll(
  takeString("--"),
  takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))
).map(results => Token.Comment)

val nameValueNib: Nibbler[String, String] =
  concat(
    flatten(
      takeAll(
        seq(takeCond((c: String) => c(0).isLetter || c == "_")),
        optional(takeWhile((c: String) => c(0).isLetter || c(0).isDigit || c == "_"))
      )
    )
  )

// /** This nibbler matches both names and keywords. After the initial match all
//   * keywords are checked and if none match and name is returned.
//   */
val nameTokenNib: Nibbler[String, Token] = nameValueNib.map { values =>
  values match {
    case "when"        => Token.WhenKeyword
    case "end"         => Token.EndKeyword
    case "true"        => Token.BooleanLiteral(true)
    case "false"       => Token.BooleanLiteral(false)
    case value: String => Token.Field(value)
  }
}

val dotNib =
  takeString(".").map(res => Token.Dot)

val atNib =
  takeString("@").map(res => Token.At)

val pipeNib =
  takeString("|").map(res => Token.Pipe)

val questionMarkNib =
  takeString("?").map(res => Token.QuestionMark)

val equalSignTokenNib =
  takeString("=").map(res => Token.EqualSign)

val colonTokenNib =
  takeString(":").map(res => Token.Colon)

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

val hashTokenNib =
  takeString("#").map(res => Token.Hash)

val backtickTokenNib =
  takeString("`").map(res => Token.Backtick)

val commaTokenNib =
  takeString(",").map(res => Token.Comma)

val arrowTokenNib =
  takeString("->").map(res => Token.Arrow)

val wideArrowTokenNib =
  takeString("=>").map(res => Token.WideArrow)

val lambdaTokenNib =
  takeString("\\").map(res => Token.Lambda)

val integerTokenNib =
  LigNibblers.numberNibbler.map(res => Token.IntegerLiteral(res.mkString.toLong))

val spacesTokenNib =
  concat(takeWhile[String](_ == " "))
    .map(res => Token.Spaces(res.mkString))

val tokensNib: Nibbler[String, Seq[Token]] = repeat(
  takeFirst(
    spacesTokenNib,
    stringTokenNib,
    nameTokenNib,
    colonTokenNib,
    commaTokenNib,
    openParenTokenNib,
    closeParenTokenNib,
    wideArrowTokenNib,
    arrowTokenNib,
    dotNib,
    atNib,
    pipeNib,
    lambdaTokenNib,
    bytesTokenNib,
    integerTokenNib,
    newLineTokenNib,
    openBraceTokenNib,
    closeBraceTokenNib,
    openBracketTokenNib,
    closeBracketTokenNib,
    backtickTokenNib,
    commentTokenNib,
    equalSignTokenNib,
    questionMarkNib,
    hashTokenNib
  )
)

object LigNibblers {
  val whiteSpaceNibbler = takeAll(take(" "), take("\t"))
  val whiteSpaceAndNewLineNibbler = takeAll(
    takeFirst(takeString(" "), takeString("\n"), takeString("\r\n"), takeString("\t"))
  )
  val numberNibbler =
    concat(
      flatten(
        takeAll(
          seq(optional(take("-"))),
          takeAny(('0' to '9').map((c: Char) => take(c.toString())).toSeq*)
        )
      )
    )

  val identifierNibbler: Nibbler[String, String] = between(
    takeString("<"),
    concat(takeWhile { (c: String) =>
      "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c)
    }),
    takeString(">")
  )

  val stringContentNibbler: Nibbler[String, String] =
    (gaze: Gaze[String]) => {
      // Full pattern \"(([^\x00-\x1F\"\\]|\\[\"\\/bfnrt]|\\u[0-9a-fA-F]{4})*)\"
      val commandChars = 0x00.toChar to 0x1f.toChar
      val validHexChar: (String) => Boolean = (c: String) =>
        ('0' to '9' contains c) || ('a' to 'f' contains c) || ('A' to 'F' contains c)
      val hexNibbler: Nibbler[String, String] = concat(takeWhile(validHexChar))

      var sb = ArrayBuffer[String]()
      var offset = 0 // TODO delete
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

  private val validPrefixName =
    (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toList.appended('_')

  // val identifierIdGenNibbler = ??? //matches <{}> <prefix:{}> <{}:postfix> <pre:{}:post> etc
  // val prefixedIdentifierNibbler = ??? //matches prefix:value:after:prefix
  // val prefixedIdGenNibbler = ??? // matches prefix:value:after:prefix:{}
}

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

enum Token:
  case BooleanLiteral(value: Boolean)
  case Spaces(value: String)
  case IntegerLiteral(value: Long)
  case StringLiteral(value: String, interpolated: Boolean = false)
  case Name(name: String)
  case TaggedName(name: String, tag: String)
  case OpenBrace, CloseBrace, Colon, OpenParen, CloseParen, NewLine,
    Arrow, WideArrow, WhenKeyword, EqualSign, Comment,
    OpenBracket, CloseBracket, NothingKeyword, QuestionMark,
    EndKeyword, Period, Backtick, Hash, Lambda, Pipe, Comma,
    ImportKeyword, ExportKeyword

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

val commentTokenNib = takeAll(
  takeString("--"),
  takeUntil(takeFirst(takeString("\n"), takeString("\r\n")))
).map(results => Token.Comment)

val nameValueNib: Nibbler[String, String] =
  concat(
    flatten(
      takeAll(
        seq(takeCond((c: String) => c(0).isLetter || c == "_")),
        optional(takeWhile((c: String) => c(0).isLetter || c(0).isDigit || c == "_" || c == "."))
      )
    )
  )

// /** This nibbler matches both names and keywords. After the initial match all
//   * keywords are checked and if none match and name is returned.
//   */
val nameTokenNib: Nibbler[String, Token] = nameValueNib.map { values =>
  values match {
    case "import"      => Token.ImportKeyword
    case "export"      => Token.ExportKeyword
    case "when"        => Token.WhenKeyword
    case "end"         => Token.EndKeyword
    case "true"        => Token.BooleanLiteral(true)
    case "false"       => Token.BooleanLiteral(false)
    case "nothing"     => Token.NothingKeyword
    case value: String => Token.Name(value)
  }
}

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
    lambdaTokenNib,
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

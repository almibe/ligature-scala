/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.preludes.common

import scalajs.js.{Dictionary, Any}
import scala.scalajs.js.annotation.*

@JSExportTopLevel("default")
object Wander {
   @JSExport
   def run(script: String): String = {
     printResult(dev.ligature.wander.run(script, common()))
   }
   @JSExport
   def introspect(script: String): Any =
     val intro = dev.ligature.wander.introspect(script)
     val res = Dictionary[Any]()
     res.put("tokens", intro.tokens.getOrElse(Seq(Token.StringLiteral("Error"))).map(token => tokenToJS(token)))
     res.put("expressions", intro.expression.getOrElse(Seq(Expression.StringValue("Error"))).map(expression => expressionToJS(expression)))
     res.put("terms", intro.terms.getOrElse(Seq(Term.StringLiteral("Error"))).map(term => termToJS(term)))
     res

   private def tokenToJS(token: Token): Dictionary[Any] =
     token match
       case Token.BooleanLiteral(value: Boolean) => Dictionary(("type", "boolean"), ("value", value.toString))
       case Token.Spaces(value: String) => Dictionary(("type", "spaces"), ("value", value))
       case Token.Identifier(value: dev.ligature.wander.Identifier) => Dictionary(("type", "identifier"), ("value", value.name))
       case Token.IntegerLiteral(value: Long) => Dictionary(("type", "int"), ("value", value.toString))
       case Token.StringLiteral(value: String) => Dictionary(("type", "string"), ("value", value))
       case Token.Name(value: String) => Dictionary(("type", "name"), ("value", value))
       case Token.OpenBrace => Dictionary(("type", "openBrace"), ("value", "{"))
       case Token.CloseBrace => Dictionary(("type", "closeBrace"), ("value", "}"))
       case Token.Colon => Dictionary(("type", "colon"), ("value", ":"))
       case Token.OpenParen => Dictionary(("type", "openParen"), ("value", "("))
       case Token.CloseParen => Dictionary(("type", "closeParen"), ("value", ")"))
       case Token.NewLine => Dictionary(("type", "newLine"), ("value", "\n")) //TODO this should probably hold original text
       case Token.Arrow => Dictionary(("type", "arrow"), ("value", "->"))
       case Token.WideArrow => Dictionary(("type", "wideArrow"), ("value", "=>"))
       case Token.WhenKeyword => Dictionary(("type", "when"), ("value", "when"))
       case Token.EqualSign => Dictionary(("type", "equalsSign"), ("value", "="))
       case Token.LetKeyword => Dictionary(("type", "let"), ("value", "let"))
       case Token.Comment => Dictionary(("type", "comment"), ("value", "--")) //TODO this should probably hold original text
       case Token.OpenBracket => Dictionary(("type", "openBracket"), ("value", "["))
       case Token.CloseBracket => Dictionary(("type", "closeBracket"), ("value", "]"))
       case Token.NothingKeyword => Dictionary(("type", "nothing"), ("value", "nothing"))
       case Token.QuestionMark => Dictionary(("type", "questionMark"), ("value", "?"))
       case Token.EndKeyword => Dictionary(("type", "end"), ("value", "end"))
       case Token.Period => Dictionary(("type", "period"), ("value", "."))
       case Token.Backtick => Dictionary(("type", "backtick"), ("value", "`"))
       case Token.Hash => Dictionary(("type", "hash"), ("value", "#"))
       case Token.Lambda => Dictionary(("type", "lambda"), ("value", "\\"))
       case Token.Pipe => Dictionary(("type", "pipe"), ("value", "|"))
       case Token.Comma => Dictionary(("type", "comma"), ("value", ","))

   private def termToJS(term: Term): Dictionary[Any] =
     term match
       case Term.NameTerm(value: Name) => Dictionary(("type", "name"), ("value", value.name))
       case Term.IdentifierLiteral(value: Identifier) => Dictionary(("type", "identifier"), ("value", value.name))
       case Term.IntegerLiteral(value: Long) => Dictionary(("type", "int"), ("value", value.toString))
       case Term.StringLiteral(value: String) => Dictionary(("type", "string"), ("value", value))
       case Term.BooleanLiteral(value: Boolean) => Dictionary(("type", "bool"), ("value", value.toString))
       case Term.NothingLiteral => Dictionary(("type", "nothing"), ("value", "nothing"))
       case Term.QuestionMark => Dictionary(("type", "questionMark"), ("value", "?"))
       case Term.Array(value: Seq[Term]) => Dictionary(("type", "array"), ("value", "[array]")) //TODO
       case Term.LetExpression(name: Name, term: Term) => Dictionary(("type", "let"), ("value", "[let]")) //TODO
       case Term.WhenExpression(conditionals: Seq[(Term, Term)]) => Dictionary(("type", "when"), ("value", "[when]")) //TODO
       case Term.Application(terms: Seq[Term]) => Dictionary(("type", "application"), ("value", "[application]")) //TODO
       case Term.Grouping(terms: Seq[Term]) => Dictionary(("type", "grouping"), ("value", "[grouping]")) //TODO
       case Term.Lambda(parameters: Seq[Name], body: Term) => Dictionary(("type", "lambda"), ("value", "[lambda]")) //TODO
       case Term.Pipe => Dictionary(("type", "pipe"), ("value", "|"))

   private def expressionToJS(expression: Expression): Dictionary[Any] =
     expression match
       case Expression.NameExpression(value) => Dictionary(("type", "name"), ("value", value.name))
       case Expression.IdentifierValue(value) => Dictionary(("type", "identifier"), ("value", value.name))
       case Expression.IntegerValue(value) => Dictionary(("type", "int"), ("value", value.toString))
       case Expression.StringValue(value) => Dictionary(("type", "string"), ("value", value))
       case Expression.BooleanValue(value) => Dictionary(("type", "bool"), ("value", value.toString))
       case Expression.Nothing => Dictionary(("type", "nothing"), ("value", "nothing"))
       case Expression.Array(value) => Dictionary(("type", "array"), ("value", "[array]")) //TODO
       case Expression.LetExpression(name, value) => Dictionary(("type", "let"), ("value", "[let]")) //TODO
       case Expression.Lambda(parameters, body) => Dictionary(("type", "lambda"), ("value", "[lambda]")) //TODO
       case Expression.WhenExpression(conditionals) => Dictionary(("type", "when"), ("value", "[when]")) //TODO
       case Expression.Application(expressions) => Dictionary(("type", "application"), ("value", "[application]")) //TODO
       case Expression.Grouping(expressions) => Dictionary(("type", "grouping"), ("value", "[grouping]")) //TODO
       case Expression.QuestionMark => Dictionary(("type", "questionMark"), ("value", "?"))
}

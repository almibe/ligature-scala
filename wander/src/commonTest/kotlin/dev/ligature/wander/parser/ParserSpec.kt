/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.Identifier
import dev.ligature.wander.lexer.tokenize
import dev.ligature.wander.model.Element
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ParserSpec: FunSpec() {
  private fun runCases(cases: Map<String, List<Element>>) {
    for((input, expected) in cases) {
      when(val tokens = tokenize(input)) {
        is Right -> {
          val res = parse(tokens.value)
          res shouldBe Right(expected)
        }
        is Left -> {
          throw Error("Could not tokenize $input")
        }
      }
    }
  }

  init {
    test("parse a boolean literal") {
      runCases(mapOf(
        "true" to listOf(Element.BooleanLiteral(true))
      ))
    }

    test("Ligature value parsing") {
      runCases(mapOf(
        "-123123" to listOf(Element.IntegerLiteral(-123123L)),
        "\"hello, world\"" to listOf(Element.StringLiteral("hello, world")),
        "<https://ligature.dev>" to listOf(Element.IdentifierLiteral(Identifier("https://ligature.dev"))),
        //TODO add this case back in
        //"0x1233" to listOf(LigatureValue(BytesLiteral(byteArrayOf(0x12.toByte(), 0x33.toByte()))))
      ))
    }

    test("white space and new lines should be ignored") {
      runCases(mapOf(
        " " to listOf(),
        "\n" to listOf(),
        "\n     \n\n    \r\n" to listOf(),
      ))
    }

    test("parsing names") {
      runCases(mapOf(
        "hello" to listOf(Element.Name("hello")),
      ))
    }

    test("parsing Sequences") {
      runCases(mapOf(
        "[]" to listOf(Element.Seq(listOf())),
        "[1]" to listOf(Element.Seq(listOf(Element.IntegerLiteral(1)))),
        "[1 nothing <hello>]" to listOf(Element.Seq(listOf(
          Element.IntegerLiteral(1),
          Element.Name("nothing"),
          Element.IdentifierLiteral(Identifier("hello"))
        )))
      ))
    }

    test("basic scope support") {
      runCases(mapOf(
        "{}" to listOf(Element.Scope(listOf())),
        "{ -123123 }" to listOf(Element.Scope(listOf(Element.IntegerLiteral(-123123L)))),
        "{ { true } }" to listOf(Element.Scope(listOf(Element.Scope(listOf(Element.BooleanLiteral(true)))))),
        "{ let x = 5 }" to listOf(Element.Scope(listOf((Element.LetStatement("x", Element.IntegerLiteral(5))))))
      ))
    }

    test("parse if expressions") {
      runCases(mapOf(
        "if {} {}" to listOf(
          Element.IfExpression(Element.Conditional(Element.Scope(listOf()), Element.Scope(listOf())))),
        "if { -123123 } true" to listOf(
          Element.IfExpression(
            Element.Conditional(
              Element.Scope(listOf(Element.IntegerLiteral(-123123))), Element.BooleanLiteral(true)))),
        "if {{ true }} false" to listOf(
          Element.IfExpression(
            Element.Conditional(
              Element.Scope(listOf(Element.Scope(listOf(Element.BooleanLiteral(true))))),
              Element.BooleanLiteral(false)))),
        "if true 5 else 6" to listOf(
          Element.IfExpression(
            Element.Conditional(
              Element.BooleanLiteral(true), Element.IntegerLiteral(5)),
            listOf(),
            Element.IntegerLiteral(6))),
        "if true false elsif false true else 7" to listOf(Element.IfExpression(
          Element.Conditional(Element.BooleanLiteral(true), Element.BooleanLiteral(false)),
            listOf(Element.Conditional(Element.BooleanLiteral(false), Element.BooleanLiteral(true))),
            Element.IntegerLiteral(7))),
        //note: the following test is a little weird since it should parse correctly but would be a runtime
        //error
        "if 1 2 elsif 2 3 elsif 3 4 else 5" to listOf(Element.IfExpression(
          Element.Conditional(Element.IntegerLiteral(1), Element.IntegerLiteral(2)),
            listOf(
              Element.Conditional(Element.IntegerLiteral(2), Element.IntegerLiteral(3)),
              Element.Conditional(Element.IntegerLiteral(3), Element.IntegerLiteral(4))
            ),
            Element.IntegerLiteral(5)))
      ))
    }

    test("function call") {
      runCases(mapOf(
        "foo()" to listOf(Element.FunctionCall("foo", listOf())),
        "foo(x)" to listOf(Element.FunctionCall("foo", listOf(Element.Name("x")))),
        "foo(5 x {})" to listOf(
          Element.FunctionCall("foo",
            listOf(Element.IntegerLiteral(5), Element.Name("x"), Element.Scope(listOf())))
        )))
    }

    test("parse let Statements") {
      runCases(mapOf(
        "let x = 5" to listOf(Element.LetStatement("x", Element.IntegerLiteral(5))),
        "let x = foo(6)" to listOf(Element.LetStatement("x",
            Element.FunctionCall("foo",
              listOf(Element.IntegerLiteral(6))))),
        "let x = { foo(\"hello\") }" to listOf(Element.LetStatement("x", Element.Scope(
            listOf(Element.FunctionCall("foo", listOf(Element.StringLiteral("hello")))))))
      ))
    }

    test("lambda definition") {
      runCases(mapOf(
        "{ -> 5 }" to listOf(Element.LambdaDefinition(listOf(), listOf(Element.IntegerLiteral(5)))),
        "{ x -> x }" to listOf(Element.LambdaDefinition(listOf("x"), listOf(Element.Name("x")))),
        "{ x -> let x = 65 x }" to listOf(Element.LambdaDefinition(listOf("x"),
          listOf(Element.LetStatement("x", Element.IntegerLiteral(65)),
            Element.Name("x")))),
        "{ x y z -> foo2(z y x) }" to listOf(
          Element.LambdaDefinition(
            listOf("x", "y", "z"),
            listOf(Element.FunctionCall("foo2", listOf(Element.Name("z"), Element.Name("y"), Element.Name("x"))))))
        ))
    }
  }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.parser

import arrow.core.Either
import arrow.core.Some
import dev.ligature.BytesLiteral
import dev.ligature.Identifier
import dev.ligature.IntegerLiteral
import dev.ligature.StringLiteral
import dev.ligature.lig.lexer.LigToken
import dev.ligature.wander.lexer.tokenize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ParserSpec: FunSpec() {
  private fun runCases(cases: Map<String, List<Element>>) {
    for((input, expected) in cases) {
      when(val tokens = tokenize(input)) {
        is Either.Right -> {
          val res = parse(tokens.value)
          res shouldBe Either.Right(Script(expected))
        }
        is Either.Left -> {
          throw Error("Could not tokenize $input")
        }
      }
    }
  }

  init {
    test("parse a boolean literal") {
      runCases(mapOf(
        "true" to listOf(BooleanValue(true))
      ))
    }

    test("Ligature value parsing") {
      runCases(mapOf(
        "-123123" to listOf(LigatureValue(IntegerLiteral(-123123L))),
        "\"hello, world\"" to listOf(LigatureValue(StringLiteral("hello, world"))),
        "<https://ligature.dev>" to listOf(LigatureValue(Identifier("https://ligature.dev"))),
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
        "hello" to listOf(Name("hello")),
      ))
    }

    test("parsing Sequences") {
      runCases(mapOf(
        "[]" to listOf(Seq()),
        "[1]" to listOf(Seq(listOf(LigatureValue(IntegerLiteral(1))))),
        "[1 nothing <hello>]" to listOf(Seq(listOf(
          LigatureValue(IntegerLiteral(1)),
          Name("nothing"),
          LigatureValue(Identifier("hello"))
        )))
      ))
    }

    test("basic scope support") {
      runCases(mapOf(
        "{}" to listOf(Scope(listOf())),
        "{ -123123 }" to listOf(Scope(listOf(LigatureValue(IntegerLiteral(-123123L))))),
        "{ { true } }" to listOf(Scope(listOf(Scope(listOf(BooleanValue(true)))))),
        "{ let x = 5 }" to listOf(Scope(listOf((LetStatement(Name("x"), LigatureValue(IntegerLiteral(5)))))))
      ))
    }

    test("parse if expressions") {
      runCases(mapOf(
        "if {} {}" to listOf(IfExpression(Scope(listOf()), Scope(listOf()))),
        "if { -123123 } true" to listOf(IfExpression(Scope(listOf(LigatureValue(IntegerLiteral(-123123)))), BooleanValue(true))),
        "if {{ true }} false" to listOf(IfExpression(Scope(listOf(Scope(listOf(BooleanValue(true))))), BooleanValue(false))),
        "if true 5 else 6" to listOf(
          IfExpression(BooleanValue(true), LigatureValue(IntegerLiteral(5)),
            listOf(),
            Else(LigatureValue(IntegerLiteral(6))))),
        "if true false elsif false true else 7" to listOf(
          IfExpression(BooleanValue(true), BooleanValue(false),
            listOf(Elsif(BooleanValue(false), BooleanValue(true))),
            Else(LigatureValue(IntegerLiteral(7))))),
        "if 1 2 elsif 2 3 elsif 3 4 else 5" to listOf(
          IfExpression(LigatureValue(IntegerLiteral(1)), LigatureValue(IntegerLiteral(2)),
            listOf(
              Elsif(LigatureValue(IntegerLiteral(2)), LigatureValue(IntegerLiteral(3))),
              Elsif(LigatureValue(IntegerLiteral(3)), LigatureValue(IntegerLiteral(4)))
            ),
            Else(LigatureValue(IntegerLiteral(5)))))
      ))
    }

    test("function call") {
      runCases(mapOf(
        "foo()" to listOf(FunctionCall(Name("foo"), listOf())),
        "foo(x)" to listOf(FunctionCall(Name("foo"), listOf(Name("x")))),
        "foo(5 x {})" to listOf(
          FunctionCall(Name("foo"),
            listOf(LigatureValue(IntegerLiteral(5)), Name("x"), Scope(listOf())))
        )))
    }

    test("parse let Statements") {
      runCases(mapOf(
        "let x = 5" to listOf(LetStatement(Name("x"), LigatureValue(IntegerLiteral(5)))),
        "let x = foo(6)" to listOf(
          LetStatement(Name("x"),
            FunctionCall(Name("foo"),
              listOf(LigatureValue(IntegerLiteral(6)))))),
        "let x = { foo(\"hello\") }" to listOf(
          LetStatement(Name("x"), Scope(
            listOf(FunctionCall(Name("foo"), listOf(LigatureValue(StringLiteral("hello"))))))))
      ))
    }

    test("function definition") {
      runCases(mapOf(
        "{ -> 5 }" to listOf(WanderFunction(listOf(), Scope(listOf(LigatureValue(IntegerLiteral(5)))))),
        "{ x -> x }" to listOf(WanderFunction(listOf(Parameter(Name("x"))), Scope(listOf(Name("x"))))),
        "{ x -> let x = 65 x }" to listOf(WanderFunction(listOf(Parameter(Name("x"))),
          Scope(listOf(LetStatement(Name("x"), LigatureValue(IntegerLiteral(65))),
            Name("x"))))),
        "{ x y z -> foo2(z y x) }" to listOf(
          WanderFunction(
            listOf(Parameter(Name("x")), Parameter(Name("y")), Parameter(Name("z"))),
            Scope(listOf(FunctionCall(Name("foo2"), listOf(Name("z"), Name("y"), Name("x")))))
        ))))
    }
  }
}

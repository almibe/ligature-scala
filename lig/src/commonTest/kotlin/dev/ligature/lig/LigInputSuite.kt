/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.lig

import io.kotest.core.spec.style.FunSpec

class LigInputSuite : FunSpec() {

  init {
    //    test("id gen") {
    //      val input = parseIdentifier(Gaze.from("<{}>"), mapOf(), none())
    //      val resultRegEx = Regex("[0-9_\\-a-fA-F]{12}")
    //      when(input) {
    //        is Either.Right -> resultRegEx.matches(input.value.name) shouldBe true
    //        is Either.Left  -> throw Error("Could not parse Identifier.")
    //      }
    //    }
    //
    //    test("id gen with prefix") {
    //      val input = parseIdentifier(Gaze.from("<this:is:a/prefix{}>"), mapOf(), none())
    //      val resultRegEx = Regex("this:is:a/prefix[0-9_\\-a-fA-F]{12}")
    //      when(input) {
    //        is Either.Right -> resultRegEx.matches(input.value.name) shouldBe true
    //        is Either.Left  -> throw Error("Could not parse Identifier.")
    //      }
    //    }
    //
    //    test("id gen in infix") {
    //      val input =
    //        parseIdentifier(Gaze.from("<this{}is:a/infix>"), mapOf(), none())
    //      val resultRegEx = Regex("this[0-9_\\-a-fA-F]{12}is:a/infix")
    //      when(input) {
    //        is Either.Right -> resultRegEx.matches(input.value.name) shouldBe true
    //        is Either.Left  -> throw Error("Could not parse Identifier.")
    //      }
    //    }
    //
    //    test("id gen in postfix") {
    //      val input =
    //        parseIdentifier(Gaze.from("<this::is:a/postfix/{}>"), mapOf(), none())
    //      val resultRegEx = Regex("this::is:a/postfix/[0-9_\\-a-fA-F]{12}")
    //      when(input) {
    //        is Either.Right -> resultRegEx.matches(input.value.name) shouldBe true
    //        is Either.Left  -> throw Error("Could not parse Identifier.")
    //      }
    //    }
    //
    //    test("basic prefix definition") {
    //      val res = parsePrefix(Gaze.from("prefix name = prefixed:identifier:"))
    //      when(res) {
    //        is Either.Right -> res shouldBe Pair("name", "prefixed:identifier:")
    //        is Either.Left  -> throw Error("Could not parse Prefix.")
    //      }
    //    }
    //
    //    test("prefixed id") {
    //      val res =
    //        parseIdentifier(
    //          Gaze.from("prefix:world"),
    //          mapOf("prefix" to "hello:"),
    //          none()
    //        )
    //      when(res) {
    //        is Either.Right -> res.value.name shouldBe "hello:world"
    //        is Either.Left  -> throw Error("Could not parse Identifier.")
    //      }
    //    }
    //
    //    test("copy character test with entity and attribute") {
    //      val input = "<e> <a> 234\n^ ^ 432"
    //      val expected = listOf(
    //        Statement(
    //          Identifier("e"),
    //          Identifier("a"),
    //          IntegerLiteral(234)
    //        ),
    //        Statement(
    //          Identifier("e"),
    //          Identifier("a"),
    //          IntegerLiteral(432)
    //        )
    //      )
    //      val res = read(input)
    //      when(res) {
    //        is Either.Right -> res.value shouldBe expected
    //        is Either.Left  -> throw Error("Error reading.") //fail("failed", clues(err))
    //      }
    //    }
    //
    //    test("error copy character test") {
    //      val input = "<this:is:an:error> <a> ^"
    //      val res = read(input)
    //      res.isLeft() shouldBe true
    //    }
    //
    //    test("copy character test with attribute and value") {
    //      val input = "<e> <a> 234\n<e2> ^ ^"
    //      val expected = listOf(
    //        Statement(
    //          Identifier("e"),
    //          Identifier("a"),
    //          IntegerLiteral(234)
    //        ),
    //        Statement(
    //          Identifier("e2"),
    //          Identifier("a"),
    //          IntegerLiteral(234)
    //        )
    //      )
    //      val result = read(input)
    //      when(result) {
    //        is Either.Right -> result.value shouldBe expected
    //        is Either.Left  -> throw Error("Could not read.") //fail("failed", clues(err))
    //      }
    //    }
    //
    //    test("prefix error test") {
    //      val input = "prefix x = this:\nx x:is:a x:prefix"
    //      val result = read(input)
    //      result.isLeft() shouldBe true
    //    }
    //
    //    test("error prefix test") {
    //      val input = "x x:is:an x:error"
    //      val result = read(input)
    //      result.isLeft() shouldBe true
    //    }
    //
    //    test("basic prefix test") {
    //      val input = "prefix x = this:\nx:hello x:cruel x:world"
    //      val result = read(input)
    //      when(result) {
    //        is Either.Right -> {
    //          result.value.size shouldBe 1
    //          val statement = result.value.first()
    //          val expected = Statement(
    //            Identifier("this:hello"),
    //            Identifier("this:cruel"),
    //            Identifier("this:world")
    //          )
    //          statement shouldBe expected
    //        }
    //        is Either.Left -> throw Error("Could not read.")//fail("failed", clues(err))
    //      }
    //    }
    //
    //    test("entity gen id prefix test") {
    //      val input = "prefix x = this:\nx:hello{} x:cruel x:world"
    //      val result = read(input)
    //      when(result) {
    //        is Either.Right -> {
    //          result.value.size shouldBe 1
    //          val statement = result.value.first()
    //          //TODO test Entity
    //  //        assertEquals(statements(0).entity,
    // Identifier.fromString("this:hello").getOrElse(???))
    //          statement.attribute shouldBe Identifier("this:cruel")
    //          statement.value shouldBe Identifier("this:world")
    //        }
    //        is Either.Left -> throw Error("Could not read.") //fail("failed", clues(err))
    //      }
    //    }
    //
    //    test("complex prefix test") {
    //      val input = "prefix x = this:\nx:{} x:{}is:a x:prefix{}"
    //      val result = read(input)
    //      when(result) {
    //        is Either.Right -> {
    //          result.value.size shouldBe 1
    //          // TODO add more checks
    //        }
    //        is Either.Left -> throw Error("Could not read.") //fail("failed", clues(err))
    //      }
    //    }
  }
}

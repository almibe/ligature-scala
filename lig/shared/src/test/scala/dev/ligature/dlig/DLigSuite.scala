/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

import munit.FunSuite
import dev.ligature.{Identifier, IntegerLiteral, Statement, StringLiteral}
import dev.ligature.gaze.Gaze
import dev.ligature.lig.CommonSuite

class DLigSuite {//extends CommonSuite(readDLig) {

  // test("copy character test with entity and attribute") {
  //   val input = "<e> <a> 234\n^^ 432"
  //   val result = Array(
  //     Statement(Identifier.fromString("e").getOrElse(???), Identifier.fromString("a").getOrElse(???), IntegerLiteral(234)),
  //     Statement(Identifier.fromString("e").getOrElse(???), Identifier.fromString("a").getOrElse(???), IntegerLiteral(432))
  //   )
  //   ???
  // }

  // test("error copy character test") {
  //   val input = "<this:is:an:error> <a> ^"
  //   ???
  // }

  // test("copy character test with attribute and value") {
  //   val input = "<e> <a> <v>\n<e2> ^^"
  //   val result = Array(
  //     Statement(Identifier.fromString("e").getOrElse(???), Identifier.fromString("a").getOrElse(???), IntegerLiteral(234)),
  //     Statement(Identifier.fromString("e2").getOrElse(???), Identifier.fromString("a").getOrElse(???), IntegerLiteral(234))
  //   )
  //   ???
  // }

  // test("prefix error test") {
  //   val input = "prefix x = this:\nx x:is:a x:prefix"
  //   ???
  // }

  // test("error prefix test") {
  //   val input = "x x:is:an x:error"
  //   ???
  // }

  // test("complex prefix test") {
  //   val input = "prefix x = this:\nx:{} x:{}is:a x:prefix{}"
  //   val result = Array(
  //     Statement(Identifier.fromString("e").getOrElse(???), Identifier.fromString("a").getOrElse(???), IntegerLiteral(234)),
  //   )
  //   ???
  // }  
}

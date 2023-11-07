// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// package dev.ligature.wander

// import dev.ligature.{Identifier, LigatureLiteral, LigatureError}
// import dev.ligature.wander.Token
// import dev.ligature.wander.ScriptResult
// import cats.effect.IO
// import dev.ligature.LigatureLiteral
// import dev.ligature.wander.preludes.common

// class LetSuite extends munit.CatsEffectSuite {
//   def check(script: String, expected: ScriptResult) =
//     assertIO(run(script, common()), expected)

//   test("basic let") {
//     val script = "let x = 5"
//     val result = WanderValue.Nothing
//     check(script, result)
//   }
//   test("make sure name parser is greedy") {
//     val script = "let trued = true trued"
//     val result = WanderValue.BooleanValue(true)
//     check(script, result)
//   }
//   test("basic scope") {
//     val script = """{
//                    |  let x = 7
//                    |  x
//                    |}""".stripMargin
//     val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(7))
//     check(script, result)
//   }
//   test("scope shadowing") {
//     val script = """let x = 5
//                    |{
//                    |  let x = 7
//                    |  x
//                    |}""".stripMargin
//     val result = WanderValue.LigatureValue(LigatureLiteral.IntegerLiteral(7))
//     check(script, result)
//   }
// }

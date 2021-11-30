/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.{
  Name,
  Nothing,
  Scope,
  Script,
  BooleanValue,
  LigatureValue,
  ScriptResult,
  ScriptError
}
import dev.ligature.wander.lexer.TokenType
import dev.ligature.{Identifier, IntegerLiteral, StringLiteral}
import dev.ligature.wander.parser.LetStatement

case class TestData(
    val category: String,
    val testInstances: List[TestInstance]
)

case class TestInstance(
    val description: String,
    val script: String,
    val tokens: List[Token],
    val ast: Script,
    val result: Either[ScriptError, ScriptResult]
)

val primitivesTestData = List(
  TestInstance(
    description = "true boolean primitive",
    script = "true",
    tokens = List(Token("true", TokenType.Boolean)),
    ast = Script(List(BooleanValue(true))),
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "false boolean primitive",
    script = "false",
    tokens = List(Token("false", TokenType.Boolean)),
    ast = Script(List(BooleanValue(false))),
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "true boolean primitive with trailing whitespace",
    script = "true   ",
    tokens =
      List(Token("true", TokenType.Boolean), Token("   ", TokenType.Spaces)),
    ast = Script(List(BooleanValue(true))),
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "identifier",
    script = "<test>",
    tokens = List(Token("test", TokenType.Identifier)),
    ast =
      Script(List(LigatureValue(Identifier.fromString("test").getOrElse(???)))),
    result = Right(
      ScriptResult(LigatureValue(Identifier.fromString("test").getOrElse(???)))
    )
  ),
  TestInstance(
    description = "integer",
    script = "24601",
    tokens = List(Token("24601", TokenType.Integer)),
    ast = Script(List(LigatureValue(IntegerLiteral(24601)))),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(24601))))
  ),
  TestInstance(
    description = "negative integer",
    script = "-111",
    tokens = List(Token("-111", TokenType.Integer)),
    ast = Script(List(LigatureValue(IntegerLiteral(-111)))),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(-111))))
  ),
  TestInstance(
    description = "comment + nothing test",
    script = "#nothing   \n",
    tokens = List(
      Token("#nothing   ", TokenType.Comment),
      Token("\n", TokenType.NewLine)
    ),
    ast = Script(List()),
    result = Right(ScriptResult(Nothing))
  ),
  // TestInstance(
  //   description = "statement",
  //   script = "<entity> <attribute> 3 <context>",
  //   tokens = List(
  //     Token("entity", TokenType.Identifier),
  //     Token(" ", TokenType.Spaces),
  //     Token("attribute", TokenType.Identifier),
  //     Token(" ", TokenType.Spaces),
  //     Token("3", TokenType.Integer),
  //     Token(" ", TokenType.Spaces),
  //     Token("context", TokenType.Identifier)
  //   ),
  //   ast = Script(
  //     List(
  //       LigatureValue(Identifier.fromString("entity").getOrElse(???)),
  //       LigatureValue(Identifier.fromString("attribute").getOrElse(???)),
  //       LigatureValue(IntegerLiteral(3)),
  //       LigatureValue(Identifier.fromString("context").getOrElse(???))
  //     )
  //   ),
  //   result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  // ),
  TestInstance(
    description = "string",
    script = "\"hello world\" ",
    tokens = List(
      Token("hello world", TokenType.String),
      Token(" ", TokenType.Spaces)
    ),
    ast = Script(List(LigatureValue(StringLiteral("hello world")))),
    result = Right(ScriptResult(LigatureValue(StringLiteral("hello world"))))
  )
)

val assignmentTestData = List(
  TestInstance(
    description = "basic let",
    script = "let x = 5",
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer)
    ),
    ast =
      Script(List(LetStatement(Name("x"), LigatureValue(IntegerLiteral(5))))),
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "make sure keyword parser is greedy",
    script = "let trued = true",
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("trued", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("true", TokenType.Boolean)
    ),
    ast = Script(List(LetStatement(Name("trued"), BooleanValue(true)))),
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "let with result",
    script = """let hello = 5
              |hello""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("hello", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer),
      Token("\n", TokenType.NewLine),
      Token("hello", TokenType.Name)
    ),
    ast = Script(
      List(
        LetStatement(Name("hello"), LigatureValue(IntegerLiteral(5))),
        Name("hello")
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  ),
  TestInstance(
    description = "basic scope",
    script = """{
               |  let x = 7
               |  x
               |}""".stripMargin,
    tokens = List(
      Token("{", TokenType.OpenBrace),
      Token("\n", TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("7", TokenType.Integer),
      Token("\n", TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token("\n", TokenType.NewLine),
      Token("}", TokenType.CloseBrace)
    ),
    ast = Script(
      List(
        Scope(
          List(
            LetStatement(Name("x"), LigatureValue(IntegerLiteral(7))),
            Name("x")
          )
        )
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  )

// "block.wander" ->
//     Script(List(
//         scope(List(
//             letStatement(identifier("x"), valueExpression(7n)),
//             referenceExpression(identifier('x'))
//         ))
//     )),

// "block-shadow.wander" ->
//     Script(List(
//         letStatement(identifier("x"), valueExpression(5n)),
//         scope(List(
//             letStatement(identifier("x"), valueExpression(7n)),
//             referenceExpression(identifier('x'))
//         ))
//     )),
)

val functionTestData = List(
  // //FUNCTIONS
  // "function0-def.wander" ->
  //     Script(List(
  //         letStatement(identifier("f"), valueExpression(functionDefinition(List(), List(valueExpression(5n))))),
  //         functionCall(identifier("f"), List())
  //     )),

  // "function1-def.wander" ->
  //     Script(List(
  //         letStatement(identifier("identity"), valueExpression(functionDefinition(List("value"), List(referenceExpression(identifier("value")))))),
  //         functionCall(identifier("identity"), List(valueExpression(new Entity("testEntity"))))
  //     )),
)

val booleanExpression = List(
  // //BOOLEAN-EXPRESSION
  // "not.wander" ->
  //     Script(List(
  //         functionCall(identifier("not"), List(valueExpression(true)))
  //     ))
)

val errorsExpression = List(
)

val testData = List(
  TestData(
    category = "Primitives",
    testInstances = primitivesTestData
  ),
  TestData(
    category = "Assignment",
    testInstances = assignmentTestData
  )
)

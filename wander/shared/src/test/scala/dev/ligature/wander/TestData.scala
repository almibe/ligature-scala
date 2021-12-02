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
  LetStatement,
  FunctionCall,
  FunctionDefinition,
  LigatureValue,
  Parameter,
  ScriptResult,
  ScriptError,
  WanderValue
}
import dev.ligature.wander.lexer.TokenType
import dev.ligature.{Identifier, IntegerLiteral, StringLiteral}

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
  ),
  TestInstance(
    description = "scope shadowing",
    script = """let x = 5
               |{
               |  let x = 7
               |  x
               |}""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("x", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer),
      Token("\n", TokenType.NewLine),
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
        LetStatement(Name("x"), LigatureValue(IntegerLiteral(5))),
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
)

val functionTestData = List(
  TestInstance(
    description = "function0 def",
    script = """let f = () -> { 5 }
               |f()""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("f", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token(" ", TokenType.Spaces),
      Token("5", TokenType.Integer),
      Token(" ", TokenType.Spaces),
      Token("}", TokenType.CloseBrace),
      Token("\n", TokenType.NewLine),
      Token("f", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("f"),
          FunctionDefinition(
            List(),
            Scope(List(LigatureValue(IntegerLiteral(5))))
          )
        ),
        FunctionCall(Name("f"), List())
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(5))))
  ),
  TestInstance(
    description = "function1 def",
    script = """let identity = (value) -> {
               |  value
               |}
               |identity(<testEntity>)""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("identity", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token("value", TokenType.Name),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token("\n", TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("value", TokenType.Name),
      Token("\n", TokenType.NewLine),
      Token("}", TokenType.CloseBrace),
      Token("\n", TokenType.NewLine),
      Token("identity", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("testEntity", TokenType.Identifier),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("identity"),
          FunctionDefinition(
            List(Parameter(Name("value"))),
            Scope(List(Name("value")))
          )
        ),
        FunctionCall(
          Name("identity"),
          List(
            LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
          )
        )
      )
    ),
    result = Right(
      ScriptResult(
        LigatureValue(Identifier.fromString("testEntity").getOrElse(???))
      )
    )
  ),
  TestInstance(
    description = "function2 def",
    script = """let second = (value1 value2) -> {
               |  value2
               |}
               |second(<testEntity> "hello")""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("second", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token("value1", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token("\n", TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token("\n", TokenType.NewLine),
      Token("}", TokenType.CloseBrace),
      Token("\n", TokenType.NewLine),
      Token("second", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("testEntity", TokenType.Identifier),
      Token(" ", TokenType.Spaces),
      Token("hello", TokenType.String),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("second"),
          FunctionDefinition(
            List(Parameter(Name("value1")), Parameter(Name("value2"))),
            Scope(List(Name("value2")))
          )
        ),
        FunctionCall(
          Name("second"),
          List(
            LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
            LigatureValue(StringLiteral("hello"))
          )
        )
      )
    ),
    result = Right(
      ScriptResult(
        LigatureValue(StringLiteral("hello"))
      )
    )
  ),
  TestInstance(
    description = "function3 def",
    script = """let middle = (value1 value2 value3) -> {
               |  value2
               |}
               |middle(<testEntity> "hello" 24601)""".stripMargin,
    tokens = List(
      Token("let", TokenType.LetKeyword),
      Token(" ", TokenType.Spaces),
      Token("middle", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("=", TokenType.EqualSign),
      Token(" ", TokenType.Spaces),
      Token("(", TokenType.OpenParen),
      Token("value1", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token(" ", TokenType.Spaces),
      Token("value3", TokenType.Name),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("->", TokenType.Arrow),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token("\n", TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("value2", TokenType.Name),
      Token("\n", TokenType.NewLine),
      Token("}", TokenType.CloseBrace),
      Token("\n", TokenType.NewLine),
      Token("middle", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("testEntity", TokenType.Identifier),
      Token(" ", TokenType.Spaces),
      Token("hello", TokenType.String),
      Token(" ", TokenType.Spaces),
      Token("24601", TokenType.Integer),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        LetStatement(
          Name("middle"),
          FunctionDefinition(
            List(
              Parameter(Name("value1")),
              Parameter(Name("value2")),
              Parameter(Name("value3"))
            ),
            Scope(List(Name("value2")))
          )
        ),
        FunctionCall(
          Name("middle"),
          List(
            LigatureValue(Identifier.fromString("testEntity").getOrElse(???)),
            LigatureValue(StringLiteral("hello")),
            LigatureValue(IntegerLiteral(24601))
          )
        )
      )
    ),
    result = Right(
      ScriptResult(
        LigatureValue(StringLiteral("hello"))
      )
    )
  )
)

val booleanExpression = List(
  // //BOOLEAN-EXPRESSION
  // "not.wander" ->
  //     Script(List(
  //         functionCall(identifier("not"), List(valueExpression(true)))
  //     ))
)

val ifExpression = List()

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
  ),
  TestData(
    category = "Functions",
    testInstances = functionTestData
  )
  // TODO add boolean functions
  // TODO add if expressions
  // TODO add error cases
)

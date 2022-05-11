/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.lexer.Token
import dev.ligature.wander.parser.{BooleanValue, Else, ElseIf, FunctionCall, IfExpression, LetStatement, LigatureValue, Name, Nothing, Parameter, Scope, Script, ScriptError, ScriptResult, WanderFunction, WanderValue}
import dev.ligature.wander.lexer.TokenType
import dev.ligature.{Dataset, Identifier, IntegerLiteral, StringLiteral}

case class TestData(
    category: String,
    dataset: Dataset,
    testInstances: List[TestInstance]
)

case class TestInstance(
    description: String,
    script: String,
    tokens: List[Token],
    ast: Script,
    result: Either[ScriptError, ScriptResult]
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
          WanderFunction(
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
          WanderFunction(
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
          WanderFunction(
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
          WanderFunction(
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
  TestInstance(
    description = "not function",
    script = "not(true)",
    tokens = List(
      Token("not", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("true", TokenType.Boolean),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        FunctionCall(Name("not"), List(BooleanValue(true)))
      )
    ),
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "boolean1 test",
    script = "or(true and(false false))",
    tokens = List(
      Token("or", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("true", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("and", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("false", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("false", TokenType.Boolean),
      Token(")", TokenType.CloseParen),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        FunctionCall(
          Name("or"),
          List(
            BooleanValue(true),
            FunctionCall(
              Name("and"),
              List(BooleanValue(false), BooleanValue(false))
            )
          )
        )
      )
    ),
    result = Right(ScriptResult(BooleanValue(true)))
  ),
  TestInstance(
    description = "boolean2 test",
    script = "and(or(true false) false)",
    tokens = List(
      Token("and", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("or", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("true", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("false", TokenType.Boolean),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("false", TokenType.Boolean),
      Token(")", TokenType.CloseParen)
    ),
    ast = Script(
      List(
        FunctionCall(
          Name("and"),
          List(
            FunctionCall(
              Name("or"),
              List(BooleanValue(true), BooleanValue(false))
            ),
            BooleanValue(false)
          )
        )
      )
    ),
    result = Right(ScriptResult(BooleanValue(false)))
  ),
  TestInstance(
    description = "boolean3 test with variables",
    script = """let t = not(or(false false))
               |let f = false
               |let res = or(t and(f false))
               |res""".stripMargin,
    tokens = null,
    ast = null,
    result = Right(ScriptResult(BooleanValue(true)))
  )
)

val ifExpression = List(
  TestInstance(
    description = "if true",
    script = """if true {
               |  7
               |}""".stripMargin,
    tokens = List(
      Token("if", TokenType.IfKeyword),
      Token(" ", TokenType.Spaces),
      Token("true", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token("\n", TokenType.NewLine),
      Token("  ", TokenType.Spaces),
      Token("7", TokenType.Integer),
      Token("\n", TokenType.NewLine),
      Token("}", TokenType.CloseBrace)
    ),
    ast = Script(
      List(
        IfExpression(
          BooleanValue(true),
          Scope(List(LigatureValue(IntegerLiteral(7))))
        )
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(7))))
  ),
  TestInstance(
    description = "if false",
    script = "if and(false true) { 24601 }",
    tokens = List(
      Token("if", TokenType.IfKeyword),
      Token(" ", TokenType.Spaces),
      Token("and", TokenType.Name),
      Token("(", TokenType.OpenParen),
      Token("false", TokenType.Boolean),
      Token(" ", TokenType.Spaces),
      Token("true", TokenType.Boolean),
      Token(")", TokenType.CloseParen),
      Token(" ", TokenType.Spaces),
      Token("{", TokenType.OpenBrace),
      Token(" ", TokenType.Spaces),
      Token("24601", TokenType.Integer),
      Token(" ", TokenType.Spaces),
      Token("}", TokenType.CloseBrace)
    ),
    ast = Script(
      List(
        IfExpression(
          FunctionCall(
            Name("and"),
            List(BooleanValue(false), BooleanValue(true))
          ),
          Scope(List(LigatureValue(IntegerLiteral(24601))))
        )
      )
    ),
    result = Right(ScriptResult(Nothing))
  ),
  TestInstance(
    description = "if else",
    script = """let x = true
               |let y = false
               |if y {
               |    1
               |} else if x {
               |    2
               |} else if false {
               |    3    
               |} else {
               |    4
               |}""".stripMargin,
    tokens = null,
    ast = Script(
      List(
        LetStatement(Name("x"), BooleanValue(true)),
        LetStatement(Name("y"), BooleanValue(false)),
        IfExpression(
          Name("y"),
          Scope(List(LigatureValue(IntegerLiteral(1)))),
          List(
            ElseIf(Name("x"), Scope(List(LigatureValue(IntegerLiteral(2))))),
            ElseIf(
              BooleanValue(false),
              Scope(List(LigatureValue(IntegerLiteral(3))))
            )
          ),
          Some(Else(Scope(List(LigatureValue(IntegerLiteral(4))))))
        )
      )
    ),
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(2))))
  ),
  TestInstance(
    description = "else",
    script = """let x = true
               |let y = false
               |if y {
               |    1
               |} else if not(x) {
               |    2
               |} else {
               |    3
               |}""".stripMargin,
    tokens = null,
    ast = null,
    result = Right(ScriptResult(LigatureValue(IntegerLiteral(3))))
  )
)

val errorsExpression = List()

val testData = List(
  TestData(
    category = "Primitives",
    dataset = Dataset.fromString("test").getOrElse(???),
    testInstances = primitivesTestData
  ),
  TestData(
    category = "Assignment",
    dataset = Dataset.fromString("test").getOrElse(???),
    testInstances = assignmentTestData
  ),
  TestData(
    category = "Functions",
    dataset = Dataset.fromString("test").getOrElse(???),
    testInstances = functionTestData
  ),
  TestData(
    category = "Boolean Functions",
    dataset = Dataset.fromString("test").getOrElse(???),
    testInstances = booleanExpression
  ),
  TestData(
    category = "If Expressions",
    dataset = Dataset.fromString("test").getOrElse(???),
    testInstances = ifExpression
  )
  // TODO add error cases
)

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.tail
import dev.ligature.IntegerLiteral
import dev.ligature.Statement
import dev.ligature.StringLiteral
import dev.ligature.wander.interpreter.*
import dev.ligature.wander.model.Element
import dev.ligature.wander.model.Parameter
import dev.ligature.wander.model.WanderType
import dev.ligature.wander.model.write

interface Logger {
  fun log(message: String): Element.Nothing
}

/** Create a Bindings instance with functions */
fun common(
    logger: Logger =
        object : Logger {
          override fun log(message: String): Element.Nothing {
            println(message)
            return Element.Nothing
          }
        }
): Bindings {
  val stdLib = Bindings()

  stdLib.bindVariable(
      "log",
      Element.NativeFunction(listOf(Parameter("message", WanderType.Any))) { arguments, bindings ->
        if (arguments.size == 1) {
          val message = arguments[0]
          val output = write(message)
          logger.log(output)
          Right(Element.Nothing)
        } else {
          TODO()
        }
      })

  stdLib.bindVariable(
      "eq",
      Element.NativeFunction(
          listOf(Parameter("left", WanderType.Any), Parameter("right", WanderType.Any))) {
              arguments,
              bindings ->
            if (arguments.size == 2) {
              val left = arguments[0]
              val right = arguments[1]
              Right(Element.BooleanLiteral(left == right))
            } else {
              Left(EvalError("eq function requires 2 arguments."))
            }
          })

  stdLib.bindVariable(
      "take",
      Element.NativeFunction(
          listOf(
              Parameter("seq", WanderType.Seq(WanderType.Any)),
              Parameter("number", WanderType.Integer))) { arguments, bindings ->
            if (arguments.size == 2) {
              val seq = arguments[0] as Element.Seq
              val number = arguments[1] as Element.IntegerLiteral
              Right(Element.Seq(seq.values.take(number.value.toInt())))
            } else {
              Left(EvalError("take requires 2 arguments"))
            }
          })

  stdLib.bindVariable(
      "entity",
      Element.NativeFunction(listOf(Parameter("statement", WanderType.Seq(WanderType.Any)))) {
          arguments,
          bindings ->
        if (arguments.size == 1) {
          val seq = arguments[0] as Element.Seq
          Right(seq.values[0] as Element.Value)
        } else {
          Left(EvalError("entity requires 1 argument"))
        }
      })

  stdLib.bindVariable(
      "attribute",
      Element.NativeFunction(listOf(Parameter("statement", WanderType.Seq(WanderType.Any)))) {
          arguments,
          bindings ->
        if (arguments.size == 1) {
          val seq = arguments[0] as Element.Seq
          Right(seq.values[1] as Element.Value)
        } else {
          Left(EvalError("attribute requires 1 argument"))
        }
      })

  stdLib.bindVariable(
      "value",
      Element.NativeFunction(listOf(Parameter("statement", WanderType.Seq(WanderType.Any)))) {
          arguments,
          bindings ->
        if (arguments.size == 1) {
          val seq = arguments[0] as Element.Seq
          Right(seq.values[2] as Element.Value)
        } else {
          Left(EvalError("value requires 1 argument"))
        }
      })

  stdLib.bindVariable(
      "get",
      Element.NativeFunction(
          listOf(
              Parameter("seq", WanderType.Seq(WanderType.Any)),
              Parameter("index", WanderType.Integer))) { arguments, bindings ->
            if (arguments.size == 2) {
              val seq = arguments[0] as Element.Seq
              val index = arguments[1] as Element.IntegerLiteral
              Right(seq.values[index.value.toInt()] as Element.Value)
            } else {
              Left(EvalError("value requires 1 argument"))
            }
          })

  stdLib.bindVariable(
      "count",
      Element.NativeFunction(listOf(Parameter("seq", WanderType.Seq(WanderType.Any)))) {
          arguments,
          bindings ->
        if (arguments.size == 1) {
          val seq = arguments[0] as Element.Seq
          Right(Element.IntegerLiteral(seq.values.size.toLong()))
        } else {
          Left(EvalError("count requires 1 argument"))
        }
      })

  stdLib.bindVariable(
      "ensure",
      Element.NativeFunction(listOf(Parameter("arg", WanderType.Boolean))) { arguments, bindings ->
        if (arguments.size == 1) {
          val arg = arguments[0]
          if (arg is Element.BooleanLiteral && arg.value) {
            Right(Element.Nothing)
          } else {
            Left(EvalError("Ensure failed."))
          }
        } else {
          Left(EvalError("Ensure requires a single argument."))
        }
      })

  stdLib.bindVariable(
      "not",
      Element.NativeFunction(listOf(Parameter("bool", WanderType.Boolean))) {
          arguments,
          bindings: Bindings ->
        val bool = arguments[0]
        if (bool is Element.BooleanLiteral) {
          Right(Element.BooleanLiteral(!bool.value))
        } else {
          TODO()
        }
      })

  stdLib.bindVariable(
      "and",
      Element.NativeFunction(
          listOf(
              Parameter("boolLeft", WanderType.Boolean),
              Parameter("boolRight", WanderType.Boolean))) { arguments, bindings: Bindings ->
            val left = arguments[0]
            val right = arguments[1]
            if (left is Element.BooleanLiteral && right is Element.BooleanLiteral) {
              Right(Element.BooleanLiteral(left.value && right.value))
            } else {
              Left(EvalError("Function `and` requires two booleans"))
            }
          })

  stdLib.bindVariable(
      "or",
      Element.NativeFunction(
          listOf(
              Parameter("boolLeft", WanderType.Boolean),
              Parameter("boolRight", WanderType.Boolean))) { arguments, bindings: Bindings ->
            val left = arguments[0]
            val right = arguments[1]
            if (left is Element.BooleanLiteral && right is Element.BooleanLiteral) {
              Right(Element.BooleanLiteral(left.value || right.value))
            } else {
              Left(EvalError("Function `or` requires two booleans"))
            }
          })

  //  stdLib.bindVariable(
  //    Name("range"),
  //    NativeFunction(
  //      listOf(Parameter(Name("start")), Parameter(Name("stop")))) { bindings: Bindings ->
  //        //TODO for now just return 0 to 9, eventually read start and stop params
  //        TODO()
  //      }
  //    )
  //
  stdLib.bindVariable(
      "each",
      Element.NativeFunction(
          listOf(
              Parameter("seq", WanderType.Seq(WanderType.Any)),
              Parameter("fn", WanderType.Function))) { arguments, bindings ->
            // TODO check arguments length first
            val seq = arguments[0]
            val fn = arguments[1]
            if (seq is Element.Seq && fn is Element.Function) {
              if (fn.parameters.size == 1) {
                seq.values.forEach {
                  when (val r = eval(it, bindings)) {
                    is Right -> {
                      val arg = r.value as Element.Value
                      val evalRes = fn.call(listOf(arg), bindings)
                      if (evalRes is Left) {
                        return@NativeFunction evalRes
                      }
                    }
                    is Left -> return@NativeFunction r
                  }
                }
                Right(Element.Nothing)
              } else {
                Left(EvalError("Second argument to function `each` require a single parameter."))
              }
            } else {
              Left(EvalError("Function `each` requires two arguments."))
            }
          })

  val flatten =
      Element.NativeFunction(
          listOf(Parameter("seq", WanderType.Seq(WanderType.Seq(WanderType.Any))))) {
              arguments,
              bindings ->
            if (arguments.size == 1) {
              val seq = arguments[0] as Element.Seq
              val result = mutableListOf<Element.Expression>()
              seq.values.forEach {
                it as Element.Seq
                result.addAll(it.values)
              }
              Right(Element.Seq(result))
            } else {
              Left(EvalError("flatten requires 1 parameter"))
            }
          }

  stdLib.bindVariable("flatten", flatten)

  stdLib.bindVariable(
      "first",
      Element.NativeFunction(
          listOf(Parameter("seq", WanderType.Seq(WanderType.Seq(WanderType.Any))))) {
              arguments,
              bindings ->
            if (arguments.size == 1) {
              val seq = arguments[0] as Element.Seq
              Right(seq.values.first() as Element.Value)
            } else {
              Left(EvalError("first requires 1 parameter"))
            }
          })

  stdLib.bindVariable(
      "rest",
      Element.NativeFunction(
          listOf(Parameter("seq", WanderType.Seq(WanderType.Seq(WanderType.Any))))) {
              arguments,
              bindings ->
            if (arguments.size == 1) {
              val seq = arguments[0] as Element.Seq
              Right(Element.Seq(seq.values.tail()))
            } else {
              Left(EvalError("first requires 1 parameter"))
            }
          })

  val map =
      Element.NativeFunction(
          listOf(
              Parameter("seq", WanderType.Seq(WanderType.Any)),
              Parameter("fn", WanderType.Function))) { arguments, bindings ->
            // TODO check arguments length first
            val seq = arguments[0] // bindings.read("seq", Element.Seq::class)
            val fn = arguments[1] // bindings.read("fn", Element.Function::class)
            if (seq is Element.Seq && fn is Element.Function) {
              if (fn.parameters.size == 1) {
                val argName = fn.parameters.first()
                val res = mutableListOf<Element.Expression>()
                seq.values.forEach {
                  // TODO bind `it` to the name saved above
                  when (val r = eval(it, bindings)) {
                    is Right -> {
                      val arg = r.value as Element.Value
                      when (val evalRes = fn.call(listOf(arg), bindings)) {
                        // TODO fix below, I think I need a way to convert from Value to Element
                        is Right -> res.add(evalRes.value as Element.Expression)
                        is Left -> return@NativeFunction evalRes
                      }
                    }
                    is Left -> return@NativeFunction r
                  }
                }
                Right(Element.Seq(res))
              } else {
                TODO("report error, incorrect parameters")
              }
            } else {
              Left(EvalError("Could not map."))
            }
          }

  stdLib.bindVariable("map", map)

  stdLib.bindVariable(
      "flatmap",
      Element.NativeFunction(
          listOf(
              Parameter("seq", WanderType.Seq(WanderType.Any)),
              Parameter("fn", WanderType.Function))) { arguments, bindings ->
            if (arguments.size == 2) {
              when (val mapResult = map.body(arguments, bindings)) {
                is Left -> return@NativeFunction mapResult
                is Right -> {
                  flatten.body(listOf(mapResult.value), bindings)
                }
              }
            } else {
              Left(EvalError("flatmap requires two parameters."))
            }
          })

  stdLib.bindVariable(
      "fold",
      Element.NativeFunction(
          listOf(
              Parameter("seq", WanderType.Seq(WanderType.Any)),
              Parameter("initial", WanderType.Any),
              Parameter("fn", WanderType.Function))) { arguments, bindings ->
            if (arguments.size == 3) {
              TODO()
            } else {
              Left(EvalError("fold requires three parameters."))
            }
          })
  // TODO flatMap function --

  stdLib.bindVariable(
      "filter",
      Element.NativeFunction(
          listOf(
              Parameter("seq", WanderType.Seq(WanderType.Any)),
              Parameter("fn", WanderType.Function))) { arguments, bindings ->
            // TODO check arguments length first
            val seq = arguments[0] // bindings.read("seq", Element.Seq::class)
            val fn = arguments[1] // bindings.read("fn", Element.Function::class)
            if (seq is Element.Seq && fn is Element.Function) {
              if (fn.parameters.size == 1) {
                val argName = fn.parameters.first()
                val res = mutableListOf<Element.Expression>()
                seq.values.forEach {
                  // TODO bind `it` to the name saved above
                  when (val r = eval(it, bindings)) {
                    is Right -> {
                      val arg = r.value as Element.Value
                      when (val evalRes = fn.call(listOf(arg), bindings)) {
                        is Right -> {
                          val value = evalRes.value
                          if (value is Element.BooleanLiteral && value.value) res.add(it)
                        }
                        is Left -> return@NativeFunction evalRes
                      }
                    }
                    is Left -> return@NativeFunction r
                  }
                }
                Right(Element.Seq(res))
              } else {
                TODO("report error, incorrect parameters")
              }
            } else {
              Left(EvalError("Could not filter."))
            }
          })

  fun seqToStatement(seq: Element.Seq): Either<EvalError, Statement> {
    if (seq.values.size == 3) {
      val e = seq.values[0] as Element.IdentifierLiteral
      val a = seq.values[1] as Element.IdentifierLiteral
      val v =
          when (val v = seq.values[2] as Element.Value) {
            is Element.BooleanLiteral ->
                return Left(EvalError("Booleans are not a valid Ligature Values."))
            is Element.Graph -> return Left(EvalError("Graphs are not a valid Ligature Values."))
            is Element.IdentifierLiteral -> v.value
            is Element.IntegerLiteral -> IntegerLiteral(v.value)
            Element.Nothing -> return Left(EvalError("Nothing is not a valid Ligature Value."))
            is Element.Seq -> return Left(EvalError("Seqs are not a valid Ligature Values."))
            is Element.StringLiteral -> StringLiteral(v.value)
            is Element.LambdaDefinition ->
                return Left(EvalError("Functions are not valid Ligature Value."))
            is Element.NativeFunction ->
                return Left(EvalError("Functions are not valid Ligature Value."))
          }
      return Right(Statement(e.value, a.value, v))
    } else {
      return Left(EvalError("${write(seq)} is not a valid statement."))
    }
  }

  stdLib.bindVariable(
      "graph",
      Element.NativeFunction(listOf()) { arguments, bindings ->
        if (arguments.isEmpty()) {
          Right(Element.Graph())
        } else if (arguments.size == 1 && arguments.first() is Element.Seq) {
          val seq = arguments.first() as Element.Seq
          val statements: List<Statement> =
              seq.values.map {
                if (it is Element.Seq) {
                  when (val statement = seqToStatement(it)) {
                    is Right -> statement.value
                    is Left -> TODO()
                  }
                } else {
                  // eval the expression and check if it's a Seq
                  TODO()
                }
              }
          Right(Element.Graph(statements.toMutableSet()))
        } else {
          Left(EvalError("graph function takes 0 or 1 arguments."))
        }
      })

  stdLib.bindVariable(
      "add",
      Element.NativeFunction(
          listOf(
              Parameter("graph", WanderType.Graph),
              Parameter("statement", WanderType.Seq(WanderType.Any)))) { arguments, bindings ->
            // TODO check arguments length first
            val graph = arguments[0] // bindings.read("graph", Element.Graph::class)
            val statementSeq = arguments[1] // bindings.read("statement", Element.Seq::class)
            if (graph is Element.Graph && statementSeq is Element.Seq) {
              when (val statement = seqToStatement(statementSeq)) {
                is Right -> {
                  graph.statements.add(statement.value)
                  Right(graph)
                }
                is Left -> {
                  statement
                }
              }
            } else {
              Left(EvalError("add requires a graph and a statement"))
            }
          })

  stdLib.bindVariable(
      "sum",
      Element.NativeFunction(
          listOf(Parameter("left", WanderType.Integer), Parameter("right", WanderType.Integer))) {
              arguments,
              bindings ->
            if (arguments.size == 2) {
              val left = arguments[0] as Element.IntegerLiteral
              val right = arguments[1] as Element.IntegerLiteral
              Right(Element.IntegerLiteral(left.value + right.value))
            } else {
              TODO()
            }
          })

  return stdLib
}

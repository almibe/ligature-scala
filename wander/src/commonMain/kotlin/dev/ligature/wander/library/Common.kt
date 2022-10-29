/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.IntegerLiteral
import dev.ligature.Statement
import dev.ligature.StringLiteral
import dev.ligature.wander.interpreter.*
import dev.ligature.wander.model.Element
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
      Element.NativeFunction(listOf("message")) { arguments, bindings ->
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
      "ensure",
      Element.NativeFunction(listOf("arg")) { arguments, bindings ->
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
      Element.NativeFunction(listOf("bool")) { arguments, bindings: Bindings ->
        val bool = arguments[0]
        if (bool is Element.BooleanLiteral) {
          Right(Element.BooleanLiteral(!bool.value))
        } else {
          TODO()
        }
      })

  stdLib.bindVariable(
      "and",
      Element.NativeFunction(listOf("boolLeft", "boolRight")) { arguments, bindings: Bindings ->
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
      Element.NativeFunction(listOf("boolLeft", "boolRight")) { arguments, bindings: Bindings ->
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
      Element.NativeFunction(listOf("seq", "fn")) { arguments, bindings ->
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

  // TODO count function
  // TODO filter function
  // TODO reduce function
  // TODO first function
  // TODO rest function

  stdLib.bindVariable(
      "filter",
      Element.NativeFunction(listOf("seq", "fn")) { arguments, bindings ->
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

  stdLib.bindVariable(
      "map",
      Element.NativeFunction(listOf("seq", "fn")) { arguments, bindings ->
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
      Element.NativeFunction(listOf("graph", "statement")) { arguments, bindings ->
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

  return stdLib
}

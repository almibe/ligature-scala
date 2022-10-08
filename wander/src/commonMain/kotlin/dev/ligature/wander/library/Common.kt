/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.wander.interpreter.*

interface Logger {
  fun log(message: String): Value.Nothing
}

/**
 * Create a Bindings instance with functions
 */
fun common(logger: Logger = object : Logger {
  override fun log(message: String): Value.Nothing {
    println(message)
    return Value.Nothing
  }
}): Bindings {
  val stdLib = Bindings()

  stdLib
    .bindVariable(
      "log",
      Value.NativeFunction(
        listOf("message")
      ) { bindings ->
        when (val message = bindings.read("message", Value::class)) {
          is Right -> {
            val output = write(message.value)
            logger.log(output)
            Right(Value.Nothing)
          }
          is Left -> TODO()
        }
      }
    )

//  stdLib
//    .bindVariable(
//      "ensure",
//      Value.NativeFunction(
//        listOf("arg")) { bindings ->
//          when (val arg = bindings.read("arg", Value.BooleanLiteral)) {
//            is Right -> {
//              val value = arg.value
//              if (value is BooleanValue) {
//                if (value.value) {
//                  Right(Nothing)
//                } else {
//                  TODO()
//                }
//              } else {
//                TODO()
//              }
//            }
//            is Left -> TODO()
//          }
//      })

  stdLib
    .bindVariable(
      "not",
      Value.NativeFunction(
        listOf("bool"))
      { bindings: Bindings ->
        val bool = bindings.read("bool", Value.BooleanLiteral::class)
        if (bool is Right) {
          val value = bool.value
          Right(Value.BooleanLiteral(!value.value))
        } else {
          TODO()
        }
      }
    )

  stdLib
    .bindVariable(
      "and",
      Value.NativeFunction(
        listOf(
          "boolLeft",
          "boolRight"
        )) { bindings: Bindings ->
        val left = bindings.read("boolLeft", Value.BooleanLiteral::class)
        val right = bindings.read("boolRight", Value.BooleanLiteral::class)
        if (left is Right && right is Right) {
          val l = left.value
          val r = right.value
          Right(Value.BooleanLiteral(l.value && r.value))
        } else {
          Left(EvalError("Function `and` requires two booleans"))
        }
      }
    )

  stdLib.bindVariable(
    "or",
    Value.NativeFunction(
      listOf(
        "boolLeft",
        "boolRight"
      )) { bindings: Bindings ->
      val left = bindings.read("boolLeft", Value.BooleanLiteral::class)
      val right = bindings.read("boolRight", Value.BooleanLiteral::class)
      if (left is Right && right is Right) {
        val l = left.value
        val r = right.value
        Right(Value.BooleanLiteral(l.value || r.value))
      } else {
        Left(EvalError("Function `or` requires two booleans"))
      }
    }
  )

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
    Value.NativeFunction(
      listOf("seq", "fn")) { bindings ->
      val seq = bindings.read("seq", Value.Seq::class)
      val fn = bindings.read("fn", Value.Function::class)
      if (seq is Right && fn is Right) {
        val s = seq.value
        val f = fn.value
        if (f.parameters.size == 1) {
          val argName = f.parameters.first()
          s.values.forEach {
            when (val r = eval(it, bindings)) {
              is Right -> {
                bindings.addScope()
                bindings.bindVariable(argName, r.value)
                val evalRes = f.call(bindings)
                bindings.removeScope()
                if (evalRes is Left) {
                  return@NativeFunction evalRes
                }
              }
              is Left -> return@NativeFunction r
            }
          }
          Right(Value.Nothing)
        } else {
          Left(EvalError("Second argument to function `each` require a single parameter."))
        }
      } else {
        Left(EvalError("Function `each` requires two arguments."))
      }
    }
  )

//  //TODO count function
//
//  stdLib.bindVariable(
//    Name("map"),
//    NativeFunction(
//      listOf(Parameter(Name("seq")), Parameter(Name("fn")))) { bindings ->
//        val seq = bindings.read(Name("seq"))
//        val fn = bindings.read(Name("fn"))
//        if (seq is Right<*> && fn is Right<*>) {
//          val s = seq.value as Seq
//          val f = fn.value as FunctionDefinition
//          if (f.parameters.size == 1) {
//            val argName = f.parameters.first().name
//            val res = mutableListOf<Expression>()
//            s.contents.forEach {
//              //TODO bind `it` to the name saved above
//              val r = it.eval(bindings)
//              when (r) {
//                is Right -> {
//                  bindings.addScope()
//                  bindings.bindVariable(argName, r.value.result)
//                  val evalRes = f.eval(bindings)
//                  bindings.removeScope()
//                  when (evalRes) {
//                    is Right -> res.add(evalRes.value.result)
//                    is Left -> return@NativeFunction evalRes
//                  }
//                }
//                is Left -> return@NativeFunction r
//              }
//            }
//            Right(Seq(res))
//          } else {
//            TODO("report error, incorrect parameters")
//          }
//        } else {
//          Left (ScriptError("Could not map."))
//        }
//      }
//  )
  return stdLib
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.wander.interpreter.*
import dev.ligature.wander.model.Element
import dev.ligature.wander.model.write

interface Logger {
  fun log(message: String): Element.Nothing
}

/**
 * Create a Bindings instance with functions
 */
fun common(logger: Logger = object : Logger {
  override fun log(message: String): Element.Nothing {
    println(message)
    return Element.Nothing
  }
}): Bindings {
  val stdLib = Bindings()

  stdLib
    .bindVariable(
      "log",
      Element.NativeFunction(
        listOf("message")
      ) { bindings ->
        when (val message = bindings.read("message", Element::class)) {
          is Right -> {
            val output = write(message.value)
            logger.log(output)
            Right(Element.Nothing)
          }
          is Left -> TODO()
        }
      }
    )

  stdLib
    .bindVariable(
      "ensure",
      Element.NativeFunction(
        listOf("arg")) { bindings ->
          when (val arg = bindings.read("arg", Element.BooleanLiteral::class)) {
            is Right -> {
              val value = arg.value
                if (value.value) {
                  Right(Element.Nothing)
                } else {
                  Left(EvalError("Ensure failed."))
                }
            }
            is Left -> Left(EvalError("Ensure failed."))
          }
      })

  stdLib
    .bindVariable(
      "not",
      Element.NativeFunction(
        listOf("bool"))
      { bindings: Bindings ->
        val bool = bindings.read("bool", Element.BooleanLiteral::class)
        if (bool is Right) {
          val value = bool.value
          Right(Element.BooleanLiteral(!value.value))
        } else {
          TODO()
        }
      }
    )

  stdLib
    .bindVariable(
      "and",
      Element.NativeFunction(
        listOf(
          "boolLeft",
          "boolRight"
        )) { bindings: Bindings ->
        val left = bindings.read("boolLeft", Element.BooleanLiteral::class)
        val right = bindings.read("boolRight", Element.BooleanLiteral::class)
        if (left is Right && right is Right) {
          val l = left.value
          val r = right.value
          Right(Element.BooleanLiteral(l.value && r.value))
        } else {
          Left(EvalError("Function `and` requires two booleans"))
        }
      }
    )

  stdLib.bindVariable(
    "or",
    Element.NativeFunction(
      listOf(
        "boolLeft",
        "boolRight"
      )) { bindings: Bindings ->
      val left = bindings.read("boolLeft", Element.BooleanLiteral::class)
      val right = bindings.read("boolRight", Element.BooleanLiteral::class)
      if (left is Right && right is Right) {
        val l = left.value
        val r = right.value
        Right(Element.BooleanLiteral(l.value || r.value))
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
    Element.NativeFunction(
      listOf("seq", "fn")) { bindings ->
      val seq = bindings.read("seq", Element.Seq::class)
      val fn = bindings.read("fn", Element.Function::class)
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
          Right(Element.Nothing)
        } else {
          Left(EvalError("Second argument to function `each` require a single parameter."))
        }
      } else {
        Left(EvalError("Function `each` requires two arguments."))
      }
    }
  )

//TODO count function
//TODO filter function
//TODO reduce function
//TODO first function
//TODO rest function

  stdLib.bindVariable(
    "map",
    Element.NativeFunction(
      listOf("seq", "fn")) { bindings ->
        val seq = bindings.read("seq", Element.Seq::class)
        val fn = bindings.read("fn", Element.Function::class)
        if (seq is Right && fn is Right) {
          val s = seq.value
          val f = fn.value
          if (f.parameters.size == 1) {
            val argName = f.parameters.first()
            val res = mutableListOf<Element.Expression>()
            s.values.forEach {
              //TODO bind `it` to the name saved above
              when (val r = eval(it, bindings)) {
                is Right -> {
                  bindings.addScope()
                  bindings.bindVariable(argName, r.value)
                  val evalRes = f.call(bindings)
                  bindings.removeScope()
                  when (evalRes) {
                    //TODO fix below, I think I need a way to convert from Value to Element
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
  )

  return stdLib
}

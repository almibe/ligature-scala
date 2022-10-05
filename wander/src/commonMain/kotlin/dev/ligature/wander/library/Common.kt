/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.StringLiteral
import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.interpreter.ScriptError
import dev.ligature.wander.parser.*

interface Logger {
  fun log(message: String): dev.ligature.wander.parser.Nothing
}

/**
 * Create a Bindings instance with functions
 */
fun common(logger: Logger = object : Logger {
  override fun log(message: String): dev.ligature.wander.parser.Nothing {
    println(message)
    return dev.ligature.wander.parser.Nothing
  }
}): Bindings {
  val stdLib = Bindings()

  stdLib
    .bindVariable(
      Name("log"),
      NativeFunction(
        listOf(Parameter(Name("message")))) { bindings ->
          when (val message = bindings.read(Name("message"))) {
            is Right -> Right(logger.log((message.value as StringLiteral).value))
            is Left -> TODO()
          }
        }
    )

  stdLib
    .bindVariable(
      Name("ensure"),
      NativeFunction(
        listOf(Parameter(Name("arg")))) { bindings ->
          when (val arg = bindings.read(Name("arg"))) {
            is Right -> {
              val value = arg.value
              if (value is BooleanValue) {
                if (value.value) {
                  Right(Nothing)
                } else {
                  TODO()
                }
              } else {
                TODO()
              }
            }
            is Left -> TODO()
          }
      })

  stdLib
    .bindVariable(
      Name("not"),
      NativeFunction(
        listOf(Parameter(Name("bool")))
      ) //, WanderType.Boolean)),
      { bindings: Bindings ->
        val bool = bindings.read(Name("bool"))
        if (bool is Right) {
          val value = bool.value as BooleanValue
          Right(BooleanValue(!value.value))
        } else {
          TODO()
        }
      }
    )

  stdLib
    .bindVariable(
      Name("and"),
      NativeFunction(
        listOf(
          Parameter(Name("boolLeft")),//, WanderType.Boolean),
          Parameter(Name("boolRight"))//, WanderType.Boolean)
        )) { bindings: Bindings ->
        val left = bindings.read(Name("boolLeft"))
        val right = bindings.read(Name("boolRight"))
        if (left is Right && right is Right) {
          val l = left.value as BooleanValue
          val r = right.value as BooleanValue
          Right(BooleanValue(l.value && r.value))
        } else {
          Left(ScriptError("and requires two booleans"))
        }
      }
    )

  stdLib.bindVariable(
    Name("or"),
    NativeFunction(
      listOf(
        Parameter(Name("boolLeft")),//, WanderType.Boolean),
        Parameter(Name("boolRight"))//, WanderType.Boolean)
      )) { bindings: Bindings ->
      val left = bindings.read(Name("boolLeft"))
      val right = bindings.read(Name("boolRight"))
      if (left is Right && right is Right) {
        val l = left.value as BooleanValue
        val r = right.value as BooleanValue
        Right(BooleanValue(l.value || r.value))
      } else {
        Left (ScriptError("or requires two booleans"))
      }
    }
  )

  stdLib.bindVariable(
    Name("range"),
    NativeFunction(
      listOf(Parameter(Name("start")), Parameter(Name("stop")))) { bindings: Bindings ->
        //TODO for now just return 0 to 9, eventually read start and stop params
        TODO()
      }
    )

//  stdLib.bindVariable(
//    Name("each"),
//    NativeFunction(
//      listOf(Parameter(Name("seq")), Parameter(Name("fn")))) {
//        val seq = bindings.read(Name("seq"))
//        val fn = bindings.read(Name("fn"))
//        if (seq is Right && fn is Right) {
//          val s = seq.value as Seq
//          val f = fn.value as FunctionDefinition
//          val res = mutableListOf<Expression>()
//          s.contents.forEach {
//            f.
//          }
//          Right(BooleanValue(l.value || r.value))
//        } else {
//          Left (ScriptError("or requires two booleans"))
//        }
//      }
//  )

//  stdLib.bindVariable(
//    Name("map"),
//    NativeFunction(
//      listOf(Parameter(Name("seq")), Parameter(Name("fn")))) {
//        val seq = bindings.read(Name("seq"))
//        val fn = bindings.read(Name("fn"))
//        if (seq is Right && fn is Right) {
//          val s = seq.value as Seq
//          val f = fn.value as FunctionDefinition
//          val res = mutableListOf<Expression>()
//          s.contents.forEach {
//            f.
//          }
//          Right(BooleanValue(l.value || r.value))
//        } else {
//          Left (ScriptError("or requires two booleans"))
//        }
//      }
//  )

  // class RangeResultStream implements ResultStream<bigint> {
  //     readonly start: bigint
  //     readonly stop: bigint
  //     private i: bigint

  //     constructor(start: bigint, stop: bigint) {
  //         this.start = start
  //         this.stop = stop
  //         this.i = start
  //     }

  //     next(): Promise<bigint | ResultComplete | ResultError> {
  //         if (this.i < this.stop) {
  //             val result = this.i
  //             this.i++;
  //             return Promise.resolve(result)
  //         } else {
  //             return Promise.resolve(ResultComplete(this.stop - this.start));
  //         }
  //     }
  //     toArray(): Promise<ResultComplete | ResultError | bigint[]> {
  //         throw Error("Method not implemented.");
  //     }
  // }

  // // stdLib.bind(Name("range"), NativeFunction(["start", "stop"], (bindings: Bindings) => {
  // //     val start = bindings.read(Name("start")) as unknown as bigint //TODO check value
  // //     val stop = bindings.read(Name("stop")) as unknown as bigint //TODO check value
  // //     return RangeResultStream(start, stop)
  // // }))

  return stdLib
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.interpreter.ScriptError
import dev.ligature.wander.parser.BooleanValue
import dev.ligature.wander.parser.Name
import dev.ligature.wander.parser.NativeFunction
import dev.ligature.wander.parser.Parameter

/**
 * Create a Bindings instance with functions
 */
fun common(): Bindings {
  val stdLib = Bindings()

//  stdLib = stdLib
//    .bindVariable(
//      Name("log"),
//      NativeFunction(
//        List(Parameter(Name("message"), WanderType.String)),
//        (binding: Bindings) -> TODO()
//      )
//    )
//    .getOrElse(???)
//
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

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.interpreter

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.Either.Left
import dev.ligature.wander.parser.Name
import dev.ligature.wander.parser.NativeFunction
import dev.ligature.wander.parser.Parameter
import dev.ligature.wander.parser.BooleanValue
import dev.ligature.wander.parser.ScriptResult
import dev.ligature.wander.parser.WanderValue

import dev.ligature.Ligature
import dev.ligature.Dataset
import dev.ligature.wander.Bindings

fun createStandardBindings(dataset: Dataset): Bindings {
  val bindings = common()
  return datasetModeBindings(bindings, dataset)
}

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

fun datasetModeBindings(bindings: Bindings, dataset: Dataset): Bindings =
  bindings

//def instanceModeBindings(bindings: Bindings): Bindings = {
//// function instanceScope(scope: ExecutionScope, bindings: Bindings) {
////     // allDatasets(): Promise<Array<Dataset>>;
////     bindings.bind(Name("allDatasets"), NativeFunction([], (_bindings: Bindings) => {
////         return TODO()
////     }))
////     // datasetExists(dataset: Dataset): Promise<boolean>;
////     bindings.bind(Name("datasetExists"), NativeFunction(["dataset"], (_bindings: Bindings) => {
////         return TODO()
////     }))
////     // matchDatasetPrefix(prefix: string): Promise<Array<Dataset>>;
////     bindings.bind(Name("matchDatasetPrefix"), NativeFunction(["prefix"], (_bindings: Bindings) => {
////         return TODO()
////     }))
////     // matchDatasetRange(start: string, end: string): Promise<Array<Dataset>>;
////     bindings.bind(Name("matchDatasetRange"), NativeFunction(["start", "end"], (_bindings: Bindings) => {
////         return TODO()
////     }))
////     // createDataset(dataset: Dataset): Promise<Dataset>;
////     bindings.bind(Name("createDataset"), NativeFunction(["dataset"], (_bindings: Bindings) => {
////         return TODO()
////     }))
////     // deleteDataset(dataset: Dataset): Promise<Dataset>;
////     bindings.bind(Name("deleteDataset"), NativeFunction(["dataset"], (_bindings: Bindings) => {
////         return TODO()
////     }))
////     // query<T>(dataset: Dataset, fn: (readTx: ReadTx) => Promise<T>): Promise<T>;
////     bindings.bind(Name("query"), NativeFunction(["dataset", "fn"], (_bindings: Bindings) => {
////         return TODO()
////     }))
////     // write<T>(dataset: Dataset, fn: (writeTx: WriteTx) => Promise<T>): Promise<T>;
////     bindings.bind(Name("write"), NativeFunction(["dataset", "fn"], (_bindings: Bindings) => {
////         return TODO()
////     }))
//// }
//
//  bindings
//}

// function readScope(scope: ExecutionScope, bindings: Bindings) {
//     //      allStatements(): Promise<Array<Statement>>
//     bindings.bind(Name("allStatements"), NativeFunction([], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     //      matchStatements(entity: Entity | null, attribute: Attribute | null, value: Value | null | LiteralRange, context: Entity | null): Promise<Array<Statement>>
//     bindings.bind(Name("matchStatements"), NativeFunction(["entity", "attribute", "value", "context"], (_bindings: Bindings) => {
//         return TODO()
//     }))
// }

// function writeScope(scope: ExecutionScope, bindings: Bindings) {
//     // /**
//     //  * Returns a new, unique to this collection identifier in the form _:UUID
//     //  */
//     //  generateEntity(prefix: string): Promise<Entity>
//     bindings.bind(Name("newEntity"), NativeFunction(["prefix"], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     //  addStatement(statement: Statement): Promise<Statement>
//     bindings.bind(Name("addStatement"), NativeFunction(["statement"], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     //  removeStatement(statement: Statement): Promise<Statement>
//     bindings.bind(Name("removeStatement"), NativeFunction(["statement"], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     //  /**
//     //   * Cancels this transaction.
//     //   */
//     //  cancel(): any //TODO figure out return type
//     bindings.bind(Name("cancel"), NativeFunction([], (_bindings: Bindings) => {
//         return TODO()
//     }))
// }

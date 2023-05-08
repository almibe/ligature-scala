/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.WanderValue
import dev.ligature.wander.{Parameter, ScriptResult, ScriptError}

import dev.ligature.{Ligature, Dataset}
import dev.ligature.wander.WanderType
import dev.ligature.Identifier

def instanceMode(instance: Ligature): Bindings = {
  var bindings = common()

  bindings = bindings.bindVariable(WanderValue.Name("datasets"), WanderValue.NativeFunction(
    List(),
    (arguments: Seq[Term], binding: Bindings) => Right(WanderValue.LigatureValue(Identifier.fromString("test").getOrElse(???)))
  )).getOrElse(???)

  bindings = bindings.bindVariable(WanderValue.Name("addDataset"), WanderValue.NativeFunction(
    List(Parameter(WanderValue.Name("message"), WanderType.String)),
    (arguments: Seq[Term], binding: Bindings) => ???
  )).getOrElse(???)

  //TODO datasets
  //TODO addDataset
  //TODO removeDataset
  //TODO datasetExists
  //TODO addStatement
  //TODO removeStatement
  //TODO addAll
  //TODO removeAll
  bindings
}

// def createStandardBindings(dataset: Dataset): Bindings = {
//   val bindings = common()
//   datasetModeBindings(bindings, dataset)
// }

/**
 * 
 */
def common(): Bindings = {
  var stdLib = Bindings()

  // stdLib = stdLib
  //   .bindVariable(
  //     Name("log"),
  //     NativeFunction(
  //       List(Parameter(Name("message"), WanderType.String)),
  //       (binding: Bindings) => ???
  //     )
  //   )
  //   .getOrElse(???)

  stdLib = stdLib
    .bindVariable(
      WanderValue.Name("not"),
      WanderValue.NativeFunction(
        List(Parameter(WanderValue.Name("bool"), WanderType.Boolean)),
        (arguments: Seq[Term], bindings: Bindings) =>
          if arguments.size != 1 then
            Left(ScriptError("`not` function requires 1 argument."))
          else
            val evaledArgs = arguments.map(evalTerm(_, bindings))
            evaledArgs.headOption match
              case Some(Right(EvalResult(b: WanderValue.BooleanValue, _))) => Right(WanderValue.BooleanValue(!b.value))
              case _ => Left(ScriptError("`not` function requires 1 boolean argument."))
      )
    )
    .getOrElse(???)

  stdLib = stdLib
    .bindVariable(
      WanderValue.Name("and"),
      WanderValue.NativeFunction(
        List(
          Parameter(WanderValue.Name("boolLeft"), WanderType.Boolean),
          Parameter(WanderValue.Name("boolRight"), WanderType.Boolean)
        ),
        (arguments: Seq[Term], bindings: Bindings) =>
          if arguments.length == 2 then
            val evaledArgs = arguments.map(evalTerm(_, bindings))
            val left = evaledArgs(0) //bindings.read(Name("boolLeft"))
            val right = evaledArgs(1) //bindings.read(Name("boolRight"))
            (left, right) match {
              case (Right(EvalResult(l: WanderValue.BooleanValue, _)), Right(EvalResult(r: WanderValue.BooleanValue, _))) =>
                Right(WanderValue.BooleanValue(l.value && r.value))
              case _ => Left(ScriptError("`and` function requires two booleans"))
            }
          else 
            Left(ScriptError("`and` function requires two booleans"))
      )
    )
    .getOrElse(???)

  stdLib = stdLib
    .bindVariable(
      WanderValue.Name("or"),
      WanderValue.NativeFunction(
        List(
          Parameter(WanderValue.Name("boolLeft"), WanderType.Boolean),
          Parameter(WanderValue.Name("boolRight"), WanderType.Boolean)
        ),
        (arguments: Seq[Term], bindings: Bindings) =>
          if arguments.length == 2 then
            val evaledArgs = arguments.map(evalTerm(_, bindings))
            val left = evaledArgs(0) // bindings.read(Name("boolLeft"))
            val right = evaledArgs(1) //bindings.read(Name("boolRight"))
            (left, right) match {
              case (Right(EvalResult(l: WanderValue.BooleanValue, _)), Right(EvalResult(r: WanderValue.BooleanValue, _))) =>
                Right(WanderValue.BooleanValue(l.value || r.value))
              case _ => Left(ScriptError("`or` function requires two booleans"))
            }
          else
            Left(ScriptError("`or` function requires two booleans"))
      )
    )
    .getOrElse(???)

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

  stdLib
}

def datasetModeBindings(bindings: Bindings, dataset: Dataset): Bindings =
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

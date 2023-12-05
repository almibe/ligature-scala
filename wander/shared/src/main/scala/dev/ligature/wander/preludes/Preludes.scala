/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.preludes

import dev.ligature.wander.{Parameter, WanderValue}
import dev.ligature.wander.interpreter.*
import scala.collection.mutable.ListBuffer
import dev.ligature.wander.*
import dev.ligature.wander.Environment

/**
  */
def common(interpreter: Interpreter): Environment = {
  var stdLib = Environment(interpreter)
  stdLib = bindBooleanPrelude(stdLib)
  stdLib = bindCore(stdLib)
  stdLib
}

def bindCore(environment: Environment): Environment = environment
// environment.addHostFunctions(
//   Seq(
//     HostFunction(
//       "count",
//       (arguments, environment) =>
//         if arguments.size != 1 then Left(WanderError("`not` function requires 1 argument."))
//         else if (environment.graphs.contains("")) {
//           Right((WanderValue.IntValue(environment.graphs.get("").get.size), environment))
//         } else {
//           Right((WanderValue.IntValue(0), environment))
//         }
//         // eval(arguments.head, environment).map {
//         //   _ match
//         //     case _ => (WanderValue.IntValue(5), environment)
//         // }
//     )
//   )
// )

// stdLib = stdLib
//   .bindVariable(
//     Name("log"),
//     NativeFunction(
//       List(Parameter(Name("message"), WanderType.String)),
//       (environment: Environment) => ???
//     )
//   )
//   .getOrElse(???)

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

// // stdLib.bind(Name("range"), NativeFunction(["start", "stop"], (environment: Environment) => {
// //     val start = environment.read(Name("start")) as unknown as bigint //TODO check value
// //     val stop = environment.read(Name("stop")) as unknown as bigint //TODO check value
// //     return RangeResultStream(start, stop)
// // }))

// function readScope(scope: ExecutionScope, environment: Environment) {
//     //      allStatements(): Promise<Array<Statement>>
//     environment.bind(Name("allStatements"), NativeFunction([], (_environment: Environment) => {
//         return TODO()
//     }))
//     //      matchStatements(entity: Entity | null, attribute: Attribute | null, value: Value | null | LiteralRange, context: Entity | null): Promise<Array<Statement>>
//     environment.bind(Name("matchStatements"), NativeFunction(["entity", "attribute", "value", "context"], (_environment: Environment) => {
//         return TODO()
//     }))
// }

// function writeScope(scope: ExecutionScope, environment: Environment) {
//     // /**
//     //  * Returns a new, unique to this collection identifier in the form _:UUID
//     //  */
//     //  generateEntity(prefix: string): Promise<Entity>
//     environment.bind(Name("newEntity"), NativeFunction(["prefix"], (_environment: Environment) => {
//         return TODO()
//     }))
//     //  addStatement(statement: Statement): Promise<Statement>
//     environment.bind(Name("addStatement"), NativeFunction(["statement"], (_environment: Environment) => {
//         return TODO()
//     }))
//     //  removeStatement(statement: Statement): Promise<Statement>
//     environment.bind(Name("removeStatement"), NativeFunction(["statement"], (_environment: Environment) => {
//         return TODO()
//     }))
//     //  /**
//     //   * Cancels this transaction.
//     //   */
//     //  cancel(): any //TODO figure out return type
//     environment.bind(Name("cancel"), NativeFunction([], (_environment: Environment) => {
//         return TODO()
//     }))
// }

////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Below is code that needs to be moved to ligature.
///////////////////////////////////////////////////////////////////////////////////////////////////////////

// // def createStandardEnvironment(dataset: Dataset): Environment = {
// //   val environment = common()
// //   datasetModeEnvironment(environment, dataset)
// // }

// def instancePrelude(instance: Ligature): Environment = {
//   var environment = common()

//   environment = environment.bindVariable(Name("datasets"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       instance.allDatasets()
//         .compile.toList
//         .map { datasets => WanderValue.ListValue( datasets.map { ds => WanderValue.LigatureValue(LigatureLiteral.StringLiteral(ds.name.toString()))})}
//   )).getOrElse(???)

//   environment = environment.bindVariable(Name("addDataset"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance.createDataset(Dataset.fromString(datasetName).getOrElse(???)).map(_ => WanderValue.Nothing)
//         case _ => ???
//   )).getOrElse(???)

//   environment = environment.bindVariable(Name("removeDataset"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance.deleteDataset(Dataset.fromString(datasetName).getOrElse(???)).map(_ => WanderValue.Nothing)
//         case _ => ???
//   )).getOrElse(???)

//   environment = environment.bindVariable(Name("datasetExists"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance.datasetExists(Dataset.fromString(datasetName).getOrElse(???)).map(res => WanderValue.BooleanValue(res))
//         case _ => ???
//   )).getOrElse(???)

//   environment = environment.bindVariable(Name("allStatements"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance
//             .allStatements(Dataset.fromString(datasetName).getOrElse(???))
//             .map(statementToWanderValue)
//             .compile.toList.map(WanderValue.ListValue(_))
//         case _ => ???
//   )).getOrElse(???)

//   environment = environment.bindVariable(Name("addStatements"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       (arguments(0), arguments(1)) match
//         case (Term.StringLiteral(datasetName),
//               Term.List(statementTerms)) =>
//                 val dataset = Dataset.fromString(datasetName).getOrElse(???)
//                 termsToStatements(statementTerms, ListBuffer()) match
//                   case Left(value) => ???
//                   case Right(statements) =>
//                     instance
//                       .addStatements(dataset, Stream.emits(statements))
//                       .map { _ => WanderValue.Nothing }
//         case _ => ???
//   )).getOrElse(???)

//   environment = environment.bindVariable(Name("removeStatements"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       (arguments(0), arguments(1)) match
//         case (Term.StringLiteral(datasetName),
//               Term.List(statementTerms)) =>
//                 val dataset = Dataset.fromString(datasetName).getOrElse(???)
//                 termsToStatements(statementTerms, ListBuffer()) match
//                   case Left(value) => ???
//                   case Right(statements) =>
//                     instance
//                       .removeStatements(dataset, Stream.emits(statements))
//                       .map { _ => WanderValue.Nothing }
//         case _ => ???
//   )).getOrElse(???)

//   def termToIdentifierOption(term: Term): Option[Identifier] =
//     term match
//       case Term.NothingLiteral | Term.QuestionMark => None
//       case Term.IdentifierLiteral(identifier) => Some(identifier)
//       case _ => ???

//   def termToValueOption(term: Term): Option[Value] =
//     term match
//       case Term.NothingLiteral | Term.QuestionMark => None
//       case Term.IdentifierLiteral(identifier) => Some(identifier)
//       case _ => ???

//   environment = environment.bindVariable(Name("query"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       (arguments(0), arguments(1), arguments.lift(2), arguments.lift(3)) match
//         case (Term.StringLiteral(datasetName), entityTerm, Some(attributeTerm), Some(valueTerm)) =>
//           val dataset = Dataset.fromString(datasetName).getOrElse(???)
//           val entity = termToIdentifierOption(entityTerm)
//           val attribute = termToIdentifierOption(attributeTerm)
//           val value = termToValueOption(valueTerm)
//           instance.query(dataset) { tx =>
//             tx.matchStatements(entity, attribute, value)
//               .map(statementToWanderValue)
//               .compile.toList.map(WanderValue.ListValue(_))
//           }
//         case (Term.StringLiteral(datasetName), query: Term.WanderFunction, None, None) =>
//           query match
//             case Term.WanderFunction(name :: Nil, body) =>
//               val dataset = Dataset.fromString(datasetName).getOrElse(???)
//               instance.query(dataset) { tx =>
//                 val matchFunction = WanderValue.NativeFunction((arguments, environment) =>
//                   (arguments.lift(0), arguments.lift(1), arguments.lift(2)) match
//                     case (Some(entityTerm), Some(attributeTerm), Some(valueTerm)) =>
//                       val dataset = Dataset.fromString(datasetName).getOrElse(???)
//                       val entity = termToIdentifierOption(entityTerm)
//                       val attribute = termToIdentifierOption(attributeTerm)
//                       val value = termToValueOption(valueTerm)
//                       tx.matchStatements(entity, attribute, value)
//                         .map(statementToWanderValue)
//                         .compile.toList.map(WanderValue.ListValue(_))
//                     case _ => ???)
//                 val newEnvironment = environment.bindVariable(name, matchFunction).getOrElse(???)
//                 eval(query.body, newEnvironment).map(_.result)
//               }
//             case _ => ???
//         case _ => ???
//   )).getOrElse(???)
//   environment
// }

// def termsToStatements(terms: Seq[Term], statements: ListBuffer[Statement]): Either[LigatureError, Seq[Statement]] =
//   if terms.nonEmpty then
//     terms.head match
//       case Term.List(statementTerms) =>
//         termsToStatement(statementTerms) match
//           case Right(statement) =>
//             termsToStatements(terms.tail, statements += statement)
//           case Left(err) => Left(err)
//       case _ => println(terms.head); ???
//   else
//     Right(statements.toSeq)

// def termsToStatement(terms: Seq[Term]): Either[LigatureError, Statement] =
//   if terms.size == 3 then
//     val entity = terms(0)
//     val attribute = terms(1)
//     val value = terms(2)
//     (entity, attribute, value) match
//       case (entity: Term.IdentifierLiteral, attribute: Term.IdentifierLiteral, value: Term.IdentifierLiteral) =>
//         Right(Statement(entity.value, attribute.value, value.value))
//       case _ => ???
//   else
//     ???

// def statementToWanderValue(statement: Statement): WanderValue =
//   WanderValue.ListValue(Seq(
//     WanderValue.LigatureValue(statement.entity),
//     WanderValue.LigatureValue(statement.attribute),
//     WanderValue.LigatureValue(statement.value)
//   ))

// def datasetModeEnvironment(environment: Environment, dataset: Dataset): Environment =
//   environment

//def instancePreludeEnvironment(environment: Environment): Environment = {
//// function instanceScope(scope: ExecutionScope, environment: Environment) {
////     // allDatasets(): Promise<Array<Dataset>>;
////     environment.bind(Name("allDatasets"), NativeFunction([], (_environment: Environment) => {
////         return TODO()
////     }))
////     // datasetExists(dataset: Dataset): Promise<boolean>;
////     environment.bind(Name("datasetExists"), NativeFunction(["dataset"], (_environment: Environment) => {
////         return TODO()
////     }))
////     // matchDatasetPrefix(prefix: string): Promise<Array<Dataset>>;
////     environment.bind(Name("matchDatasetPrefix"), NativeFunction(["prefix"], (_environment: Environment) => {
////         return TODO()
////     }))
////     // matchDatasetRange(start: string, end: string): Promise<Array<Dataset>>;
////     environment.bind(Name("matchDatasetRange"), NativeFunction(["start", "end"], (_environment: Environment) => {
////         return TODO()
////     }))
////     // createDataset(dataset: Dataset): Promise<Dataset>;
////     environment.bind(Name("createDataset"), NativeFunction(["dataset"], (_environment: Environment) => {
////         return TODO()
////     }))
////     // deleteDataset(dataset: Dataset): Promise<Dataset>;
////     environment.bind(Name("deleteDataset"), NativeFunction(["dataset"], (_environment: Environment) => {
////         return TODO()
////     }))
////     // query<T>(dataset: Dataset, fn: (readTx: ReadTx) => Promise<T>): Promise<T>;
////     environment.bind(Name("query"), NativeFunction(["dataset", "fn"], (_environment: Environment) => {
////         return TODO()
////     }))
////     // write<T>(dataset: Dataset, fn: (writeTx: WriteTx) => Promise<T>): Promise<T>;
////     environment.bind(Name("write"), NativeFunction(["dataset", "fn"], (_environment: Environment) => {
////         return TODO()
////     }))
//// }
//
//  environment
//}

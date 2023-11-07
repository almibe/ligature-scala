/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// package dev.ligature.wander.preludes

// import dev.ligature.wander.WanderValue
// import dev.ligature.wander.{Parameter, ScriptResult}

// //import dev.ligature.{Ligature, Dataset, Value}
// import dev.ligature.wander.WanderType
// // import dev.ligature.Identifier
// // import dev.ligature.LigatureError
// // import dev.ligature.LigatureLiteral
// // import cats.effect.IO
// // import dev.ligature.Statement
// // import fs2.Stream
// import scala.collection.mutable.ListBuffer
// import dev.ligature.wander.*

// def instancePrelude(instance: Ligature): Bindings = {
//   var bindings = common()

//   bindings = bindings.bindVariable(Name("datasets"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], binding: Bindings) =>
//       instance.allDatasets()
//         .compile.toList
//         .map { datasets => WanderValue.ListValue( datasets.map { ds => WanderValue.LigatureValue(LigatureLiteral.StringLiteral(ds.name.toString()))})}
//   )).getOrElse(???)

//   bindings = bindings.bindVariable(Name("addDataset"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], binding: Bindings) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance.createDataset(Dataset.fromString(datasetName).getOrElse(???)).map(_ => WanderValue.Nothing)
//         case _ => ???
//   )).getOrElse(???)

//   bindings = bindings.bindVariable(Name("removeDataset"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], binding: Bindings) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance.deleteDataset(Dataset.fromString(datasetName).getOrElse(???)).map(_ => WanderValue.Nothing)
//         case _ => ???
//   )).getOrElse(???)

//   bindings = bindings.bindVariable(Name("datasetExists"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], binding: Bindings) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance.datasetExists(Dataset.fromString(datasetName).getOrElse(???)).map(res => WanderValue.BooleanValue(res))
//         case _ => ???
//   )).getOrElse(???)

//   bindings = bindings.bindVariable(Name("allStatements"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], binding: Bindings) =>
//       arguments.head match
//         case Term.StringLiteral(datasetName) =>
//           instance
//             .allStatements(Dataset.fromString(datasetName).getOrElse(???))
//             .map(statementToWanderValue)
//             .compile.toList.map(WanderValue.ListValue(_))
//         case _ => ???
//   )).getOrElse(???)

//   bindings = bindings.bindVariable(Name("addStatements"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], binding: Bindings) =>
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

//   bindings = bindings.bindVariable(Name("removeStatements"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], binding: Bindings) =>
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

//   bindings = bindings.bindVariable(Name("query"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], bindings: Bindings) =>
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
//                 val matchFunction = WanderValue.NativeFunction((arguments, bindings) =>
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
//                 val newBindings = bindings.bindVariable(name, matchFunction).getOrElse(???)
//                 eval(query.body, newBindings).map(_.result)
//               }
//             case _ => ???
//         case _ => ???
//   )).getOrElse(???)
//   bindings
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

// // def createStandardBindings(dataset: Dataset): Bindings = {
// //   val bindings = common()
// //   datasetModeBindings(bindings, dataset)
// // }

// /**
//  * 
//  */
// def common(): Bindings = {
//   var stdLib = Bindings()

//   // stdLib = stdLib
//   //   .bindVariable(
//   //     Name("log"),
//   //     NativeFunction(
//   //       List(Parameter(Name("message"), WanderType.String)),
//   //       (binding: Bindings) => ???
//   //     )
//   //   )
//   //   .getOrElse(???)

//   stdLib = stdLib
//     .bindVariable(
//       Name("not"),
//       WanderValue.NativeFunction(
//         (arguments: Seq[Term], bindings: Bindings) =>
//           if arguments.size != 1 then
//             IO.raiseError(LigatureError("`not` function requires 1 argument."))
//           else
//             evalTerm(arguments.head, bindings).map { 
//               _ match
//                 case EvalResult(b: WanderValue.BooleanValue, _) => WanderValue.BooleanValue(!b.value)
//                 case _ => throw LigatureError("`not` function requires 1 boolean argument.")
//             }
//       )
//     )
//     .getOrElse(???)

//   stdLib = stdLib
//     .bindVariable(
//       Name("and"),
//       WanderValue.NativeFunction(
//         (arguments: Seq[Term], bindings: Bindings) =>
//           if arguments.length == 2 then
//             val res = for {
//               left <- evalTerm(arguments(0), bindings)
//               right <- evalTerm(arguments(1), bindings)
//             } yield (left, right)
//             res.map { r =>
//               (r._1.result, r._2.result) match
//                 case (WanderValue.BooleanValue(left), WanderValue.BooleanValue(right)) => WanderValue.BooleanValue(left && right)
//                 case _ => throw LigatureError("`and` function requires two booleans")
//             }
//           else
//             IO.raiseError(LigatureError("`and` function requires two booleans"))
//       )
//     )
//     .getOrElse(???)

//   stdLib = stdLib
//     .bindVariable(
//       Name("or"),
//       WanderValue.NativeFunction(
//         (arguments: Seq[Term], bindings: Bindings) =>
//           if arguments.length == 2 then
//             val res = for {
//               left <- evalTerm(arguments(0), bindings)
//               right <- evalTerm(arguments(1), bindings)
//             } yield (left, right)
//             res.map { r =>
//               (r._1.result, r._2.result) match
//                 case (WanderValue.BooleanValue(left), WanderValue.BooleanValue(right)) => WanderValue.BooleanValue(left || right)
//                 case _ => throw LigatureError("`or` function requires two booleans")
//             }
//           else
//             IO.raiseError(LigatureError("`or` function requires two booleans"))
//       )
//     )
//     .getOrElse(???)

//   // class RangeResultStream implements ResultStream<bigint> {
//   //     readonly start: bigint
//   //     readonly stop: bigint
//   //     private i: bigint

//   //     constructor(start: bigint, stop: bigint) {
//   //         this.start = start
//   //         this.stop = stop
//   //         this.i = start
//   //     }

//   //     next(): Promise<bigint | ResultComplete | ResultError> {
//   //         if (this.i < this.stop) {
//   //             val result = this.i
//   //             this.i++;
//   //             return Promise.resolve(result)
//   //         } else {
//   //             return Promise.resolve(ResultComplete(this.stop - this.start));
//   //         }
//   //     }
//   //     toArray(): Promise<ResultComplete | ResultError | bigint[]> {
//   //         throw Error("Method not implemented.");
//   //     }
//   // }

//   // // stdLib.bind(Name("range"), NativeFunction(["start", "stop"], (bindings: Bindings) => {
//   // //     val start = bindings.read(Name("start")) as unknown as bigint //TODO check value
//   // //     val stop = bindings.read(Name("stop")) as unknown as bigint //TODO check value
//   // //     return RangeResultStream(start, stop)
//   // // }))

//   stdLib
// }

// def datasetModeBindings(bindings: Bindings, dataset: Dataset): Bindings =
//   bindings

//def instancePreludeBindings(bindings: Bindings): Bindings = {
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

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.ligature

import dev.ligature.bend.Environment
import dev.ligature.Ligature
import dev.ligature.bend.libraries.std
import dev.ligature.bend.*
import dev.ligature.Graph

def ligatureEnvironment(ligature: Ligature): Environment =
  std()
    .addHostProperties(
      Seq(
        HostProperty(
          "Ligature.graphs",
          "Get all graphs from this instance.",
          Tag.Single(Name("Core.Array")),
          (environment: Environment) => {
            val graphs = ligature.allGraphs().map(g => WanderValue.String(g.name))
            Right((WanderValue.Array(graphs.toSeq), environment))
          }
        )
      )
    )
    .addHostFunctions(
      Seq(
        // HostFunction(
        //   "Ligature.createGraph",
        //   (arguments, environment) =>
        //     arguments match
        //       case Seq(Expression.StringValue(graphName)) =>
        //         ligature.createGraph(Graph(graphName))
        //         Right(WanderValue.Nothing, environment)
        //       case _ => ???
        // ),
        // HostFunction(
        //   "Ligature.deleteGraph",
        //   (arguments, environment) =>
        //     arguments match
        //       case Seq(Expression.StringValue(graphName)) =>
        //         ligature.deleteGraph(Graph(graphName))
        //         Right(WanderValue.Nothing, environment)
        //       case _ => ???
        // )
      )
    )

////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Below is code that needs to be moved to ligature.
///////////////////////////////////////////////////////////////////////////////////////////////////////////

// // def createStandardEnvironment(dataset: Dataset): Environment = {
// //   val environment = common()
// //   datasetModeEnvironment(environment, dataset)
// // }

// def instanceLibrary(instance: Ligature): Environment = {
//   var environment = common()

//   environment = environment.bindVariable(Name("datasets"), WanderValue.NativeFunction(
//     (arguments: Seq[Term], environment: Environment) =>
//       instance.allDatasets()
//         .compile.toList
//         .map { datasets => WanderValue.ListValue( datasets.map { ds => WanderValue.LigatureValue(LigatureValue.StringLiteral(ds.name.toString()))})}
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

//def instanceLibraryEnvironment(environment: Environment): Environment = {
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

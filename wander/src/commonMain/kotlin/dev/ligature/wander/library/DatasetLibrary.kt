/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import dev.ligature.Dataset
import dev.ligature.QueryTx
import dev.ligature.wander.interpreter.Bindings
import dev.ligature.wander.parser.Name
import dev.ligature.wander.parser.NativeFunction
import kotlinx.coroutines.flow.fold

fun datasetQueryBindings(queryTx: QueryTx, dataset: Dataset, bindings: Bindings = common()): Bindings {
  //      allStatements(): Promise<Array<Statement>>
//  bindings.bindVariable(Name("allStatements"), NativeFunction(listOf()) {
//    queryTx.allStatements().fold("", { current, running -> "$current$running" })
//  })


  return bindings
}

//function readScope(scope: ExecutionScope, bindings: Bindings) {
//  //      matchStatements(entity: Entity | null, attribute: Attribute | null, value: Value | null | LiteralRange, context: Entity | null): Promise<Array<Statement>>
//  bindings.bind(Name("matchStatements"), NativeFunction(["entity", "attribute", "value", "context"], (_bindings: Bindings) => {
//    return TODO()
//  }))
//}
//
////function writeScope(scope: ExecutionScope, bindings: Bindings) {
////  // /**
////  //  * Returns a new, unique to this collection identifier in the form _:UUID
////  //  */
////  //  generateEntity(prefix: string): Promise<Entity>
////  bindings.bind(Name("newEntity"), NativeFunction(["prefix"], (_bindings: Bindings) => {
////    return TODO()
////  }))
////  //  addStatement(statement: Statement): Promise<Statement>
////  bindings.bind(Name("addStatement"), NativeFunction(["statement"], (_bindings: Bindings) => {
////    return TODO()
////  }))
////  //  removeStatement(statement: Statement): Promise<Statement>
////  bindings.bind(Name("removeStatement"), NativeFunction(["statement"], (_bindings: Bindings) => {
////    return TODO()
////  }))
////  //  /**
////  //   * Cancels this transaction.
////  //   */
////  //  cancel(): any //TODO figure out return type
////  bindings.bind(Name("cancel"), NativeFunction([], (_bindings: Bindings) => {
////    return TODO()
////  }))
////}

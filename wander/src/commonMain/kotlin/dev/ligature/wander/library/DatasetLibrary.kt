/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

import dev.ligature.Dataset
import dev.ligature.QueryTx
import dev.ligature.wander.interpreter.Bindings

fun datasetQueryBindings(queryTx: QueryTx, dataset: Dataset, bindings: Bindings = common()): Bindings {
  //      allStatements(): Promise<Array<Statement>>
//  bindings.bindVariable(Name("allStatements"), NativeFunction(listOf()) {
//    queryTx.allStatements().fold("", { current, running -> "$current$running" })
//  })
  return bindings
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import cats.effect.IO
import dev.ligature.*
import fs2.Stream
import jetbrains.exodus.ByteIterable
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.env.Transaction

import scala.collection.mutable.ArrayBuffer

/** Represents a QueryTx within the context of a Ligature instance and a single
  * Dataset
  */
class XodusQueryTx(private val tx: Transaction, private val xodusOperations: XodusOperations, private val dataset: Dataset) extends QueryTx {

  private def readStatement(bytes: ByteIterable): Statement = {
    val idToIdentifierStore = xodusOperations.openStore(tx, LigatureStore.IdToIdentifierStore)
    //ignore dataset id [8]
    //read entity id [8]
    val entityID = bytes.subIterable(8,8)

    //look up entity
    //read attribute id [8]
    //look up attribute
    //get value type [1]
    //read value [8]
    //construct Statement
    ???
  }

  /** Returns all Statements in this Dataset. */
  override def allStatements(): Stream[IO, Statement] = Stream.emits {
    val output: ArrayBuffer[Statement] = ArrayBuffer()
    val datasetToIdStore = xodusOperations.openStore(tx, LigatureStore.DatasetToIdStore)
    val datasetIdResult = datasetToIdStore.get(tx, StringBinding.stringToEntry(dataset.name))
    if (datasetIdResult != null) {
      val eavStore = xodusOperations.openStore(tx, LigatureStore.AEVStore)
      val eavCursor = eavStore.openCursor(tx)
      var continue = eavCursor.getSearchKeyRange(datasetIdResult) != null
      while (continue) {
        val statement = eavCursor.getKey
        if (datasetIdResult == statement.subIterable(0, datasetIdResult.getLength)) {
          output.append(readStatement(statement))
          eavCursor.getNext
        } else {
          continue = false
        }
      }
    }
    output
  }

  /** Returns all Statements that match the given criteria. If a
    * parameter is None then it matches all, so passing all Nones is the same as
    * calling allStatements.
    */
  override def matchStatements(
      entity: Option[Identifier],
      attribute: Option[Identifier],
      value: Option[Value]
  ): Stream[IO, Statement] = ???

  /** Returns all PersistedStatements that match the given criteria. If a
    * parameter is None then it matches all.
    */
  override def matchStatementsRange(
      entity: Option[Identifier],
      attribute: Option[Identifier],
      range: dev.ligature.Range
  ): Stream[IO, Statement] = ???
}

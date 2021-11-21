/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.indexeddb

import cats.effect.{IO, Resource}
import dev.ligature.{Dataset, Ligature, QueryTx, WriteTx}
import fs2.Stream

class IndexedDBLigature extends Ligature {
  def allDatasets(): Stream[IO, Dataset] = ???

  def datasetExists(dataset: Dataset): IO[Boolean] = ???

  def matchDatasetsPrefix(
                           prefix: String,
                         ): Stream[IO, Dataset] = ???

  def matchDatasetsRange(
                          start: String,
                          end: String,
                        ): Stream[IO, Dataset] = ???

  def createDataset(dataset: Dataset): IO[Unit] = ???

  def deleteDataset(dataset: Dataset): IO[Unit] = ???

  def query(dataset: Dataset): Resource[IO, QueryTx] = ???

  def write(dataset: Dataset): Resource[IO, WriteTx] = ???

  def close(): IO[Unit] = ???
}

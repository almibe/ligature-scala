/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import cats.effect.IO
import fs2.Stream

class LigatureLMDB extends Ligature {
  override def networks(): Stream[IO, String] = {
    ???
  }

  override def addNetwork(name: String): IO[Unit] = {
    ???
  }

  def addEntries(name: String, entries: fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple]
    ): fs2.Stream[cats.effect.IO, Unit] = ???

  def query(name: String, query: dev.ligature.wander.LigatureValue.Pattern, template:
    dev.ligature.wander.LigatureValue.Pattern |
    dev.ligature.wander.LigatureValue.Quote):
    fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple] = ???

  def read(name: String): fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple] = ???

  def removeEntries
  (name: String, entries: fs2.Stream[cats.effect.IO, dev.ligature.wander.Triple]
    ): fs2.Stream[cats.effect.IO, Unit] = ???
    
  def removeNetwork(name: String): cats.effect.IO[Unit] = ???

}

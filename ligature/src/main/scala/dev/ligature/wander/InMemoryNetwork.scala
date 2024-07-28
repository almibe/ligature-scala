/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

case class InMemoryNetwork(val triples: Set[Triple]) extends INetwork {

  override def educe(pattern: INetwork): Set[Map[String, LigatureValue]] = 
    ???

  override def write(): Set[Triple] = this.triples

  override def count(): Long = this.triples.size

  override def union(other: INetwork): INetwork = 
    ???

  override def apply(values: Map[String, LigatureValue]): INetwork = 
    ???

  override def minus(other: INetwork): INetwork = 
    ???
  override def query(search: INetwork, template: INetwork): INetwork = ???

  override def infer(search: INetwork, template: INetwork): INetwork = ???


}
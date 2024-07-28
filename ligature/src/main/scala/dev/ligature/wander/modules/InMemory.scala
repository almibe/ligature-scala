/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

val im = 0

// import scala.collection.mutable.HashMap
// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.Field
// import dev.ligature.wander.HostFunction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import dev.ligature.wander.Triple
// import scala.collection.mutable.HashSet

// trait LigatureStore {
//   def networks(): Seq[String]
//   def addNetwork(name: String): Unit
//   def removeNetwork(name: String): Unit
//   def add(name: String, network: Seq[Triple]): Unit
//   def remove(name: String, network: Seq[Triple]): Unit
//   def query(name: String, network: Seq[Triple]): Seq[Triple]
// }

// class InMemoryStore extends LigatureStore {
//   private val store = scala.collection.mutable.HashMap[String, HashSet[Triple]]()

//   override def networks(): Seq[String] =
//     store.keys.toSeq

//   override def addNetwork(name: String): Unit = 
//     val _ = store.put(name, HashSet())
//     ()

//   override def removeNetwork(name: String): Unit = 
//     val _ = store.remove(name)
//     ()

//   override def add(name: String, network: Seq[Triple]): Unit =
//     store.get(name) match {
//       case None => ???
//       case Some(triples) => triples.addAll(network)
//     }

//   override def remove(name: String, network: Seq[Triple]): Unit = 
//     store.get(name) match {
//       case None => ???
//       case Some(triples) => triples.subtractAll(network)
//     }

//   override def query(name: String, network: Seq[Triple]): Seq[Triple] = 
//     store.get(name) match {
//       case None => ???
//       case Some(value) => 
//         value.toSeq
//     }
// }

// val inMemoryModule: LigatureValue.Module =
//   val instance = InMemoryStore()
//   LigatureValue.Module(
//     Map(
//       Field("networks") -> LigatureValue.Function(
//         HostFunction(
//           "Get all Network names.",
//           Seq(
//             TaggedField(Field("_"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (_arguments, environment) =>
//             val names = instance.networks().map(name => LigatureValue.String(name))
//             Right((LigatureValue.Array(names), environment))
//         )
//       ),
//       Field("addNetwork") -> LigatureValue.Function(
//         HostFunction(
//           "Add a new Network.",
//           Seq(
//             TaggedField(Field("networkName"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name)) => 
//                 instance.addNetwork(name)
//                 Right((LigatureValue.Module(Map.empty), environment))
//               case _ => ???
//             }
//         )
//       ),
//       Field("removeNetwork") -> LigatureValue.Function(
//         HostFunction(
//           "Delete a Network.",
//           Seq(
//             TaggedField(Field("networkName"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name)) => 
//                 instance.removeNetwork(name)
//                 Right((LigatureValue.Module(Map.empty), environment))
//               case _ => ???
//             }
//         )
//       ),
//       Field("add") -> LigatureValue.Function(
//         HostFunction(
//           "Add Triples to an existing Network.",
//           Seq(
//             TaggedField(Field("storeName"), Tag.Untagged),
//             TaggedField(Field("network"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name), LigatureValue.Network(network)) => {
//                 instance.add(name, network.toSeq)
//                 Right((LigatureValue.Network(Set.empty), environment))
//               }
//               case _ => ???
//             }
//         )
//       ),
//       Field("remove") -> LigatureValue.Function(
//         HostFunction(
//           "Remove Triples from an existing Network.",
//           Seq(
//             TaggedField(Field("storeName"), Tag.Untagged),
//             TaggedField(Field("network"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name), LigatureValue.Network(network)) => {
//                 instance.add(name, network.toSeq)
//                 Right((LigatureValue.Network(Set.empty), environment))
//               }
//               case _ => ???
//             }
//         )
//       ),
//       Field("query") -> LigatureValue.Function(
//         HostFunction(
//           "Query a Store.",
//           Seq(
//             TaggedField(Field("storeName"), Tag.Untagged),
//             TaggedField(Field("network"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name), LigatureValue.Network(network)) => {
//                 val res = instance.query(name, network.toSeq)
//                 Right((LigatureValue.Network(res.toSet), environment))
//               }
//               case _ => ???
//             }
//         )
//       )
//   )
// )

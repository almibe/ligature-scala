/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

class Test {}

// import dev.ligature.wander.LigatureValue
// import dev.ligature.wander.Field
// import dev.ligature.wander.HostFunction
// import dev.ligature.wander.TaggedField
// import dev.ligature.wander.Tag
// import jetbrains.exodus.env.{StoreConfig, Environments, Store, Transaction}
// import jetbrains.exodus.{ArrayByteIterable, ByteIterable}
// import scala.jdk.CollectionConverters.ListHasAsScala
//import scala.collection.mutable.ArrayBuffer

// class XodusKeylime(path: String) extends Keylime {
//   val env = Environments.newInstance(path)
//   def addStore(name: String): Unit =
//     val txn = env.beginTransaction()
//     val _ = env.openStore(name, StoreConfig.WITHOUT_DUPLICATES, txn)
//     val _ = txn.commit()
//   def edit(name: String): dev.ligature.wander.modules.KeylimeEditTx =
//     val txn = env.beginTransaction()
//     val store = env.openStore(name, StoreConfig.WITHOUT_DUPLICATES, txn)
//     XodusKeylimeEditTx(store, txn)
//   def read(name: String): dev.ligature.wander.modules.KeylimeReadTx =
//     val txn = env.beginTransaction()
//     val store = env.openStore(name, StoreConfig.WITHOUT_DUPLICATES, txn)
//     XodusKeylimeReadTx(store, txn)
//   def removeStore(name: String): Unit =
//     val txn = env.beginTransaction()
//     val _ = env.removeStore(name, txn)
//     val _ = txn.commit()
//   def stores(): Seq[String] =
//     val txn = env.beginTransaction()
//     val names = env.getAllStoreNames(txn)
//     val _ = txn.commit()
//     names.asScala.toSeq
// }

// def bytesToByteIterable(bytes: Seq[Byte]): ByteIterable =
//   ArrayByteIterable(bytes.toArray)

// def byteIterableToBytes(itr: ByteIterable): Seq[Byte] =
//   itr.getBytesUnsafe().toSeq

// class XodusKeylimeReadTx(store: Store, txn: Transaction) extends KeylimeReadTx {
//   def get(key: Seq[Byte]): Option[Seq[Byte]] =
//     val res = store.get(txn, bytesToByteIterable(key))
//     if res == null then
//       None
//     else
//       Some(byteIterableToBytes(res))
//   def prefix(prefix: Seq[Byte]): Seq[Seq[Byte]] =
//     // val cursor = store.openCursor()
//     // val results = ArrayBuffer()
//     // cursor.getSearchKey(bytesToByteIterable(prefix))
//     ???
// }

// class XodusKeylimeEditTx(store: Store, txn: Transaction) extends KeylimeEditTx {
//   def delete(key: Seq[Byte]): Unit =
//     val _ = store.delete(txn, bytesToByteIterable(key))

//   def get(key: Seq[Byte]): Option[Seq[Byte]] =
//     val res = store.get(txn, bytesToByteIterable(key))
//     if res == null then
//       None
//     else
//       Some(byteIterableToBytes(res))
//   def put(key: Seq[Byte], value: Seq[Byte]): Unit =
//     val _ = store.put(txn, bytesToByteIterable(key), bytesToByteIterable(value))

//   def commit(): Unit =
//     val _ = txn.commit()
// }

// val xodusModule: LigatureValue.Module =
//   val sep = System.getProperty("file.separator")
//   val home = System.getProperty("user.home")
//   val instance = XodusKeylime(s"${home}${sep}.ligature${sep}xodus${sep}")
//   LigatureValue.Module(
//     Map(
//       Field("stores") -> LigatureValue.Function(
//         HostFunction(
//           "Get all Store names.",
//           Seq(
//             TaggedField(Field("_"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (_arguments, environment) =>
//             val names = instance.stores().map(name => LigatureValue.String(name))
//             Right((LigatureValue.Array(names), environment))
//         )
//       ),
//       Field("addStore") -> LigatureValue.Function(
//         HostFunction(
//           "Add a new Store.",
//           Seq(
//             TaggedField(Field("storeName"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name)) =>
//                 instance.addStore(name)
//                 Right((LigatureValue.Module(Map.empty), environment))
//               case _ => ???
//             }
//         )
//       ),
//       Field("deleteStore") -> LigatureValue.Function(
//         HostFunction(
//           "Delete a Store.",
//           Seq(
//             TaggedField(Field("storeName"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name)) =>
//                 instance.removeStore(name)
//                 Right((LigatureValue.Module(Map.empty), environment))
//               case _ => ???
//             }
//         )
//       ),
//       Field("edit") -> LigatureValue.Function(
//         HostFunction(
//           "Edit a Store.",
//           Seq(
//             TaggedField(Field("storeName"), Tag.Untagged),
//             TaggedField(Field("fn"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name), LigatureValue.Function(fn)) => {
//                 val tx = instance.edit(name)
//                 val editModule = keylimeEditModule(tx)
//                 val res = fn.call(Seq(editModule), environment).map((_, environment))
//                 tx.commit()
//                 res
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
//             TaggedField(Field("fn"), Tag.Untagged)
//           ),
//           Tag.Untagged,
//           (arguments, environment) =>
//             arguments match {
//               case Seq(LigatureValue.String(name), LigatureValue.Function(fn)) => {
//                 // val readModule = keylimeQueryModule(instance.read(name))
//                 // fn.call(Seq(readModule), environment).map((_, environment))
//                 ???
//               }
//               case _ => ???
//             }
//         )
//       )
//   )
// )

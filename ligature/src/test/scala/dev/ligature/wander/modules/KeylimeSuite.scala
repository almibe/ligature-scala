/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

class KeylimeSuite extends munit.FunSuite {
  test("Keylime instance should start empty") {
    val instance = InMemoryKeylime()
    assertEquals(instance.stores(), Seq())
  }
  test("Add and remove store") {
    val instance = InMemoryKeylime()
    instance.addStore("test")
    assertEquals(instance.stores(), Seq("test"))
    instance.removeStore("test")
    assertEquals(instance.stores(), Seq())
  }
  test("add values to store") {
    val instance = InMemoryKeylime()
    instance.addStore("test")
    val editTx = instance.edit("test")
    editTx.put(Seq(0.toByte), Seq(1.toByte))
    val tx = instance.read("test")
    val res = Right(tx.get(Seq(0.toByte)))
    assertEquals(res, Right(Some(Seq(1.toByte))))
  }
  test("add and remove values to store") {
    val instance = InMemoryKeylime()
    instance.addStore("test")
    val editTx = instance.edit("test")
    editTx.put(Seq(0.toByte), Seq(1.toByte))
    editTx.delete(Seq(0.toByte))
    val tx = instance.read("test")
    val res = Right(tx.get(Seq(0.toByte)))
    assertEquals(res, Right(None))
  }
  test("add and remove values to store") {
    val instance = InMemoryKeylime()
    instance.addStore("test")
    val editTx = instance.edit("test")
    editTx.put(Seq(0.toByte), Seq(1.toByte))
    editTx.delete(Seq(0.toByte))
    val tx = instance.read("test")
    val res = Right(tx.get(Seq(0.toByte)))
    assertEquals(res, Right(None))
  }
  test("prefix query on store") {
    val instance = InMemoryKeylime()
    instance.addStore("test")
    val editTx = instance.edit("test")
    editTx.put(Seq(0.toByte), Seq(6.toByte))
    editTx.put(Seq(0.toByte, 0.toByte), Seq(0.toByte))
    editTx.put(Seq(0.toByte, 1.toByte), Seq(1.toByte))
    editTx.put(Seq(1.toByte, 2.toByte), Seq(2.toByte))
    editTx.put(Seq(1.toByte, 3.toByte), Seq(3.toByte))
    editTx.put(Seq(1.toByte, 4.toByte), Seq(4.toByte))
    editTx.put(Seq(1.toByte, 5.toByte), Seq(5.toByte))
    editTx.put(Seq(1.toByte, 6.toByte), Seq(6.toByte))
    val tx = instance.read("test")
    val res = Right(tx.prefix(Seq(0.toByte)))
    assertEquals(res, Right(Seq(Seq(6.toByte), Seq(0.toByte), Seq(1.toByte))))
  }
}

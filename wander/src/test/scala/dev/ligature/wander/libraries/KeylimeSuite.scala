/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.WanderValue
import munit.FunSuite
import dev.ligature.wander.WanderSuiteCommonMode
// import dev.ligature.wander.host.openDefault
import jetbrains.exodus.env.Environment

//Note: This test suite is noisy and writes to the FS so it should be ignored unless you are testing it specifically.
@munit.IgnoreSuite
class StoreSuite extends WanderSuiteCommonMode {
  // val store = FunFixture[Environment](
  //   setup = { test =>
  //     openDefault() // TODO change to use a temp instance
  //   },
  //   teardown = { store =>
  //     store.clear()
  //     store.close()
  //   }
  // )

  // store.test("start with no stores") { store =>
  //   val script = "Keylime.stores ()"
  //   val result = WanderValue.Array(Seq())
  //   check(script, result, stdWithKeylime(store))
  // }
  // store.test("add store") { store =>
  //   val script = "Keylime.addStore \"test\", Keylime.stores ()"
  //   val result = WanderValue.Array(Seq(WanderValue.String("test")))
  //   check(script, result, stdWithKeylime(store))
  // }
  // store.test("remove store") { store =>
  //   val script = "Keylime.addStore \"test\", Keylime.removeStore \"test\", Keylime.stores ()"
  //   val result = WanderValue.Array(Seq())
  //   check(script, result, stdWithKeylime(store))
  // }
  // store.test("set and get value in a test store") { store =>
  //   val script =
  //     "Keylime.addStore \"test\", Keylime.set \"test\" 0x01 0x02, Keylime.get \"test\" 0x01"
  //   val result = WanderValue.Bytes(Seq(2.byteValue))
  //   check(script, result, stdWithKeylime(store))
  // }
  // store.test("get all entries for a store") { store =>
  //   val script = s"""
  //     Keylime.addStore "test",
  //     set = Keylime.set "test",
  //     set 0x01 0x02,
  //     set 0x02 0x03,
  //     set 0x03 0x04,
  //     Keylime.entries "test",
  //   """
  //   val result = WanderValue.Array(
  //     Seq(
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(1)), WanderValue.Bytes(Seq(2)))),
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(2)), WanderValue.Bytes(Seq(3)))),
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(3)), WanderValue.Bytes(Seq(4))))
  //     )
  //   )
  //   check(script, result, stdWithKeylime(store))
  // }
  // store.test("test setAll and then entries for a store") { store =>
  //   val script = s"""
  //     Keylime.addStore "test",
  //     Keylime.setAll "test" [[0x01, 0x02], [0x02, 0x03], [0x03, 0x04]],
  //     Keylime.entries "test",
  //   """
  //   val result = WanderValue.Array(
  //     Seq(
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(1)), WanderValue.Bytes(Seq(2)))),
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(2)), WanderValue.Bytes(Seq(3)))),
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(3)), WanderValue.Bytes(Seq(4))))
  //     )
  //   )
  //   check(script, result, stdWithKeylime(store))
  // }
  // store.test("test setAll and then delete and access entries for a store") { store =>
  //   val script = s"""
  //     Keylime.addStore "test",
  //     Keylime.setAll "test" [[0x01, 0x02], [0x02, 0x03], [0x03, 0x04]],
  //     Keylime.delete "test" 0x02,
  //     Keylime.entries "test",
  //   """
  //   val result = WanderValue.Array(
  //     Seq(
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(1)), WanderValue.Bytes(Seq(2)))),
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(3)), WanderValue.Bytes(Seq(4))))
  //     )
  //   )
  //   check(script, result, stdWithKeylime(store))
  // }
  // store.test("test setAll and then access a range") { store =>
  //   val script = s"""
  //     Keylime.addStore "test",
  //     Keylime.setAll "test" [[0x01, 0x02], [0x02, 0x03], [0x03, 0x04], [0x04, 0x05]],
  //     Keylime.range "test" 0x02 0x04,
  //   """
  //   val result = WanderValue.Array(
  //     Seq(
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(2)), WanderValue.Bytes(Seq(3)))),
  //       WanderValue.Array(Seq(WanderValue.Bytes(Seq(3)), WanderValue.Bytes(Seq(4))))
  //     )
  //   )
  //   check(script, result, stdWithKeylime(store))
  // }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.parser.{LigatureValue, Name}
import dev.ligature.{Identifier, StringLiteral}
import munit.FunSuite

class BindingsSuite extends FunSuite {
    val identifier = Name("test")
    val identifier2 = Name("test2")

    val value1 = LigatureValue(StringLiteral("this is a test"))
    val value2 = LigatureValue(StringLiteral("this is a test2"))
    val value3 = LigatureValue(StringLiteral("this is a test3"))

    test("add single value and read") {
        val binding = Bindings()
        binding.bind(identifier, value1)
        val res = binding.read(identifier)
        assertEquals(res, value1)
        intercept[Error] {
            binding.read(identifier2)
        }
    }

    test("test scoping") {
        val binding = Bindings()
        binding.bind(identifier, value1)
        assertEquals(binding.read(identifier), value1)

        binding.addScope()
        assertEquals(binding.read(identifier), value1)

        binding.bind(identifier, value2)
        binding.bind(identifier2, value3)
        assertEquals(binding.read(identifier), value2)
        assertEquals(binding.read(identifier2), value3)

        binding.removeScope()
        assertEquals(binding.read(identifier), value1)

        intercept[Error] {
            binding.read(identifier2)
        }

        intercept[Error] {
            binding.removeScope()
        }
    }
}

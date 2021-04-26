/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.readText
import kotlin.streams.toList

@OptIn(ExperimentalPathApi::class)
class WanderSuite : FunSpec() {
    private val wander = Wander()

    private fun readDirectory(path: String): List<Path> =
        Files.walk(Paths.get(path)).toList()

    init {
        test("primitives support") {
            val files = readDirectory("src/test/resources/primitives")
            val pairs = files.filter { it.extension == "wander" }.map {
                val text = it.readText()
                val result = wander.run(text)
                val expected = it.resolveSibling(it.fileName.toString().replace(".wander", ".result")).readText()
                result to expected
            }
            pairs.forAll {
                it.first shouldBe it.second
            }
        }
    }
}

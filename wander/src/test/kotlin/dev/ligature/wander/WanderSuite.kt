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

    data class TestResult(val expected: String, val commandResult: String, val queryResult: String)

    init {
        test("primitives support") {
            val files = readDirectory("src/test/resources/primitives")
            val results = files.filter { it.extension == "wander" }.map {
                val text = it.readText()
                val queryResult = wander.runQueryAndPrint(text)
                val commandResult = wander.runCommandAndPrint(text)
                val expected = it.resolveSibling(it.fileName.toString().replace(".wander", ".result")).readText()
                TestResult(expected, commandResult, queryResult)
            }
            results.forAll {
                it.commandResult shouldBe it.expected
                it.queryResult shouldBe it.expected
            }
        }
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.wander.writer.Writer
import io.kotest.core.spec.style.FunSpec
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
class WanderSpec : FunSpec() {
    private val wander = Wander()
    private val printer = Writer()

    private fun readDirectory(path: String): List<Path> =
        Files.walk(Paths.get(path)).toList()

    data class TestResult(val fileName: String, val expected: String, val result: String) {
        override fun toString(): String = fileName
    }

    private fun buildResults(path: String): List<TestResult> {
        val files = readDirectory(path)
        return files.filter { it.extension == "wander" }.map {
            val text = it.readText()
            val result = printer.write(wander.run(text))
            val expected = it.resolveSibling(it.fileName.toString().replace(".wander", ".result")).readText()
            TestResult(it.fileName.toString(), expected, result)
        }
    }

    init {
        test("primitives support") {
            buildResults("src/test/resources/primitives").forAll {
                it.result shouldBe it.expected
            }
        }

        test("assignment support") {
            buildResults("src/test/resources/assignment").forAll {
                it.result shouldBe it.expected
            }
        }
    }
}

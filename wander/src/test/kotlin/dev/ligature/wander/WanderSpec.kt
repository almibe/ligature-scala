/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import dev.ligature.inmemory.InMemoryLigature
import dev.ligature.wander.printer.Printer
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
    private val ligature = InMemoryLigature()
    private val wander = Wander(ligature)
    private val printer = Printer()

    private fun readDirectory(path: String): List<Path> =
        Files.walk(Paths.get(path)).toList()

    data class TestResult(val expected: String, val commandResult: String, val queryResult: String)

    private fun buildResults(path: String): List<TestResult> {
        val files = readDirectory(path)
        return files.filter { it.extension == "wander" }.map {
            val text = it.readText()
            val queryResult = printer.print(wander.runQuery(text))
            val commandResult = printer.print(wander.runCommand(text))
            val expected = it.resolveSibling(it.fileName.toString().replace(".wander", ".result")).readText()
            TestResult(expected, commandResult, queryResult)
        }
    }

    init {
        test("primitives support") {
            buildResults("src/test/resources/primitives").forAll {
                it.commandResult shouldBe it.expected
                it.queryResult shouldBe it.expected
            }
        }

//        test("assignment support") {
//            buildResults("src/test/resources/assignment").forAll {
//                it.commandResult shouldBe it.expected
//                it.queryResult shouldBe it.expected
//            }
//        }
    }
}

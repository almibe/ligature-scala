/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.xodus

import dev.ligature.Ligature
import dev.ligature.testsuite.LigatureTestSuite
import io.kotest.common.runBlocking
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import java.nio.file.*
import java.io.File

class LigatureXodusSpec: LigatureTestSuite() {
  lateinit var path: Path
  lateinit var ligatureInstance: Ligature

  override suspend fun beforeTest(testCase: TestCase) {
    path = Files.createTempDirectory("LigatureXodusTest")
  }

  override suspend fun afterTest(testCase: TestCase, testResult: TestResult) {
    fun deleteRecursively(file: File) {
      if (file.isDirectory) {
        file.listFiles().forEach { deleteRecursively(it) }
      }
      if (file.exists() && !file.delete()) {
        throw Exception("Unable to delete ${file.absolutePath}")
      }
    }

    runBlocking {
      ligatureInstance.close()
      deleteRecursively(path.toFile())
    }
  }

  override fun createLigature(): Ligature {
    ligatureInstance = XodusLigature(path.toFile())
    return ligatureInstance
  }
}

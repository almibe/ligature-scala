/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.lmdb

import dev.ligature.Ligature
import dev.ligature.testsuite.LigatureTestSuite
import java.nio.file._
import java.io.File

class LigatureLmdbSpec extends LigatureTestSuite() {
  var path: Path = null
  var ligatureInstance: Ligature = null

  override def beforeEach(context: BeforeEach): Unit = {
    path = Files.createTempDirectory("LigatureLMDBTest")
  }

  override def afterEach(context: AfterEach): Unit = {
    def deleteRecursively(file: File): Unit = {
      if (file.isDirectory) {
        file.listFiles.foreach(deleteRecursively)
      }
      if (file.exists && !file.delete) {
        throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
      }
    }

    ligatureInstance.close().unsafeRunSync()
    deleteRecursively(path.toFile())
  }

  override def createLigature = {
    ligatureInstance = LmdbLigature(path.toFile())
    ligatureInstance
  }
}

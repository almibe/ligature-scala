/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.http.xodus

import dev.ligature.xodus.XodusLigature
import dev.ligature.http.testsuite.LigatureHttpSuite
import dev.ligature.http.LigatureHttp
import cats.effect.unsafe.implicits.global

import java.io.File
import java.nio.file.{Files, Path}

class LigatureHttpXodusSuite extends LigatureHttpSuite {
  var path: Path = null
  var ligatureInstance: XodusLigature = null

  override def beforeEach(context: BeforeEach): Unit =
    path = Files.createTempDirectory("LigatureXodusTest")

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
    deleteRecursively(path.toFile)
  }

  override def createInstance(): LigatureHttp = {
    ligatureInstance = XodusLigature(path.toFile)
    LigatureHttp(ligatureInstance)
  }
}

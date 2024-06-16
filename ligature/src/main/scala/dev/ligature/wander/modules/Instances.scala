/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.modules

import dev.ligature.wander.*
import dev.ligature.wander.Environment
import java.nio.file.Paths
import dev.ligature.wander.libraries.loadFromPath

/** Create a Environment with core HostFunctions
  * and all of the libraries in WANDER_LIBS directory.
  */
def wanderLibs(): Environment =
  val path = Paths.get(sys.env("WANDER_LIBS"))
  loadFromPath(path) match {
    case Right(libs) => Environment(List(libs))
    case Left(_)     => ???
  }

/** Create the "default" environment for working with Wander.
  */
def std(): Environment =
  Environment()
    .bindVariable(Field("Array"), arrayModule)
    .bindVariable(Field("Bool"), boolModule)
    .bindVariable(Field("Bytes"), bytesModule)
    .bindVariable(Field("Core"), coreModule)
    .bindVariable(Field("Network"), networkModule)
    .bindVariable(Field("Int"), intModule)
    .bindVariable(Field("Statement"), statementModule)
    .bindVariable(Field("String"), stringModule)
    .bindVariable(Field("Test"), testingModule)
    .bindVariable(Field("Id"), idModule)
    .bindVariable(Field("Identifier"), identifierModule)
    .bindVariable(Field("InMemory"), inMemoryModule)
//    .bindVariable(Field("Xodus"), xodusModule)
    .bindVariable(Field("import"), importFunction)

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.modules

import dev.ligature.bend.*
import dev.ligature.bend.Environment
import dev.ligature.bend.libraries.ModuleLibrary
import dev.ligature.Ligature

/** Create the "default" environment for working with Bend.
  */
def std(libraries: List[ModuleLibrary] = List()): Environment =
  Environment(libraries)
    .bindVariable(Field("Array"), arrayModule)
    .bindVariable(Field("Bool"), boolModule)
    .bindVariable(Field("Bytes"), bytesModule)
    .bindVariable(Field("Core"), coreModule)
    .bindVariable(Field("Graph"), graphModule)
    .bindVariable(Field("Int"), intModule)
    .bindVariable(Field("Statement"), statementModule)
    .bindVariable(Field("String"), stringModule)
    .bindVariable(Field("Test"), testingModule)
    .bindVariable(Field("Id"), idModule)
    .bindVariable(Field("Identifier"), identifierModule)
    .bindVariable(Field("import"), importFunction)

def stdWithLigature(
    ligature: Ligature,
    libraries: List[ModuleLibrary] = List()
): Environment =
  std(libraries)
    .bindVariable(Field("Ligature"), createLigatureModule(ligature))

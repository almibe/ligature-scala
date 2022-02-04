/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

import dev.ligature.Identifier
import dev.ligature.Value

sealed trait DLigModel

case class PrefixDefinition(val name: String, val prefix: Identifier) extends DLigModel

sealed trait DLigIdentifiers extends DLigModel
case class RegularIdentifier(val identifier: Identifier) extends DLigIdentifiers
case class PrefixedIdentifier(val prefixName: String, val remainder: Identifier) extends DLigIdentifiers
case class GenIdentifier(val genIdentifier: String) extends DLigIdentifiers
case class GenPrefixedIdentifier(val prefixName: String, val genRemainder: String) extends DLigIdentifiers

object CopyCharacter extends DLigModel

case class DLigValue(val value: Value) extends DLigModel

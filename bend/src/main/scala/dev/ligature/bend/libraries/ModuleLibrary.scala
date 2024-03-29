/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.bend.libraries

import dev.ligature.bend.BendValue
import dev.ligature.bend.BendError

type ModuleId = String

trait ModuleLibrary:
  def lookup(id: ModuleId): Either[BendError, Option[BendValue.Module]]

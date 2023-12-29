/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.libraries

import dev.ligature.wander.WanderValue
import scala.collection.mutable.ListBuffer
import dev.ligature.wander.*
import dev.ligature.wander.Environment

/**
 * Create the "default" environment for working with Wander.
 */
def std(): Environment = {
  Environment()
    .addHostFunctions(boolLibrary)
    .addHostFunctions(coreLibrary)  
    .addHostFunctions(arrayLibrary)
    .addHostFunctions(testingHostFunctions)
    .addHostFunctions(intLibrary)
    .addHostFunctions(stringLibrary)
}

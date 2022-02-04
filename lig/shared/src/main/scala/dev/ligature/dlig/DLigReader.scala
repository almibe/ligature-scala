/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.dlig

import dev.ligature.Statement

def readDLig(input: String): Either[DLigError, List[Statement]] =
  for {
    parserResult <- parse(input)
    result <- interpret(parserResult)
  } yield result

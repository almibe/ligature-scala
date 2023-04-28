/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.command

def evalAll(input: Seq[Command]): Either[WanderError, List[WanderValue]] =
  ???

def eval(input: Seq[Command]): Either[WanderError, WanderValue] =
  if input.isEmpty then Right(WanderValue.Nothing)
  else
    input.last match
      case Command.AddStatement => ???
      case Command.Datasets => ???
      case Command.RemoveStatement => ???
      case Command.Statements => ???
      case Command.CreateDataset(datasetName) => ???
      case Command.Literal(value) => ???
      case Command.RemoveDataset(datasetName) => ???

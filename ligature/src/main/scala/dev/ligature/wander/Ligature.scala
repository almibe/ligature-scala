/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

case class LigatureError(val userMessage: String) extends Throwable(userMessage)

final case class Element(value: String)

final case class Extends(
  element: Element,
  concept: Element
)

final case class NotExtends(
  element: Element,
  concept: Element
)

final case class Role(
    first: Element,
    second: Element,
    role: Element
)

type Entry = Extends | NotExtends | Role

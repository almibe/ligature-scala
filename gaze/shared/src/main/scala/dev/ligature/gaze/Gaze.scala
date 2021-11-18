/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import scala.collection.mutable.ArrayBuffer

object Gaze {
    def from(text: String): Gaze[Char] = { //TODO eventually handle unicode better and make this Gaze[String]
        return new Gaze(text.toVector)
    }
}

class Gaze[I](private val input: Vector[I]) {
    private var offset: Int = 0

    def isComplete(): Boolean = {
        return this.offset >= this.input.length
    }

    def peek(): Option[I] = {
        if (this.isComplete()) {
            return None
        } else {
            return Some(this.input(this.offset))
        }
    }

    def next(): Option[I] = {
        if (this.isComplete()) {
            return None
        } else {
            val next = Some(this.input(this.offset))
            this.offset += 1
            return next
        }
    }

    def attempt[T, E](step: Step[I, E, T]): Either[E, T] = {
        val startOfThisLoop = this.offset
        val res = step(this)

        res match {
            case Right(_) => return res
            case Left(_) => {
                this.offset = startOfThisLoop
                return res
            }
        }
    }

    def attempt[T, E](steps: Step[I, E, T]*): Either[E, List[T]] = {
        val startOfThisLoop = this.offset
        val results: ArrayBuffer[T] = ArrayBuffer()

        for(step <- steps) {
            val res = step(this)
            res match {
                case Right(res) => results.append(res)
                case Left(e) => {
                    this.offset = startOfThisLoop
                    return Left(e)
                }
            }
        }
        Right(results.toList)
    }
}

type Step[I, E, T] = (gaze: Gaze[I]) => Either[E, T]

abstract class Nibbler[I, E, O] {
    def apply(gaze: Gaze[I]): Either[E,O]
    final def map[O, NO](input: O => NO): Nibbler[I, E, NO] = {
        ???
    }
}

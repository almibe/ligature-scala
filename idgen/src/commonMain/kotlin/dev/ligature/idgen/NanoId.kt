/** Copyright (c) 2017 The JNanoID Authors Copyright (c) 2017 Aventrix LLC
  * Copyright (c) 2017 Andrey Sitnik Copyright (c) 2022 Alex Michael Berry
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to
  * deal in the Software without restriction, including without limitation the
  * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
  * sell copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in
  * all copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
  * IN THE SOFTWARE.
  */

package dev.ligature.idgen

//import java.security.SecureRandom
//import java.util.Random
import kotlin.experimental.and
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log
import kotlin.random.Random

//NOTE: Just using kotlin.random.Random for now since Kotlin.js doesn't support SecureRandom yet.
//private val DEFAULT_NUMBER_GENERATOR = new SecureRandom()
private val DEFAULT_NUMBER_GENERATOR = Random

/** The default alphabet used by this class. Creates url-friendly NanoId Strings
  * using 64 unique symbols.
  */
private val DEFAULT_ALPHABET =
  "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    .toCharArray()

/** The default size used by this class. Creates NanoId Strings with slightly
  * more unique values than UUID v4.
  */
private val DEFAULT_SIZE = 21

/** Static factory to retrieve a url-friendly, pseudo randomly generated, NanoId
  * String.
  *
  * The generated NanoId String will have 21 symbols.
  *
  * The NanoId String is generated using a cryptographically strong pseudo
  * random number generator.
  *
  * @return
  *   A randomly generated NanoId String.
  */
fun randomNanoId(): String =
  randomNanoId(DEFAULT_NUMBER_GENERATOR, DEFAULT_ALPHABET, DEFAULT_SIZE)

/** Static factory to retrieve a NanoId String.
  *
  * The string is generated using the given random number generator.
  *
  * @param random
  *   The random number generator.
  * @param alphabet
  *   The symbols used in the NanoId String.
  * @param size
  *   The number of symbols in the NanoId String.
  * @return
  *   A randomly generated NanoId String.
  */
fun randomNanoId(random: Random, alphabet: CharArray, size: Int): String {
  if (alphabet.isEmpty() || alphabet.size >= 256) {
    throw IllegalArgumentException(
      "alphabet must contain between 1 and 255 symbols."
    )
  }

  if (size <= 0) {
    throw IllegalArgumentException("size must be greater than zero.")
  }

  val mask: Int =
    (2 shl floor(ln((alphabet.size - 1).toDouble()) / ln(2.toDouble())).toInt()) - 1
  val step: Int = ceil(1.6 * mask * size / alphabet.size).toInt()
  val idBuilder = StringBuilder()

  fun intToByteArray (i: Int) : ByteArray =
    ByteArray(4) {x -> (i.toLong() shr (x*8)).toByte()}

  while (true) {
    val bytes = intToByteArray(step)
    random.nextBytes(bytes)
    for (i in 0..step) {
      val alphabetIndex = bytes[i] and mask.toByte() //TODO not sure if this is right
      if (alphabetIndex < alphabet.size) {
        idBuilder.append(alphabet[alphabetIndex.toInt()])
        if (idBuilder.length == size) {
          return idBuilder.toString()
        }
      }
    }
  }
}

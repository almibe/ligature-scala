// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

// package dev.ligature.lig

// import dev.ligature.*
// import dev.ligature.gaze.*
// import dev.ligature.idgen.genId
// import dev.ligature.lig.LigNibblers.{numberNibbler, stringContentNibbler, whiteSpaceAndNewLineNibbler, whiteSpaceNibbler}
// import dev.ligature.lig.{createWord, parseIntegerValue, parseStringValue}

// import scala.collection.mutable.{ArrayBuffer, HashMap}

// case class LigError(message: String)

// //def read(input: String): Either[LigError, List[Triple]] = {
// //  val gaze = Gaze.from(input)
// //  val triples: ArrayBuffer[Triple] = ArrayBuffer()
// //  var continue = true
// //  while (continue && !gaze.isComplete) {
// //    gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
// //    parseTriple(gaze) match {
// //      case Left(resTriple)  => return Left(resTriple)
// //      case Right(resTriple) => triples.append(resTriple)
// //    }
// //    val check = gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
// //    if (check.isDefined && !gaze.isComplete) {
// //      continue = true
// //    } else {
// //      continue = false
// //    }
// //  }
// //  Right(triples.toList)
// //}

// def read(input: String): Either[LigError, List[Triple]] = {
//   val gaze = Gaze.from(input)
//   // val model: ArrayBuffer[DLigModel] = ArrayBuffer()
//   for {
//     prefixes <- parsePrefixes(gaze)
//     triples <- parseTriples(gaze, prefixes)
//   } yield triples
// //   var continue = true
// //   while (continue && !gaze.isComplete) {
// //     gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
// //     parseTriple(gaze) match {
// //       case Left(resTriple)  => return Left(resTriple)
// //       case Right(resTriple) => triples.append(resTriple)
// //     }
// //     val check = gaze.attempt(optional(whiteSpaceAndNewLineNibbler))
// //     if (check.isDefined && !gaze.isComplete) {
// //       continue = true
// //     } else {
// //       continue = false
// //     }
// //   }
// //  return Right(model.toList)
// }

// //def parseTriple(gaze: Gaze[Char]): Either[LigError, Triple] =
// //  for {
// //    _ <- gaze
// //      .attempt(optional(whiteSpaceAndNewLineNibbler))
// //      .toRight(LigError("Error parsing optional whitespace before Triple"))
// //    entity <- parseWord(gaze)
// //    _ <- gaze
// //      .attempt(whiteSpaceNibbler)
// //      .toRight(LigError("Error parsing whitespace after Entity"))
// //    attribute <- parseWord(gaze)
// //    _ <- gaze
// //      .attempt(whiteSpaceNibbler)
// //      .toRight(LigError("Error parsing whitespace after Attribute"))
// //    value <- parseValue(gaze)
// //    _ <- gaze
// //      .attempt(optional(whiteSpaceAndNewLineNibbler))
// //      .toRight(LigError(""))
// //  } yield Triple(entity, attribute, value)

// def createWord(id: String): Either[LigError, Word] =
//   Word
//     .fromString(id)
//     .left
//     .map(_ => LigError("Invalid Word Id - $id"))

// def parseIntegerValue(gaze: Gaze[Char]): Either[LigError, LigatureValue.IntegerValue] =
//   gaze.attempt(numberNibbler) match {
//     case None => Left(LigError("Could not parse Integer."))
//     case Some(i) =>
//       Right(LigatureValue.IntegerValue(i.mkString.toLong)) // TODO toLong can throw
//   }

// def parseStringValue(gaze: Gaze[Char]): Either[LigError, LigatureValue.StringValue] = {
//   val res = gaze.attempt(takeAllGrouped(takeString("\""), stringContentNibbler))

//   res match {
//     case None      => Left(LigError("Could not parse String."))
//     case Some(res) => Right(LigatureValue.StringValue(res(1).mkString))
//   }
// }

// def parsePrefixes(gaze: Gaze[Char]): Either[LigError, Map[String, String]] = {
//   val result = HashMap[String, String]()
//   while (!gaze.isComplete)
//     parsePrefix(gaze) match {
//       case Left(err)   => return Left(err)
//       case Right(None) => return Right(result.toMap)
//       case Right(Some(pair)) =>
//         if (result.contains(pair._1)) {
//           return Left(LigError(s"Duplicate Prefix Name: ${pair._1}"))
//         } else {
//           result.addOne(pair)
//         }
//     }
//   Right(result.toMap)
// }

// //TODO: this function never returns an Error so if there is a malformed prefix it won't be caught until we try to read a Triple
// def parsePrefix(
//     gaze: Gaze[Char]
// ): Either[LigError, Option[(String, String)]] = {
//   val parseResult = gaze.attempt(
//     takeAllGrouped(
//       takeString("prefix"),
//       whiteSpaceNibbler,
//       LigNibblers.prefixNameNibbler,
//       whiteSpaceNibbler,
//       takeString("="),
//       whiteSpaceNibbler,
//       takeWhile { c =>
//         "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)
//       }
//     )
//   )
//   parseResult match {
//     case None => Right(None)
//     case Some(p) =>
//       val prefixName = p(2).mkString
//       val prefixValue = p(6).mkString
//       Right(Some((prefixName, prefixValue)))
//   }
// }

// def parseTriples(
//     gaze: Gaze[Char],
//     prefixes: Map[String, String]
// ): Either[LigError, List[Triple]] = {
//   val triples = ArrayBuffer[Triple]()
//   var lastTriple: Option[Triple] = None
//   while (!gaze.isComplete)
//     parseTriple(gaze, prefixes, lastTriple) match {
//       case Left(err) => return Left(err)
//       case Right(triple) =>
//         lastTriple = Some(triple)
//         triples.addOne(triple)
//     }
//   Right(triples.toList)
// }

// def parseTriple(
//     gaze: Gaze[Char],
//     prefixes: Map[String, String],
//     lastTriple: Option[Triple]
// ): Either[LigError, Triple] =
//   for {
//     _ <- gaze
//       .attempt(optional(whiteSpaceAndNewLineNibbler))
//       .toRight(LigError("Error parsing optional whitespace before Triple"))
//     entity <- parseWord(gaze, prefixes, lastEntity(lastTriple))
//     _ <- gaze
//       .attempt(whiteSpaceNibbler)
//       .toRight(LigError("Error parsing whitespace after Entity"))
//     attribute <- parseWord(gaze, prefixes, lastAttribute(lastTriple))
//     _ <- gaze
//       .attempt(whiteSpaceNibbler)
//       .toRight(LigError("Error parsing whitespace after Attribute"))
//     value <- parseValue(gaze, prefixes, lastValue(lastTriple))
//     _ <- gaze
//       .attempt(optional(whiteSpaceAndNewLineNibbler))
//       .toRight(LigError(""))
//   } yield Triple(entity, attribute, value)

// def lastEntity(lastTriple: Option[Triple]): Option[Word] =
//   lastTriple match {
//     case None            => None
//     case Some(triple) => Some(triple.entity)
//   }

// def lastAttribute(lastTriple: Option[Triple]): Option[Word] =
//   lastTriple match {
//     case None            => None
//     case Some(triple) => Some(triple.attribute)
//   }

// def lastValue(lastTriple: Option[Triple]): Option[Value] =
//   lastTriple match {
//     case None            => None
//     case Some(triple) => Some(triple.value)
//   }

// def parseWord(
//     gaze: Gaze[Char],
//     prefixes: Map[String, String],
//     lastWord: Option[Word]
// ): Either[LigError, Word] = {
//   // attempt copy character
//   val copyChar = gaze.attempt(LigNibblers.copyNibbler)
//   if (copyChar.isDefined) {
//     lastWord match {
//       case None =>
//         return Left(
//           LigError("Can't Use Copy Character Without Existing Instance.")
//         )
//       case Some(id) => return Right(id)
//     }
//   }
//   val idGenId = gaze.attempt(
//     between(
//       takeString("<"),
//       repeat(
//         takeFirst(
//           takeWhile(c => "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)),
//           takeString("{}")
//         )
//       ),
//       takeString(">")
//     )
//   )
//   if (idGenId.isDefined) {
//     handleIdGenId(idGenId.get.mkString) match {
//       case Right(id) => return Right(id)
//       case Left(err) => return Left(err)
//     }
//   }
//   val prefixedGenId = gaze.attempt(
//     takeAllGrouped(
//       LigNibblers.prefixNameNibbler,
//       takeString(":"),
//       repeat(
//         takeFirst(
//           takeWhile(c => "[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;%=]".r.matches(c.toString)),
//           takeString("{}")
//         )
//       )
//     )
//   )
//   if (prefixedGenId.isDefined) {
//     handlePrefixedGenId(prefixedGenId.get, prefixes) match {
//       case Right(id) => return Right(id)
//       case Left(err) => return Left(err)
//     }
//   }

//   Left(LigError(s"Could not match Word. ${gaze.location}"))
// }

// def handleIdGenId(input: String): Either[LigError, Word] =
//   Word.fromString(genIdId(input)).left.map(err => LigError(err.userMessage))

// def genIdId(input: String): String = {
//   val itr = input.toCharArray.iterator
//   val sb = StringBuilder()
//   while (itr.hasNext)
//     itr.next match {
//       case c: Char if c == '{' =>
//         itr.next // eat }, TODO should probably assert here
//         sb.append(genId())
//       case c: Char => sb.append(c)
//     }
//   sb.toString
// }

// def handlePrefixedId(
//     input: Seq[Seq[Char]],
//     prefixes: Map[String, String]
// ): Either[LigError, Word] = {
//   val prefixName = input(0).mkString
//   prefixes.get(prefixName) match {
//     case None => Left(LigError(s"Prefix Name $prefixName, Doesn't Exist."))
//     case Some(prefixValue) =>
//       val postfix = input(2).mkString
//       Word
//         .fromString(prefixValue + postfix)
//         .left
//         .map(err => LigError(err.userMessage))
//   }
// }

// def handlePrefixedGenId(
//     input: Seq[Seq[Char]],
//     prefixes: Map[String, String]
// ): Either[LigError, Word] = {
//   val prefixName = input(0).mkString
//   prefixes.get(prefixName) match {
//     case None => Left(LigError(s"Prefix Name $prefixName, Doesn't Exist."))
//     case Some(prefixValue) =>
//       val postfix = input(2).mkString
//       Word
//         .fromString(genIdId(prefixValue + postfix))
//         .left
//         .map(err => LigError(err.userMessage))
//   }
// }

// def parseValue(
//     gaze: Gaze[Char],
//     prefixes: Map[String, String],
//     lastValue: Option[Value]
// ): Either[LigError, Value] = {
//   // attempt copy character
//   val copyChar = gaze.attempt(LigNibblers.copyNibbler)
//   if (copyChar.isDefined) {
//     lastValue match {
//       case None =>
//         return Left(
//           LigError("Can't Use Copy Character Without Existing Instance.")
//         )
//       case Some(id) => return Right(id)
//     }
//   }

//   val entityRes = parseWord(
//     gaze,
//     prefixes,
//     None
//   ) // can be None since copy character has been checked for
//   if (entityRes.isRight) return entityRes

//   val integerRes = parseIntegerValue(gaze)
//   if (integerRes.isRight)
//     return integerRes.left.map(err => LigError(err.message))

//   val stringRes = parseStringValue(gaze)
//   if (stringRes.isRight)
//     return stringRes.left.map(err => LigError(err.message))

//   Left(LigError("Unsupported Value."))
// }

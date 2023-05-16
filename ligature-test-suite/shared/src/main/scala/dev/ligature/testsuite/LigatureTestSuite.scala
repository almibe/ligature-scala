/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import cats.effect.unsafe.implicits.global
import cats.effect.IO
import dev.ligature._
import munit._
import cats.effect.std.Console
import cats.effect.kernel.Ref
import fs2.Stream
import cats.effect.kernel.Resource

abstract class LigatureTestSuite extends CatsEffectSuite {
  def createLigature: Resource[IO, Ligature]

  val testDataset = Dataset.fromString("test/test").getOrElse(???)
  val testDataset2 = Dataset.fromString("test/test2").getOrElse(???)
  val testDataset3 = Dataset.fromString("test3/test").getOrElse(???)
  val a = Identifier.fromString("a").getOrElse(???)
  val b = Identifier.fromString("b").getOrElse(???)
  val entity1 = Identifier.fromString("a").getOrElse(???)
  val entity2 = Identifier.fromString("b").getOrElse(???)
  val entity3 = Identifier.fromString("c").getOrElse(???)

  test("create and close store".only) {
    createLigature.use { instance => 
      assertIO(instance.allDatasets().compile.toList, List())
    }
  }

//   test("creating a new dataset".only) {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       res <- instance.allDatasets().compile.toList
//     } yield res
//     assertIO(res, List(testDataset))
//   }

//   test("check if datasets exist") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       exists1 <- instance.datasetExists(testDataset)
//       exists2 <- instance.datasetExists(testDataset2)
//     } yield (exists1, exists2)
//     assertIO(res, (true, false))
//   }

//   test("match datasets prefix exact") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       res1 <- instance.matchDatasetsPrefix("test/test").compile.toList
//     } yield (res1.length)
//     assertIO(res, (1))
//   }

//   test("match datasets prefix") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.createDataset(testDataset2)
//       _ <- instance.createDataset(testDataset3)
//       res1 <- instance.matchDatasetsPrefix("test").compile.toList
//       res2 <- instance.matchDatasetsPrefix("test/").compile.toList
//       res3 <- instance.matchDatasetsPrefix("snoo").compile.toList
//     } yield (res1.length, res2.length, res3.length)
//     assertIO(res, (3, 2, 0))
//   }

//   test("match datasets range") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(Dataset.fromString("a").getOrElse(???))
//       _ <- instance.createDataset(Dataset.fromString("app").getOrElse(???))
//       _ <- instance.createDataset(Dataset.fromString("b").getOrElse(???))
//       _ <- instance.createDataset(Dataset.fromString("be").getOrElse(???))
//       _ <- instance.createDataset(Dataset.fromString("bee").getOrElse(???))
//       _ <- instance.createDataset(
//         Dataset.fromString("test1/test").getOrElse(???)
//       )
//       _ <- instance.createDataset(
//         Dataset.fromString("test2/test2").getOrElse(???)
//       )
//       _ <- instance.createDataset(
//         Dataset.fromString("test3/test").getOrElse(???)
//       )
//       _ <- instance.createDataset(Dataset.fromString("test4").getOrElse(???))
//       _ <- instance.createDataset(Dataset.fromString("z").getOrElse(???))
//       res <- instance.allDatasets().compile.toList
//       res1 <- instance.matchDatasetsRange("a", "b").compile.toList
//       res2 <- instance.matchDatasetsRange("be", "test3").compile.toList
//       res3 <- instance.matchDatasetsRange("snoo", "zz").compile.toList
//     } yield (res1.length, res2.length, res3.length)
//     assertIO(res, (2, 4, 5)) // TODO check instances not just counts
//   }

//   test("create and delete new dataset") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.deleteDataset(testDataset)
//       _ <- instance.deleteDataset(testDataset2)
//       res <- instance.allDatasets().compile.toList
//     } yield res
//     assertIO(res, List())
//   }

//   test("new datasets should be empty") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       res <- instance.allStatements(testDataset).compile.toList
//     } yield res
//     assertIO(res, List())
//   }

//   test("adding statements to datasets") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.addStatements(testDataset, Stream.emits(Seq(
//         Statement(entity1, a, entity2),
//         Statement(entity1, a, entity2), // dupe
//         Statement(entity1, a, entity3),
//       )))
//       statements <- instance.allStatements(testDataset).compile.toList
//     } yield statements.toSet
//     assertIO(
//       res,
//       Set(
//         Statement(entity1, a, entity2),
//         Statement(entity1, a, entity3)
//       )
//     )
//   }

//   test("add Statement with IntegerLiteral Value") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.addStatements(testDataset, Stream.emits(Seq(
//           Statement(entity1, a, entity2),
//           Statement(entity1, a, LigatureLiteral.IntegerLiteral(100)),
//           Statement(entity1, a, LigatureLiteral.IntegerLiteral(101)),
//           Statement(entity1, a, LigatureLiteral.IntegerLiteral(100)),
//           Statement(entity2, a, LigatureLiteral.IntegerLiteral(-243729)))))
//       statements <- instance.allStatements(testDataset).compile.toList
//     } yield statements.toSet
//     assertIO(
//       res,
//       Set(
//         Statement(entity1, a, entity2),
//         Statement(entity1, a, LigatureLiteral.IntegerLiteral(100)),
//         Statement(entity1, a, LigatureLiteral.IntegerLiteral(101)),
//         Statement(entity2, a, LigatureLiteral.IntegerLiteral(-243729))
//       )
//     )
//   }

//   test("add Statement with StringLiteral Value") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.addStatements(testDataset, Stream.emits(Seq(
//           Statement(entity1, a, entity2),
//           Statement(entity1, a, LigatureLiteral.StringLiteral("text")),
//           Statement(entity1, a, LigatureLiteral.StringLiteral("text2")),
//           Statement(entity1, a, LigatureLiteral.StringLiteral("text")),
//           Statement(entity2, a, LigatureLiteral.StringLiteral("text")))))
//       statements <- instance.allStatements(testDataset).compile.toList
//     } yield statements.toSet
//     assertIO(
//       res,
//       Set(
//         Statement(entity1, a, entity2),
//         Statement(entity1, a, LigatureLiteral.StringLiteral("text")),
//         Statement(entity1, a, LigatureLiteral.StringLiteral("text2")),
//         Statement(entity2, a, LigatureLiteral.StringLiteral("text"))
//       )
//     )
//   }

//   // test("new identifiers") {
//   //   val instance = createLigature
//   //   val res = for {
//   //     _ <- instance.createDataset(testDataset)
//   //     _ <- instance.write(testDataset) { tx =>
//   //       for {
//   //         entity <- tx.newIdentifier("entity-")
//   //         attribute <- tx.newIdentifier("attribute-")
//   //         value <- tx.newIdentifier("value-")
//   //         _ <- tx.addStatement(Statement(entity, attribute, value))
//   //       } yield IO.unit
//   //     }
//   //     statements <- instance.query(testDataset) { tx =>
//   //       tx.allStatements().compile.toList
//   //     }
//   //   } yield statements.head
//   //   res.map { it =>
//   //     assert(it.entity.name.startsWith("entity-"))
//   //     assert(it.attribute.name.startsWith("attribute-"))
//   //     it.value match {
//   //       case Identifier(id) => assert(id.startsWith("value-"))
//   //       case _              => assert(false)
//   //     }
//   //   }
//   // }

//   test("removing statements from datasets") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.addStatements(testDataset, Stream.emit(Statement(entity1, a, entity2)))
//       _ <- instance.removeStatements(testDataset, Stream.emits(Seq(Statement(entity1, a, entity2), Statement(entity1, a, entity2))))
//       statements <- instance.allStatements(testDataset).compile.toList
//     } yield statements
//     assertIO(res, List())
//   }

//   test("removing statements from datasets with dupe") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.addStatements(testDataset, Stream.emits(Seq(
//         Statement(entity1, a, entity2),
//         Statement(entity3, a, entity2),
//         Statement(entity1, a, entity2),
//         Statement(entity1, a, entity2))))
//       statements <- instance.allStatements(testDataset).compile.toList
//     } yield statements
//     assertIO(res, List(Statement(entity3, a, entity2)))
//   }

//   test("removing statements from datasets with duplicate Strings") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.addStatements(testDataset, Stream.emits(Seq(
//         Statement(entity1, a, LigatureLiteral.StringLiteral("hello")),
//         Statement(entity1, a, LigatureLiteral.StringLiteral("hello")),
//         Statement(entity2, a, LigatureLiteral.StringLiteral("hello")))))
//       _ <- instance.removeStatements(testDataset, Stream.emit(Statement(entity1, a, LigatureLiteral.StringLiteral("hello"))))
//       statements <- instance.allStatements(testDataset).compile.toList
//     } yield statements.toSet
//     assertIO(res, Set(Statement(entity2, a, LigatureLiteral.StringLiteral("hello"))))
//   }

//   // test("allow canceling WriteTx by throwing exception") {
//   //     val res = createLigature.instance.use { instance  =>
//   //         for {
//   //             _ <- instance.createDataset(testDataset)
//   //             _ <- instance.write(testDataset).use { tx =>
//   //                 for {
//   //                     _ <- tx.addStatement(Statement(entity1, a, entity2))
//   //                     _ <- tx.cancel()
//   //                 } yield ()
//   //             }
//   //             _ <- instance.write(testDataset).use { tx =>
//   //                 for {
//   //                     _ <- tx.addStatement(Statement(entity2, a, entity3))
//   //                     _ <- tx.addStatement(Statement(entity3, a, entity2))
//   //                 } yield ()
//   //             }
//   //             statements <- instance.query(testDataset).use { tx =>
//   //                 tx.allStatements().compile.toList
//   //             }
//   //         } yield statements
//   //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
//   //     assertEquals(res, Set(
//   //         Statement(entity2, a, entity3),
//   //         Statement(entity3, a, entity2)))
//   // }

//   test("matching statements in datasets") {
//     val instance = createLigature
//     val res = for {
//       _ <- instance.createDataset(testDataset)
//       _ <- instance.addStatements(testDataset, Stream.emits(Seq(
//         Statement(entity1, a, LigatureLiteral.StringLiteral("Hello")),
//         Statement(entity2, a, entity1),
//         Statement(entity2, a, entity3),
//         Statement(entity3, b, entity2),
//         Statement(entity3, b, LigatureLiteral.StringLiteral("Hello")))))
//       res <- instance.query(testDataset) { tx =>
//         for {
//           all <- tx.matchStatements().compile.toList
//           as <- tx.matchStatements(None, Some(a)).compile.toList
//           hellos <- tx
//             .matchStatements(None, None, Some(LigatureLiteral.StringLiteral("Hello")))
//             .compile
//             .toList
//           helloa <- tx
//             .matchStatements(None, Some(a), Some(LigatureLiteral.StringLiteral("Hello")))
//             .compile
//             .toList
//         } yield (all.toSet, as.toSet, hellos.toSet, helloa.toSet)
//       }
//     } yield res
//     res.map { (all, as, hellos, helloa) =>
//       assertEquals(
//         all,
//         Set(
//           Statement(entity1, a, LigatureLiteral.StringLiteral("Hello")),
//           Statement(entity2, a, entity1),
//           Statement(entity2, a, entity3),
//           Statement(entity3, b, entity2),
//           Statement(entity3, b, LigatureLiteral.StringLiteral("Hello"))
//         )
//       )
//       assertEquals(
//         as,
//         Set(
//           Statement(entity1, a, LigatureLiteral.StringLiteral("Hello")),
//           Statement(entity2, a, entity1),
//           Statement(entity2, a, entity3)
//         )
//       )
//       assertEquals(
//         hellos,
//         Set(
//           Statement(entity1, a, LigatureLiteral.StringLiteral("Hello")),
//           Statement(entity3, b, LigatureLiteral.StringLiteral("Hello"))
//         )
//       )
//       assertEquals(
//         helloa,
//         Set(
//           Statement(entity1, a, LigatureLiteral.StringLiteral("Hello"))
//         )
//       )
//     }
//   }

// //  test("matching statements with literals and ranges in datasets") {
// //    val instance = createLigature
// //    val res = for {
// //      _ <- instance.createDataset(testDataset)
// //      _ <- instance.write(testDataset) { tx =>
// //        for {
// //          _ <- tx.addStatement(Statement(entity1, a, entity2))
// //          _ <- tx
// //            .addStatement(Statement(entity1, b, StringLiteral("add")))
// //          _ <- tx
// //            .addStatement(Statement(entity1, a, IntegerLiteral(5L)))
// //          _ <- tx
// //            .addStatement(Statement(entity2, a, IntegerLiteral(3L)))
// //          _ <- tx.addStatement(
// //            Statement(entity2, a, StringLiteral("divide"))
// //          )
// //          _ <- tx.addStatement(Statement(entity2, b, entity3))
// //          _ <- tx
// //            .addStatement(Statement(entity3, a, IntegerLiteral(7L)))
// //          _ <- tx.addStatement(
// //            Statement(entity3, b, StringLiteral("decimal"))
// //          )
// //        } yield ()
// //      }
// //      res <- instance.query(testDataset) { tx =>
// //        for {
// //          res1 <- tx
// //            .matchStatementsRange(None, None, StringLiteralRange("a", "dd"))
// //            .compile
// //            .toList
// //          res2 <- tx
// //            .matchStatementsRange(None, None, IntegerLiteralRange(3, 6))
// //            .compile
// //            .toList
// //          res3 <- tx
// //            .matchStatementsRange(None, Some(b), StringLiteralRange("ae", "df"))
// //            .compile
// //            .toList
// //        } yield (res1.toSet, res2.toSet, res3.toSet)
// //      }
// //    } yield res
// //    res.map { (res1, res2, res3) =>
// //      assertEquals(
// //        res1,
// //        Set(
// //          Statement(entity1, b, StringLiteral("add"))
// //        )
// //      )
// //      assertEquals(
// //        res2,
// //        Set(
// //          Statement(entity2, a, IntegerLiteral(3L)),
// //          Statement(entity1, a, IntegerLiteral(5L))
// //        )
// //      )
// //      assertEquals(
// //        res3,
// //        Set(
// //          Statement(entity3, b, StringLiteral("decimal"))
// //        )
// //      )
// //    }
// //  }
}

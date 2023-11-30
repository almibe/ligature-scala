/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature._
import munit.FunSuite

abstract class LigatureTestSuite extends FunSuite {
  def createLigature(): Ligature

  val testDataset = Dataset("test/test")
  val testDataset2 = Dataset("test/test2")
  val testDataset3 = Dataset("test3/test")
  val a = Label("a")
  val b = Label("b")
  val label1 = Label("a")
  val label2 = Label("b")
  val label3 = Label("c")

  test("create and close store") {
    val instance = createLigature()
    assertEquals(instance.allDatasets().toList, List())
  }

  test("creating a new dataset") {
    val instance = createLigature()
    instance.createDataset(testDataset)
    val res = instance.allDatasets().toList
    assertEquals(res, List(testDataset))
  }

  test("check if datasets exist") {
    val instance = createLigature()
    instance.createDataset(testDataset)
    val exists1 = instance.datasetExists(testDataset)
    val exists2 = instance.datasetExists(testDataset2)
    val res = (exists1, exists2)
    assertEquals(res, (true, false))
  }

//   test("match datasets prefix exact") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         res1 <- instance.matchDatasetsPrefix("test/test").compile.toList
//       } yield res1.length
//       assertIO(res, 1)
//     }
//   }

//   test("match datasets prefix") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.createDataset(testDataset2)
//         _ <- instance.createDataset(testDataset3)
//         res1 <- instance.matchDatasetsPrefix("test").compile.toList
//         res2 <- instance.matchDatasetsPrefix("test/").compile.toList
//         res3 <- instance.matchDatasetsPrefix("snoo").compile.toList
//       } yield (res1.length, res2.length, res3.length)
//       assertIO(res, (3, 2, 0))
//     }
//   }

//   test("match datasets range") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(Dataset("a"))
//         _ <- instance.createDataset(Dataset("app"))
//         _ <- instance.createDataset(Dataset("b"))
//         _ <- instance.createDataset(Dataset("be"))
//         _ <- instance.createDataset(Dataset("bee"))
//         _ <- instance.createDataset(
//           Dataset("test1/test")
//         )
//         _ <- instance.createDataset(
//           Dataset("test2/test2")
//         )
//         _ <- instance.createDataset(
//           Dataset("test3/test")
//         )
//         _ <- instance.createDataset(Dataset("test4"))
//         _ <- instance.createDataset(Dataset("z"))
//         res <- instance.allDatasets().compile.toList
//         res1 <- instance.matchDatasetsRange("a", "b").compile.toList
//         res2 <- instance.matchDatasetsRange("be", "test3").compile.toList
//         res3 <- instance.matchDatasetsRange("snoo", "zz").compile.toList
//       } yield (res1.length, res2.length, res3.length)
//       assertIO(res, (2, 4, 5)) // TODO check instances not just counts
//     }
//   }

//   test("create and delete new dataset") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.deleteDataset(testDataset)
//         _ <- instance.deleteDataset(testDataset2)
//         res <- instance.allDatasets().compile.toList
//       } yield res
//       assertIO(res, List())
//     }
//   }

//   test("new datasets should be empty") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         res <- instance.allEdges(testDataset).compile.toList
//       } yield res
//       assertIO(res, List())
//     }
//   }

//   test("adding statements to datasets") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.addEdges(
//           testDataset,
//           Stream.emits(
//             Seq(
//               Edge(label1, a, label2),
//               Edge(label1, a, label2), // dupe
//               Edge(label1, a, label3)
//             )
//           )
//         )
//         statements <- instance.allEdges(testDataset).compile.toList
//       } yield statements.toSet
//       assertIO(
//         res,
//         Set(
//           Edge(label1, a, label2),
//           Edge(label1, a, label3)
//         )
//       )
//     }
//   }

//   test("add Edge with IntegerLiteral Value") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.addEdges(
//           testDataset,
//           Stream.emits(
//             Seq(
//               Edge(label1, a, label2),
//               Edge(label1, a, LigatureLiteral.IntegerLiteral(100)),
//               Edge(label1, a, LigatureLiteral.IntegerLiteral(101)),
//               Edge(label1, a, LigatureLiteral.IntegerLiteral(100)),
//               Edge(label2, a, LigatureLiteral.IntegerLiteral(-243729))
//             )
//           )
//         )
//         statements <- instance.allEdges(testDataset).compile.toList
//       } yield statements.toSet
//       assertIO(
//         res,
//         Set(
//           Edge(label1, a, label2),
//           Edge(label1, a, LigatureLiteral.IntegerLiteral(100)),
//           Edge(label1, a, LigatureLiteral.IntegerLiteral(101)),
//           Edge(label2, a, LigatureLiteral.IntegerLiteral(-243729))
//         )
//       )
//     }
//   }

//   test("add Edge with StringLiteral Value") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.addEdges(
//           testDataset,
//           Stream.emits(
//             Seq(
//               Edge(label1, a, label2),
//               Edge(label1, a, LigatureLiteral.StringLiteral("text")),
//               Edge(label1, a, LigatureLiteral.StringLiteral("text2")),
//               Edge(label1, a, LigatureLiteral.StringLiteral("text")),
//               Edge(label2, a, LigatureLiteral.StringLiteral("text"))
//             )
//           )
//         )
//         statements <- instance.allEdges(testDataset).compile.toList
//       } yield statements.toSet
//       assertIO(
//         res,
//         Set(
//           Edge(label1, a, label2),
//           Edge(label1, a, LigatureLiteral.StringLiteral("text")),
//           Edge(label1, a, LigatureLiteral.StringLiteral("text2")),
//           Edge(label2, a, LigatureLiteral.StringLiteral("text"))
//         )
//       )
//     }
//   }

// //   // test("new identifiers") {
// //   //   val instance = createLigature
// //   //   val res = for {
// //   //     _ <- instance.createDataset(testDataset)
// //   //     _ <- instance.write(testDataset) { tx =>
// //   //       for {
// //   //         entity <- tx.newLabel("entity-")
// //   //         attribute <- tx.newLabel("attribute-")
// //   //         value <- tx.newLabel("value-")
// //   //         _ <- tx.addEdge(Edge(entity, attribute, value))
// //   //       } yield IO.unit
// //   //     }
// //   //     statements <- instance.query(testDataset) { tx =>
// //   //       tx.allEdges().compile.toList
// //   //     }
// //   //   } yield statements.head
// //   //   res.map { it =>
// //   //     assert(it.entity.name.startsWith("entity-"))
// //   //     assert(it.attribute.name.startsWith("attribute-"))
// //   //     it.value match {
// //   //       case Label(id) => assert(id.startsWith("value-"))
// //   //       case _              => assert(false)
// //   //     }
// //   //   }
// //   // }

//   test("removing statements from datasets") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.addEdges(testDataset, Stream.emit(Edge(label1, a, label2)))
//         _ <- instance.removeEdges(
//           testDataset,
//           Stream.emits(Seq(Edge(label1, a, label2), Edge(label1, a, label2)))
//         )
//         statements <- instance.allEdges(testDataset).compile.toList
//       } yield statements
//       assertIO(res, List())
//     }
//   }

//   test("removing statements from datasets with dupe") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.addEdges(
//           testDataset,
//           Stream.emits(
//             Seq(Edge(label1, a, label2), Edge(label3, a, label2), Edge(label1, a, label2))
//           )
//         )
//         _ <- instance.removeEdges(testDataset, Stream.emit(Edge(label1, a, label2)))
//         statements <- instance.allEdges(testDataset).compile.toList
//       } yield statements
//       assertIO(res, List(Edge(label3, a, label2)))
//     }
//   }

//   test("removing statements from datasets with duplicate Strings") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.addEdges(
//           testDataset,
//           Stream.emits(
//             Seq(
//               Edge(label1, a, LigatureLiteral.StringLiteral("hello")),
//               Edge(label1, a, LigatureLiteral.StringLiteral("hello")),
//               Edge(label2, a, LigatureLiteral.StringLiteral("hello"))
//             )
//           )
//         )
//         _ <- instance.removeEdges(
//           testDataset,
//           Stream.emit(Edge(label1, a, LigatureLiteral.StringLiteral("hello")))
//         )
//         statements <- instance.allEdges(testDataset).compile.toList
//       } yield statements.toSet
//       assertIO(res, Set(Edge(label2, a, LigatureLiteral.StringLiteral("hello"))))
//     }
//   }

// //   // test("allow canceling WriteTx by throwing exception") {
// //   //     val res = createLigature.instance.use { instance  =>
// //   //         for {
// //   //             _ <- instance.createDataset(testDataset)
// //   //             _ <- instance.write(testDataset).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addEdge(Edge(label1, a, label2))
// //   //                     _ <- tx.cancel()
// //   //                 } yield ()
// //   //             }
// //   //             _ <- instance.write(testDataset).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addEdge(Edge(label2, a, label3))
// //   //                     _ <- tx.addEdge(Edge(label3, a, label2))
// //   //                 } yield ()
// //   //             }
// //   //             statements <- instance.query(testDataset).use { tx =>
// //   //                 tx.allEdges().compile.toList
// //   //             }
// //   //         } yield statements
// //   //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
// //   //     assertEquals(res, Set(
// //   //         Edge(label2, a, label3),
// //   //         Edge(label3, a, label2)))
// //   // }

//   test("matching statements in datasets") {
//     createLigature.use { instance =>
//       val res = for {
//         _ <- instance.createDataset(testDataset)
//         _ <- instance.addEdges(
//           testDataset,
//           Stream.emits(
//             Seq(
//               Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
//               Edge(label2, a, label1),
//               Edge(label2, a, label3),
//               Edge(label3, b, label2),
//               Edge(label3, b, LigatureLiteral.StringLiteral("Hello"))
//             )
//           )
//         )
//         res <- instance.query(testDataset) { tx =>
//           for {
//             all <- tx.matchEdges().compile.toList
//             as <- tx.matchEdges(None, Some(a)).compile.toList
//             hellos <- tx
//               .matchEdges(None, None, Some(LigatureLiteral.StringLiteral("Hello")))
//               .compile
//               .toList
//             helloa <- tx
//               .matchEdges(None, Some(a), Some(LigatureLiteral.StringLiteral("Hello")))
//               .compile
//               .toList
//           } yield (all.toSet, as.toSet, hellos.toSet, helloa.toSet)
//         }
//       } yield res
//       res.map { (all, as, hellos, helloa) =>
//         assertEquals(
//           all,
//           Set(
//             Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
//             Edge(label2, a, label1),
//             Edge(label2, a, label3),
//             Edge(label3, b, label2),
//             Edge(label3, b, LigatureLiteral.StringLiteral("Hello"))
//           )
//         )
//         assertEquals(
//           as,
//           Set(
//             Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
//             Edge(label2, a, label1),
//             Edge(label2, a, label3)
//           )
//         )
//         assertEquals(
//           hellos,
//           Set(
//             Edge(label1, a, LigatureLiteral.StringLiteral("Hello")),
//             Edge(label3, b, LigatureLiteral.StringLiteral("Hello"))
//           )
//         )
//         assertEquals(
//           helloa,
//           Set(
//             Edge(label1, a, LigatureLiteral.StringLiteral("Hello"))
//           )
//         )
//       }
//     }
//   }

// //  test("matching statements with literals and ranges in datasets") {
// //    val instance = createLigature
// //    val res = for {
// //      _ <- instance.createDataset(testDataset)
// //      _ <- instance.write(testDataset) { tx =>
// //        for {
// //          _ <- tx.addEdge(Edge(label1, a, label2))
// //          _ <- tx
// //            .addEdge(Edge(label1, b, StringLiteral("add")))
// //          _ <- tx
// //            .addEdge(Edge(label1, a, IntegerLiteral(5L)))
// //          _ <- tx
// //            .addEdge(Edge(label2, a, IntegerLiteral(3L)))
// //          _ <- tx.addEdge(
// //            Edge(label2, a, StringLiteral("divide"))
// //          )
// //          _ <- tx.addEdge(Edge(label2, b, label3))
// //          _ <- tx
// //            .addEdge(Edge(label3, a, IntegerLiteral(7L)))
// //          _ <- tx.addEdge(
// //            Edge(label3, b, StringLiteral("decimal"))
// //          )
// //        } yield ()
// //      }
// //      res <- instance.query(testDataset) { tx =>
// //        for {
// //          res1 <- tx
// //            .matchEdgesRange(None, None, StringLiteralRange("a", "dd"))
// //            .compile
// //            .toList
// //          res2 <- tx
// //            .matchEdgesRange(None, None, IntegerLiteralRange(3, 6))
// //            .compile
// //            .toList
// //          res3 <- tx
// //            .matchEdgesRange(None, Some(b), StringLiteralRange("ae", "df"))
// //            .compile
// //            .toList
// //        } yield (res1.toSet, res2.toSet, res3.toSet)
// //      }
// //    } yield res
// //    res.map { (res1, res2, res3) =>
// //      assertEquals(
// //        res1,
// //        Set(
// //          Edge(label1, b, StringLiteral("add"))
// //        )
// //      )
// //      assertEquals(
// //        res2,
// //        Set(
// //          Edge(label2, a, IntegerLiteral(3L)),
// //          Edge(label1, a, IntegerLiteral(5L))
// //        )
// //      )
// //      assertEquals(
// //        res3,
// //        Set(
// //          Edge(label3, b, StringLiteral("decimal"))
// //        )
// //      )
// //    }
// //  }
}

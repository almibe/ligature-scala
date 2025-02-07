// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature.wander._
import munit.FunSuite


abstract class LigatureTestSuite extends FunSuite {
  def createLigature(): Ligature

  val testDataset = "test/test"
  val testDataset2 = "test/test2"
  val testDataset3 = "test3/test"
  val a = LigatureValue.Element("a")
  val b = LigatureValue.Element("b")
//   val label1 = LigatureValue.Word("a")
//   val label2 = LigatureValue.Word("b")
//   val label3 = LigatureValue.Word("c")

  val setup = FunFixture[Ligature](
    setup = { test =>
      // Files.createTempFile("tmp", test.name)
      createLigature()
    },
    teardown = { instance =>
      ???
      //instance.close()
    }
  )

  //  runExternalTests(setup)

  setup.test("create and close store") { instance =>
    assertEquals(instance.networks().subscribe().asIterable().toList, List())
  }

//   setup.test("creating a new network") { instance =>
//     instance.createDataset(testDataset)
//     val res = instance.allDatasets().toList
//     assertEquals(res, List(testDataset))
//   }

//   setup.test("check if datasets exist") { instance =>
//     instance.createDataset(testDataset)
//     val exists1 = instance.networkExists(testDataset)
//     val exists2 = instance.networkExists(testDataset2)
//     val res = (exists1, exists2)
//     assertEquals(res, (true, false))
//   }

//   setup.test("match datasets prefix exact") { instance =>
//     instance.createDataset(testDataset)
//     val res = instance.matchDatasetsPrefix("test/test").toList
//     assertEquals(res.length, 1)
//   }

//   setup.test("match datasets prefix") { instance =>
//     instance.createDataset(testDataset)
//     instance.createDataset(testDataset2)
//     instance.createDataset(testDataset3)
//     val res1 = instance.matchDatasetsPrefix("test").length
//     val res2 = instance.matchDatasetsPrefix("test/").length
//     val res3 = instance.matchDatasetsPrefix("snoo").length
//     assertEquals((res1, res2, res3), (3, 2, 0))
//   }

//   setup.test("match datasets range") { instance =>
//     instance.createDataset(DatasetName("a"))
//     instance.createDataset(DatasetName("app"))
//     instance.createDataset(DatasetName("b"))
//     instance.createDataset(DatasetName("be"))
//     instance.createDataset(DatasetName("bee"))
//     instance.createDataset(
//       DatasetName("test1/test")
//     )
//     instance.createDataset(
//       DatasetName("test2/test2")
//     )
//     instance.createDataset(
//       DatasetName("test3/test")
//     )
//     instance.createDataset(DatasetName("test4"))
//     instance.createDataset(DatasetName("z"))
//     instance.allDatasets().toList.length
//     val res1 = instance.matchDatasetsRange("a", "b").toList.length
//     val res2 = instance.matchDatasetsRange("be", "test3").toList.length
//     val res3 = instance.matchDatasetsRange("snoo", "zz").toList.length
//     assertEquals((res1, res2, res3), (2, 4, 5)) // TODO check instances not just counts
//   }

//   setup.test("create and delete new network") { instance =>
//     instance.createDataset(testDataset)
//     instance.deleteDataset(testDataset)
//     instance.deleteDataset(testDataset2)
//     val res = instance.allDatasets().toList
//     assertEquals(res, List())
//   }

//   setup.test("new datasets should be empty") { instance =>
//     instance.createDataset(testDataset)
//     val res = instance.allTriples(testDataset).toList
//     assertEquals(res, List())
//   }

//   setup.test("adding triples to datasets") { instance =>
//     instance.createDataset(testDataset)
//     instance.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label1, a, label2), // dupe
//         Triple(label1, a, label3)
//       ).iterator
//     )
//     val edges = instance.allTriples(testDataset).toSet
//     assertEquals(
//       edges,
//       Set(
//         Triple(label1, a, label2),
//         Triple(label1, a, label3)
//       )
//     )
//   }

//   setup.test("add Triple with Int Value") { instance =>
//     instance.createDataset(testDataset)
//     instance.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label1, a, LigatureValue.Int(100)),
//         Triple(label1, a, LigatureValue.Int(101)),
//         Triple(label1, a, LigatureValue.Int(100)),
//         Triple(label2, a, LigatureValue.Int(-243729))
//       ).iterator
//     )
//     val edges = instance.allTriples(testDataset).toSet
//     assertEquals(
//       edges,
//       Set(
//         Triple(label1, a, label2),
//         Triple(label1, a, LigatureValue.Int(100)),
//         Triple(label1, a, LigatureValue.Int(101)),
//         Triple(label2, a, LigatureValue.Int(-243729))
//       )
//     )
//   }

//   setup.test("add Triple with String Value") { instance =>
//     instance.createDataset(testDataset)
//     instance.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label1, a, LigatureValue.String("text")),
//         Triple(label1, a, LigatureValue.String("text2")),
//         Triple(label1, a, LigatureValue.String("text")),
//         Triple(label2, a, LigatureValue.String("text"))
//       ).iterator
//     )
//     val edges = instance.allTriples(testDataset).toSet
//     assertEquals(
//       edges,
//       Set(
//         Triple(label1, a, label2),
//         Triple(label1, a, LigatureValue.String("text")),
//         Triple(label1, a, LigatureValue.String("text2")),
//         Triple(label2, a, LigatureValue.String("text"))
//       )
//     )
//   }

// // //   // setup.test("new words") {
// // //   //   val instance = createLigature
// // //   //   val res = for {
// // //   //     _ <- instance.createDataset(testDataset)
// // //   //     _ <- instance.write(testDataset) { tx =>
// // //   //       for {
// // //   //         entity <- tx.newWord("entity-")
// // //   //         attribute <- tx.newWord("attribute-")
// // //   //         value <- tx.newWord("value-")
// // //   //         _ <- tx.addTriple(Triple(entity, attribute, value))
// // //   //       } yield IO.unit
// // //   //     }
// // //   //     triples <- instance.query(testDataset) { tx =>
// // //   //       tx.allTriples().toList
// // //   //     }
// // //   //   } yield triples.head
// // //   //   res.map { it =>
// // //   //     assert(it.entity.name.startsWith("entity-"))
// // //   //     assert(it.attribute.name.startsWith("attribute-"))
// // //   //     it.value match {
// // //   //       case Word(id) => assert(id.startsWith("value-"))
// // //   //       case _              => assert(false)
// // //   //     }
// // //   //   }
// // //   // }

//   setup.test("removing triples from datasets") { instance =>
//     instance.createDataset(testDataset)
//     instance.addTriples(testDataset, Seq(Triple(label1, a, label2)).iterator)
//     instance.removeTriples(
//       testDataset,
//       Seq(Triple(label1, a, label2), Triple(label1, a, label2)).iterator
//     )
//     val edges = instance.allTriples(testDataset).toSet
//     assertEquals(edges, Set())
//   }

//   setup.test("removing triples from datasets with dupe") { instance =>
//     instance.createDataset(testDataset)
//     instance.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label3, a, label2),
//         Triple(label1, a, label2)
//       ).iterator
//     )
//     instance.removeTriples(testDataset, Seq(Triple(label1, a, label2)).iterator)
//     val edges = instance.allTriples(testDataset).toSet
//     assertEquals(edges, Set(Triple(label3, a, label2)))
//   }

//   setup.test("removing triples from datasets with duplicate Strings") { instance =>
//     instance.createDataset(testDataset)
//     instance.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, LigatureValue.String("hello")),
//         Triple(label1, a, LigatureValue.String("hello")),
//         Triple(label2, a, LigatureValue.String("hello"))
//       ).iterator
//     )
//     instance.removeTriples(
//       testDataset,
//       Seq(Triple(label1, a, LigatureValue.String("hello"))).iterator
//     )
//     val edges = instance.allTriples(testDataset).toSet
//     assertEquals(edges, Set(Triple(label2, a, LigatureValue.String("hello"))))
//   }

// // //   // setup.test("allow canceling WriteTx by throwing exception") {
// // //   //     val res = createLigature.instance.use { instance  =>
// // //   //         for {
// // //   //             _ <- instance.createDataset(testDataset)
// // //   //             _ <- instance.write(testDataset).use { tx =>
// // //   //                 for {
// // //   //                     _ <- tx.addTriple(Triple(label1, a, label2))
// // //   //                     _ <- tx.cancel()
// // //   //                 } yield ()
// // //   //             }
// // //   //             _ <- instance.write(testDataset).use { tx =>
// // //   //                 for {
// // //   //                     _ <- tx.addTriple(Triple(label2, a, label3))
// // //   //                     _ <- tx.addTriple(Triple(label3, a, label2))
// // //   //                 } yield ()
// // //   //             }
// // //   //             triples <- instance.query(testDataset).use { tx =>
// // //   //                 tx.allTriples().toList
// // //   //             }
// // //   //         } yield triples
// // //   //     }.unsafeRunSync().map(_.right.get).map(_.triple).toSet
// // //   //     assertEquals(res, Set(
// // //   //         Triple(label2, a, label3),
// // //   //         Triple(label3, a, label2)))
// // //   // }

//   setup.test("matching triples in datasets") { instance =>
//     instance.createDataset(testDataset)
//     instance.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, LigatureValue.String("Hello")),
//         Triple(label2, a, label1),
//         Triple(label2, a, label3),
//         Triple(label3, b, label2),
//         Triple(label3, b, LigatureValue.String("Hello"))
//       ).iterator
//     )
//     val (all, as, hellos, helloa) = instance.query(testDataset) { tx =>
//       val all = tx.matchTriples().toSet
//       val as = tx.matchTriples(None, Some(a)).toSet
//       val hellos = tx
//         .matchTriples(None, None, Some(LigatureValue.String("Hello")))
//         .toSet
//       val helloa = tx
//         .matchTriples(None, Some(a), Some(LigatureValue.String("Hello")))
//         .toSet
//       (all, as, hellos, helloa)
//     }
//     assertEquals(
//       all,
//       Set(
//         Triple(label1, a, LigatureValue.String("Hello")),
//         Triple(label2, a, label1),
//         Triple(label2, a, label3),
//         Triple(label3, b, label2),
//         Triple(label3, b, LigatureValue.String("Hello"))
//       )
//     )
//     assertEquals(
//       as,
//       Set(
//         Triple(label1, a, LigatureValue.String("Hello")),
//         Triple(label2, a, label1),
//         Triple(label2, a, label3)
//       )
//     )
//     assertEquals(
//       hellos,
//       Set(
//         Triple(label1, a, LigatureValue.String("Hello")),
//         Triple(label3, b, LigatureValue.String("Hello"))
//       )
//     )
//     assertEquals(
//       helloa,
//       Set(
//         Triple(label1, a, LigatureValue.String("Hello"))
//       )
//     )
//   }

// // //  setup.test("matching triples with literals and ranges in datasets") {
// // //    val instance = createLigature
// // //    val res = for {
// // //      _ <- instance.createDataset(testDataset)
// // //      _ <- instance.write(testDataset) { tx =>
// // //        for {
// // //          _ <- tx.addTriple(Triple(label1, a, label2))
// // //          _ <- tx
// // //            .addTriple(Triple(label1, b, String("add")))
// // //          _ <- tx
// // //            .addTriple(Triple(label1, a, Int(5L)))
// // //          _ <- tx
// // //            .addTriple(Triple(label2, a, Int(3L)))
// // //          _ <- tx.addTriple(
// // //            Triple(label2, a, String("divide"))
// // //          )
// // //          _ <- tx.addTriple(Triple(label2, b, label3))
// // //          _ <- tx
// // //            .addTriple(Triple(label3, a, Int(7L)))
// // //          _ <- tx.addTriple(
// // //            Triple(label3, b, String("decimal"))
// // //          )
// // //        } yield ()
// // //      }
// // //      res <- instance.query(testDataset) { tx =>
// // //        for {
// // //          res1 <- tx
// // //            .matchTriplesRange(None, None, StringRange("a", "dd"))
// // //            .compile
// // //            .toList
// // //          res2 <- tx
// // //            .matchTriplesRange(None, None, IntRange(3, 6))
// // //            .compile
// // //            .toList
// // //          res3 <- tx
// // //            .matchTriplesRange(None, Some(b), StringRange("ae", "df"))
// // //            .compile
// // //            .toList
// // //        } yield (res1.toSet, res2.toSet, res3.toSet)
// // //      }
// // //    } yield res
// // //    res.map { (res1, res2, res3) =>
// // //      assertEquals(
// // //        res1,
// // //        Set(
// // //          Triple(label1, b, String("add"))
// // //        )
// // //      )
// // //      assertEquals(
// // //        res2,
// // //        Set(
// // //          Triple(label2, a, Int(3L)),
// // //          Triple(label1, a, Int(5L))
// // //        )
// // //      )
// // //      assertEquals(
// // //        res3,
// // //        Set(
// // //          Triple(label3, b, String("decimal"))
// // //        )
// // //      )
// // //    }
// // //  }
// }

// // def runExternalTests(setup: TestSetup) = {
// //   sys.env.get("WANDER_TEST_SUITE") match {
// //     case Some(dir) =>
// //       val files = File(dir).listFiles
// //         .filter(_.isFile)
// //         .filter(_.getName.endsWith(".test.wander"))
// //         .map(_.getPath)
// //         .toList
// //       files.foreach { f =>
// //         //val script = Source.fromFile(f).mkString
// //         //val library = DirectoryLibrary(Path.of(dir))
// //         run(script, std(List())) match {
// //           case Left(err) => fail(f.toString() + err.toString())
// //           case Right((results, _)) =>
// //             evaluateResults(results, f)
// //         }
// //       }
// //     case None => ()
// //   }

// //   def evaluateResults(results: LigatureValue, fileName: String) =
// //     results match
// //       case LigatureValue.Array(tests) =>
// //         tests.foreach { currentTest =>
// //           currentTest match
// //             case LigatureValue.Module(values) =>
// //               test(values(Field("name")).toString) {
// //                 val test = values(Field("test"))
// //                 val expected = values(Field("expect"))
// //                 assertEquals(test, expected)
// //               }
// //             case _ => ???
// //         }
// //       case _ =>
// //         throw RuntimeException(s"In $fileName -- Expected result to be array got ${results}")
}

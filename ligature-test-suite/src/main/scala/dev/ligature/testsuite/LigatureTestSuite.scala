/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature.wander._
import munit.CatsEffectSuite
import cats.effect.IO

abstract class LigatureTestSuite extends CatsEffectSuite {
  def createStore(): Store

  val testNetworkName = "test/test"
  val testNetworkName2 = "test/test2"
  val testNetworkName3 = "test3/test"
  val a = LigatureValue.Element("a")
  val b = LigatureValue.Element("b")
//   val label1 = LigatureValue.Word("a")
//   val label2 = LigatureValue.Word("b")
//   val label3 = LigatureValue.Word("c")

  val setup = FunFixture[Store](
    setup = { test =>
      createStore()
    },
    teardown = { store =>
      //store.close()
    }
  )

  //  runExternalTests(setup)

  setup.test("create and close store") { store =>    
   assertIO(store.networks().compile.toList, List())
  }

  setup.test("creating a new network") { store =>
    val res = 
      for {
        _ <- store.addNetwork(testNetworkName)
        res <- store.networks().compile.toList
      } yield res
    assertIO(res, List(testNetworkName))
  }

//   setup.test("check if datasets exist") { store =>
//     store.createDataset(testDataset)
//     val exists1 = store.networkExists(testDataset)
//     val exists2 = store.networkExists(testDataset2)
//     val res = (exists1, exists2)
//     assertEquals(res, (true, false))
//   }

//   setup.test("match datasets prefix exact") { store =>
//     store.createDataset(testDataset)
//     val res = store.matchDatasetsPrefix("test/test").toList
//     assertEquals(res.length, 1)
//   }

//   setup.test("match datasets prefix") { store =>
//     store.createDataset(testDataset)
//     store.createDataset(testDataset2)
//     store.createDataset(testDataset3)
//     val res1 = store.matchDatasetsPrefix("test").length
//     val res2 = store.matchDatasetsPrefix("test/").length
//     val res3 = store.matchDatasetsPrefix("snoo").length
//     assertEquals((res1, res2, res3), (3, 2, 0))
//   }

//   setup.test("match datasets range") { store =>
//     store.createDataset(DatasetName("a"))
//     store.createDataset(DatasetName("app"))
//     store.createDataset(DatasetName("b"))
//     store.createDataset(DatasetName("be"))
//     store.createDataset(DatasetName("bee"))
//     store.createDataset(
//       DatasetName("test1/test")
//     )
//     store.createDataset(
//       DatasetName("test2/test2")
//     )
//     store.createDataset(
//       DatasetName("test3/test")
//     )
//     store.createDataset(DatasetName("test4"))
//     store.createDataset(DatasetName("z"))
//     store.allDatasets().toList.length
//     val res1 = store.matchDatasetsRange("a", "b").toList.length
//     val res2 = store.matchDatasetsRange("be", "test3").toList.length
//     val res3 = store.matchDatasetsRange("snoo", "zz").toList.length
//     assertEquals((res1, res2, res3), (2, 4, 5)) // TODO check stores not just counts
//   }

//   setup.test("create and delete new network") { store =>
//     store.createDataset(testDataset)
//     store.deleteDataset(testDataset)
//     store.deleteDataset(testDataset2)
//     val res = store.allDatasets().toList
//     assertEquals(res, List())
//   }

//   setup.test("new datasets should be empty") { store =>
//     store.createDataset(testDataset)
//     val res = store.allTriples(testDataset).toList
//     assertEquals(res, List())
//   }

//   setup.test("adding triples to datasets") { store =>
//     store.createDataset(testDataset)
//     store.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label1, a, label2), // dupe
//         Triple(label1, a, label3)
//       ).iterator
//     )
//     val edges = store.allTriples(testDataset).toSet
//     assertEquals(
//       edges,
//       Set(
//         Triple(label1, a, label2),
//         Triple(label1, a, label3)
//       )
//     )
//   }

//   setup.test("add Triple with Int Value") { store =>
//     store.createDataset(testDataset)
//     store.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label1, a, LigatureValue.Int(100)),
//         Triple(label1, a, LigatureValue.Int(101)),
//         Triple(label1, a, LigatureValue.Int(100)),
//         Triple(label2, a, LigatureValue.Int(-243729))
//       ).iterator
//     )
//     val edges = store.allTriples(testDataset).toSet
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

//   setup.test("add Triple with String Value") { store =>
//     store.createDataset(testDataset)
//     store.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label1, a, LigatureValue.String("text")),
//         Triple(label1, a, LigatureValue.String("text2")),
//         Triple(label1, a, LigatureValue.String("text")),
//         Triple(label2, a, LigatureValue.String("text"))
//       ).iterator
//     )
//     val edges = store.allTriples(testDataset).toSet
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
// // //   //   val store = createLigature
// // //   //   val res = for {
// // //   //     _ <- store.createDataset(testDataset)
// // //   //     _ <- store.write(testDataset) { tx =>
// // //   //       for {
// // //   //         entity <- tx.newWord("entity-")
// // //   //         attribute <- tx.newWord("attribute-")
// // //   //         value <- tx.newWord("value-")
// // //   //         _ <- tx.addTriple(Triple(entity, attribute, value))
// // //   //       } yield IO.unit
// // //   //     }
// // //   //     triples <- store.query(testDataset) { tx =>
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

//   setup.test("removing triples from datasets") { store =>
//     store.createDataset(testDataset)
//     store.addTriples(testDataset, Seq(Triple(label1, a, label2)).iterator)
//     store.removeTriples(
//       testDataset,
//       Seq(Triple(label1, a, label2), Triple(label1, a, label2)).iterator
//     )
//     val edges = store.allTriples(testDataset).toSet
//     assertEquals(edges, Set())
//   }

//   setup.test("removing triples from datasets with dupe") { store =>
//     store.createDataset(testDataset)
//     store.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, label2),
//         Triple(label3, a, label2),
//         Triple(label1, a, label2)
//       ).iterator
//     )
//     store.removeTriples(testDataset, Seq(Triple(label1, a, label2)).iterator)
//     val edges = store.allTriples(testDataset).toSet
//     assertEquals(edges, Set(Triple(label3, a, label2)))
//   }

//   setup.test("removing triples from datasets with duplicate Strings") { store =>
//     store.createDataset(testDataset)
//     store.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, LigatureValue.String("hello")),
//         Triple(label1, a, LigatureValue.String("hello")),
//         Triple(label2, a, LigatureValue.String("hello"))
//       ).iterator
//     )
//     store.removeTriples(
//       testDataset,
//       Seq(Triple(label1, a, LigatureValue.String("hello"))).iterator
//     )
//     val edges = store.allTriples(testDataset).toSet
//     assertEquals(edges, Set(Triple(label2, a, LigatureValue.String("hello"))))
//   }

// // //   // setup.test("allow canceling WriteTx by throwing exception") {
// // //   //     val res = createLigature.store.use { store  =>
// // //   //         for {
// // //   //             _ <- store.createDataset(testDataset)
// // //   //             _ <- store.write(testDataset).use { tx =>
// // //   //                 for {
// // //   //                     _ <- tx.addTriple(Triple(label1, a, label2))
// // //   //                     _ <- tx.cancel()
// // //   //                 } yield ()
// // //   //             }
// // //   //             _ <- store.write(testDataset).use { tx =>
// // //   //                 for {
// // //   //                     _ <- tx.addTriple(Triple(label2, a, label3))
// // //   //                     _ <- tx.addTriple(Triple(label3, a, label2))
// // //   //                 } yield ()
// // //   //             }
// // //   //             triples <- store.query(testDataset).use { tx =>
// // //   //                 tx.allTriples().toList
// // //   //             }
// // //   //         } yield triples
// // //   //     }.unsafeRunSync().map(_.right.get).map(_.triple).toSet
// // //   //     assertEquals(res, Set(
// // //   //         Triple(label2, a, label3),
// // //   //         Triple(label3, a, label2)))
// // //   // }

//   setup.test("matching triples in datasets") { store =>
//     store.createDataset(testDataset)
//     store.addTriples(
//       testDataset,
//       Seq(
//         Triple(label1, a, LigatureValue.String("Hello")),
//         Triple(label2, a, label1),
//         Triple(label2, a, label3),
//         Triple(label3, b, label2),
//         Triple(label3, b, LigatureValue.String("Hello"))
//       ).iterator
//     )
//     val (all, as, hellos, helloa) = store.query(testDataset) { tx =>
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
// // //    val store = createLigature
// // //    val res = for {
// // //      _ <- store.createDataset(testDataset)
// // //      _ <- store.write(testDataset) { tx =>
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
// // //      res <- store.query(testDataset) { tx =>
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

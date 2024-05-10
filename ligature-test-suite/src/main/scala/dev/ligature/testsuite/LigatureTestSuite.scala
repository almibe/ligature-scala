/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature._
import munit.FunSuite

abstract class LigatureTestSuite extends FunSuite {
  def createLigature(): Ligature

  val testDataset = DatasetName("test/test")
  val testDataset2 = DatasetName("test/test2")
  val testDataset3 = DatasetName("test3/test")
  val a: LigatureValue.Identifier = LigatureValue.Identifier("a")
  val b: LigatureValue.Identifier = LigatureValue.Identifier("b")
  val label1: LigatureValue.Identifier = LigatureValue.Identifier("a")
  val label2: LigatureValue.Identifier = LigatureValue.Identifier("b")
  val label3: LigatureValue.Identifier = LigatureValue.Identifier("c")

  val setup = FunFixture[Ligature](
    setup = { test =>
      // Files.createTempFile("tmp", test.name)
      createLigature()
    },
    teardown = { instance =>
      instance.close()
    }
  )

//  runExternalTests(setup)

  setup.test("create and close store") { instance =>
    assertEquals(instance.allDatasets().toList, List())
  }

  setup.test("creating a new graph") { instance =>
    instance.createDataset(testDataset)
    val res = instance.allDatasets().toList
    assertEquals(res, List(testDataset))
  }

  setup.test("check if datasets exist") { instance =>
    instance.createDataset(testDataset)
    val exists1 = instance.graphExists(testDataset)
    val exists2 = instance.graphExists(testDataset2)
    val res = (exists1, exists2)
    assertEquals(res, (true, false))
  }

  setup.test("match datasets prefix exact") { instance =>
    instance.createDataset(testDataset)
    val res = instance.matchDatasetsPrefix("test/test").toList
    assertEquals(res.length, 1)
  }

  setup.test("match datasets prefix") { instance =>
    instance.createDataset(testDataset)
    instance.createDataset(testDataset2)
    instance.createDataset(testDataset3)
    val res1 = instance.matchDatasetsPrefix("test").length
    val res2 = instance.matchDatasetsPrefix("test/").length
    val res3 = instance.matchDatasetsPrefix("snoo").length
    assertEquals((res1, res2, res3), (3, 2, 0))
  }

  setup.test("match datasets range") { instance =>
    instance.createDataset(DatasetName("a"))
    instance.createDataset(DatasetName("app"))
    instance.createDataset(DatasetName("b"))
    instance.createDataset(DatasetName("be"))
    instance.createDataset(DatasetName("bee"))
    instance.createDataset(
      DatasetName("test1/test")
    )
    instance.createDataset(
      DatasetName("test2/test2")
    )
    instance.createDataset(
      DatasetName("test3/test")
    )
    instance.createDataset(DatasetName("test4"))
    instance.createDataset(DatasetName("z"))
    instance.allDatasets().toList.length
    val res1 = instance.matchDatasetsRange("a", "b").toList.length
    val res2 = instance.matchDatasetsRange("be", "test3").toList.length
    val res3 = instance.matchDatasetsRange("snoo", "zz").toList.length
    assertEquals((res1, res2, res3), (2, 4, 5)) // TODO check instances not just counts
  }

  setup.test("create and delete new graph") { instance =>
    instance.createDataset(testDataset)
    instance.deleteDataset(testDataset)
    instance.deleteDataset(testDataset2)
    val res = instance.allDatasets().toList
    assertEquals(res, List())
  }

  setup.test("new datasets should be empty") { instance =>
    instance.createDataset(testDataset)
    val res = instance.allStatements(testDataset).toList
    assertEquals(res, List())
  }

  setup.test("adding statements to datasets") { instance =>
    instance.createDataset(testDataset)
    instance.addStatements(
      testDataset,
      Seq(
        Statement(label1, a, label2),
        Statement(label1, a, label2), // dupe
        Statement(label1, a, label3)
      ).iterator
    )
    val edges = instance.allStatements(testDataset).toSet
    assertEquals(
      edges,
      Set(
        Statement(label1, a, label2),
        Statement(label1, a, label3)
      )
    )
  }

  setup.test("add Statement with IntegerValue Value") { instance =>
    instance.createDataset(testDataset)
    instance.addStatements(
      testDataset,
      Seq(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.IntegerValue(100)),
        Statement(label1, a, LigatureValue.IntegerValue(101)),
        Statement(label1, a, LigatureValue.IntegerValue(100)),
        Statement(label2, a, LigatureValue.IntegerValue(-243729))
      ).iterator
    )
    val edges = instance.allStatements(testDataset).toSet
    assertEquals(
      edges,
      Set(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.IntegerValue(100)),
        Statement(label1, a, LigatureValue.IntegerValue(101)),
        Statement(label2, a, LigatureValue.IntegerValue(-243729))
      )
    )
  }

  setup.test("add Statement with StringValue Value") { instance =>
    instance.createDataset(testDataset)
    instance.addStatements(
      testDataset,
      Seq(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.StringValue("text")),
        Statement(label1, a, LigatureValue.StringValue("text2")),
        Statement(label1, a, LigatureValue.StringValue("text")),
        Statement(label2, a, LigatureValue.StringValue("text"))
      ).iterator
    )
    val edges = instance.allStatements(testDataset).toSet
    assertEquals(
      edges,
      Set(
        Statement(label1, a, label2),
        Statement(label1, a, LigatureValue.StringValue("text")),
        Statement(label1, a, LigatureValue.StringValue("text2")),
        Statement(label2, a, LigatureValue.StringValue("text"))
      )
    )
  }

// //   // setup.test("new identifiers") {
// //   //   val instance = createLigature
// //   //   val res = for {
// //   //     _ <- instance.createDataset(testDataset)
// //   //     _ <- instance.write(testDataset) { tx =>
// //   //       for {
// //   //         entity <- tx.newLabel("entity-")
// //   //         attribute <- tx.newLabel("attribute-")
// //   //         value <- tx.newLabel("value-")
// //   //         _ <- tx.addStatement(Statement(entity, attribute, value))
// //   //       } yield IO.unit
// //   //     }
// //   //     statements <- instance.query(testDataset) { tx =>
// //   //       tx.allStatements().toList
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

  setup.test("removing statements from datasets") { instance =>
    instance.createDataset(testDataset)
    instance.addStatements(testDataset, Seq(Statement(label1, a, label2)).iterator)
    instance.removeStatements(
      testDataset,
      Seq(Statement(label1, a, label2), Statement(label1, a, label2)).iterator
    )
    val edges = instance.allStatements(testDataset).toSet
    assertEquals(edges, Set())
  }

  setup.test("removing statements from datasets with dupe") { instance =>
    instance.createDataset(testDataset)
    instance.addStatements(
      testDataset,
      Seq(
        Statement(label1, a, label2),
        Statement(label3, a, label2),
        Statement(label1, a, label2)
      ).iterator
    )
    instance.removeStatements(testDataset, Seq(Statement(label1, a, label2)).iterator)
    val edges = instance.allStatements(testDataset).toSet
    assertEquals(edges, Set(Statement(label3, a, label2)))
  }

  setup.test("removing statements from datasets with duplicate Strings") { instance =>
    instance.createDataset(testDataset)
    instance.addStatements(
      testDataset,
      Seq(
        Statement(label1, a, LigatureValue.StringValue("hello")),
        Statement(label1, a, LigatureValue.StringValue("hello")),
        Statement(label2, a, LigatureValue.StringValue("hello"))
      ).iterator
    )
    instance.removeStatements(
      testDataset,
      Seq(Statement(label1, a, LigatureValue.StringValue("hello"))).iterator
    )
    val edges = instance.allStatements(testDataset).toSet
    assertEquals(edges, Set(Statement(label2, a, LigatureValue.StringValue("hello"))))
  }

// //   // setup.test("allow canceling WriteTx by throwing exception") {
// //   //     val res = createLigature.instance.use { instance  =>
// //   //         for {
// //   //             _ <- instance.createDataset(testDataset)
// //   //             _ <- instance.write(testDataset).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addStatement(Statement(label1, a, label2))
// //   //                     _ <- tx.cancel()
// //   //                 } yield ()
// //   //             }
// //   //             _ <- instance.write(testDataset).use { tx =>
// //   //                 for {
// //   //                     _ <- tx.addStatement(Statement(label2, a, label3))
// //   //                     _ <- tx.addStatement(Statement(label3, a, label2))
// //   //                 } yield ()
// //   //             }
// //   //             statements <- instance.query(testDataset).use { tx =>
// //   //                 tx.allStatements().toList
// //   //             }
// //   //         } yield statements
// //   //     }.unsafeRunSync().map(_.right.get).map(_.statement).toSet
// //   //     assertEquals(res, Set(
// //   //         Statement(label2, a, label3),
// //   //         Statement(label3, a, label2)))
// //   // }

  setup.test("matching statements in datasets") { instance =>
    instance.createDataset(testDataset)
    instance.addStatements(
      testDataset,
      Seq(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label2, a, label1),
        Statement(label2, a, label3),
        Statement(label3, b, label2),
        Statement(label3, b, LigatureValue.StringValue("Hello"))
      ).iterator
    )
    val (all, as, hellos, helloa) = instance.query(testDataset) { tx =>
      val all = tx.matchStatements().toSet
      val as = tx.matchStatements(None, Some(a)).toSet
      val hellos = tx
        .matchStatements(None, None, Some(LigatureValue.StringValue("Hello")))
        .toSet
      val helloa = tx
        .matchStatements(None, Some(a), Some(LigatureValue.StringValue("Hello")))
        .toSet
      (all, as, hellos, helloa)
    }
    assertEquals(
      all,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label2, a, label1),
        Statement(label2, a, label3),
        Statement(label3, b, label2),
        Statement(label3, b, LigatureValue.StringValue("Hello"))
      )
    )
    assertEquals(
      as,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label2, a, label1),
        Statement(label2, a, label3)
      )
    )
    assertEquals(
      hellos,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello")),
        Statement(label3, b, LigatureValue.StringValue("Hello"))
      )
    )
    assertEquals(
      helloa,
      Set(
        Statement(label1, a, LigatureValue.StringValue("Hello"))
      )
    )
  }

// //  setup.test("matching statements with literals and ranges in datasets") {
// //    val instance = createLigature
// //    val res = for {
// //      _ <- instance.createDataset(testDataset)
// //      _ <- instance.write(testDataset) { tx =>
// //        for {
// //          _ <- tx.addStatement(Statement(label1, a, label2))
// //          _ <- tx
// //            .addStatement(Statement(label1, b, StringValue("add")))
// //          _ <- tx
// //            .addStatement(Statement(label1, a, IntegerValue(5L)))
// //          _ <- tx
// //            .addStatement(Statement(label2, a, IntegerValue(3L)))
// //          _ <- tx.addStatement(
// //            Statement(label2, a, StringValue("divide"))
// //          )
// //          _ <- tx.addStatement(Statement(label2, b, label3))
// //          _ <- tx
// //            .addStatement(Statement(label3, a, IntegerValue(7L)))
// //          _ <- tx.addStatement(
// //            Statement(label3, b, StringValue("decimal"))
// //          )
// //        } yield ()
// //      }
// //      res <- instance.query(testDataset) { tx =>
// //        for {
// //          res1 <- tx
// //            .matchStatementsRange(None, None, StringValueRange("a", "dd"))
// //            .compile
// //            .toList
// //          res2 <- tx
// //            .matchStatementsRange(None, None, IntegerValueRange(3, 6))
// //            .compile
// //            .toList
// //          res3 <- tx
// //            .matchStatementsRange(None, Some(b), StringValueRange("ae", "df"))
// //            .compile
// //            .toList
// //        } yield (res1.toSet, res2.toSet, res3.toSet)
// //      }
// //    } yield res
// //    res.map { (res1, res2, res3) =>
// //      assertEquals(
// //        res1,
// //        Set(
// //          Statement(label1, b, StringValue("add"))
// //        )
// //      )
// //      assertEquals(
// //        res2,
// //        Set(
// //          Statement(label2, a, IntegerValue(3L)),
// //          Statement(label1, a, IntegerValue(5L))
// //        )
// //      )
// //      assertEquals(
// //        res3,
// //        Set(
// //          Statement(label3, b, StringValue("decimal"))
// //        )
// //      )
// //    }
// //  }
}

// def runExternalTests(setup: TestSetup) = {
//   sys.env.get("WANDER_TEST_SUITE") match {
//     case Some(dir) =>
//       val files = File(dir).listFiles
//         .filter(_.isFile)
//         .filter(_.getName.endsWith(".test.wander"))
//         .map(_.getPath)
//         .toList
//       files.foreach { f =>
//         //val script = Source.fromFile(f).mkString
//         //val library = DirectoryLibrary(Path.of(dir))
//         run(script, std(List())) match {
//           case Left(err) => fail(f.toString() + err.toString())
//           case Right((results, _)) =>
//             evaluateResults(results, f)
//         }
//       }
//     case None => ()
//   }

//   def evaluateResults(results: WanderValue, fileName: String) =
//     results match
//       case WanderValue.Array(tests) =>
//         tests.foreach { currentTest =>
//           currentTest match
//             case WanderValue.Module(values) =>
//               test(values(Field("name")).toString) {
//                 val test = values(Field("test"))
//                 val expected = values(Field("expect"))
//                 assertEquals(test, expected)
//               }
//             case _ => ???
//         }
//       case _ =>
//         throw RuntimeException(s"In $fileName -- Expected result to be array got ${results}")
// }

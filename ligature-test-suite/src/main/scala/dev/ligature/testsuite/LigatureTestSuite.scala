/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature._
import munit._
import monix.execution.Scheduler.Implicits.global

abstract class LigatureTestSuite extends FunSuite {
  def createLigature: Ligature

  val testDataset = Dataset.fromString("test/test").get
  val testDataset2 = Dataset.fromString("test/test2").get
  val testDataset3 = Dataset.fromString("test3/test").get
  val a = Attribute.fromString("a").get
  val b = Attribute.fromString("b").get

  test("create and close store") {
    val res = createLigature.instance.use { instance: LigatureInstance  =>
      instance.allDatasets().toListL
    }.runSyncUnsafe()
    assert(res.isEmpty)
  }

  test("creating a new dataset") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(testDataset)
        res <- instance.allDatasets().toListL
      } yield res
    }.runSyncUnsafe()
    assertEquals(res, List(Right(testDataset)))
  }

  test("check if datasets exist") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(testDataset)
        exists1 <- instance.datasetExists(testDataset)
        exists2 <- instance.datasetExists(testDataset2)
      } yield (exists1, exists2)
    }.runSyncUnsafe()
    assert(res._1.right.get)
    assert(!res._2.right.get)
  }

  test("match datasets prefix") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.createDataset(testDataset2)
        _ <- instance.createDataset(testDataset3)
        res1 <- instance.matchDatasetsPrefix("test").toListL
        res2 <- instance.matchDatasetsPrefix("test/").toListL
        res3 <- instance.matchDatasetsPrefix("snoo").toListL
      } yield (res1, res2, res3)
    }.runSyncUnsafe()
    assertEquals(res._1.length, 3)
    assertEquals(res._2.length, 2)
    assertEquals(res._3.length, 0)
  }

  test("match datasets range") {
    val res = createLigature.instance.use { instance: LigatureInstance =>
      for {
        _ <- instance.createDataset(Dataset.fromString("a").get)
        _ <- instance.createDataset(Dataset.fromString("app").get)
        _ <- instance.createDataset(Dataset.fromString("b").get)
        _ <- instance.createDataset(Dataset.fromString("be").get)
        _ <- instance.createDataset(Dataset.fromString("bee").get)
        _ <- instance.createDataset(Dataset.fromString("test1/test").get)
        _ <- instance.createDataset(Dataset.fromString("test2/test2").get)
        _ <- instance.createDataset(Dataset.fromString("test3/test").get)
        _ <- instance.createDataset(Dataset.fromString("test4").get)
        _ <- instance.createDataset(Dataset.fromString("z").get)
        res <- instance.allDatasets().toListL
        res1 <- instance.matchDatasetsRange("a", "b").toListL
        res2 <- instance.matchDatasetsRange("be", "test3").toListL
        res3 <- instance.matchDatasetsRange("snoo", "zz").toListL
      } yield (res1, res2, res3)
    }.runSyncUnsafe()
    assertEquals(res._1.length, 2) //TODO check instances not just counts
    assertEquals(res._2.length, 4) //TODO check instances not just counts
    assertEquals(res._3.length, 5) //TODO check instances not just counts
  }

  test("create and delete new dataset") {
    val res = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.deleteDataset(testDataset)
        _ <- instance.deleteDataset(testDataset2)
        res <- instance.allDatasets().toListL
      } yield res
    }.runSyncUnsafe()
    assert(res.isEmpty)
  }

  test("new datasets should be empty") {
    val res = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        res <- instance.query(testDataset).use { tx =>
          tx.allStatements().toListL
        }
      } yield res
    }.runSyncUnsafe()
    assert(res.isEmpty)
  }

  test("create new entity") {
    val (entity1, entity2) = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.write(testDataset).use { tx =>
          for {
            _ <- tx.newEntity()
            _ <- tx.newEntity()
          } yield ()
        }
        res <- instance.write(testDataset).use { tx =>
          for {
            entity3 <- tx.newEntity()
            entity4 <- tx.newEntity()
          } yield (entity3, entity4)
        }
      } yield res
    }.runSyncUnsafe()
    assertEquals(entity1.right.get.id, 3L)
    assertEquals(entity2.right.get.id, 4L)
  }

  test("allow canceling WriteTx") {
    val (entity1, entity2) = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.write(testDataset).use { tx =>
          for {
            _ <- tx.newEntity()
            _ <- tx.newEntity()
            _ <- tx.cancel()
          } yield ()
        }
        res <- instance.write(testDataset).use { tx =>
          for {
            entity1 <- tx.newEntity()
            entity2 <- tx.newEntity()
          } yield (entity1, entity2)
        }
      } yield res
    }.runSyncUnsafe()
    assertEquals(entity1.right.get.id, 1L)
    assertEquals(entity2.right.get.id, 2L)
  }

  test("adding statements to datasets") {
    val res = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.write(testDataset).use { tx =>
          for {
            ent1 <- tx.newEntity()
            ent2 <- tx.newEntity()
            ent3 <- tx.newEntity()
            _    <- tx.addStatement(Statement(ent1.right.get, a, ent2.right.get))
            _    <- tx.addStatement(Statement(ent1.right.get, a, ent2.right.get)) //dupes get added since they'll have unique contexts
            _    <- tx.addStatement(Statement(ent1.right.get, a, ent3.right.get))
          } yield()
        }
        res  <- instance.query(testDataset).use { tx =>
          tx.allStatements().toListL
        }
      } yield res
    }.runSyncUnsafe().map(_.right.get).toSet
    assertEquals(res, Set(
      PersistedStatement(Statement(Entity(1), a, Entity(2)), Entity(4)),
      PersistedStatement(Statement(Entity(1), a, Entity(2)), Entity(5)),
      PersistedStatement(Statement(Entity(1), a, Entity(3)), Entity(6))))
  }

  test("removing statements from datasets") {
    val res = createLigature.instance.use { instance =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.write(testDataset).use { tx =>
          for {
            nn1 <- tx.newEntity()
            nn2 <- tx.newEntity()
            nn3 <- tx.newEntity()
            ps1 <- tx.addStatement(Statement(nn1.right.get, a, nn2.right.get))
            _ <- tx.addStatement(Statement(nn3.right.get, a, nn2.right.get))
            _ <- tx.removeStatement(ps1.right.get)
            _ <- tx.removeStatement(ps1.right.get)
            _ <- tx.removeStatement(PersistedStatement(Statement(nn2.right.get, a, nn1.right.get), Entity(5)))
          } yield ()
        }
        res <- instance.query(testDataset).use { tx =>
          tx.allStatements().map {
            _.right.get.statement
          }.toListL
        }
      } yield res
    }.runSyncUnsafe().toSet
    assertEquals(res, Set(Statement(Entity(3), a, Entity(2))))
  }

  test("get persisted statement from context") {
    val res = createLigature.instance.use { instance =>
      for {
        _ <- instance.createDataset(testDataset)
        ps <- instance.write(testDataset).use { tx =>
          for {
            nn1 <- tx.newEntity()
            nn2 <- tx.newEntity()
            _ <- tx.addStatement(Statement(nn1.right.get, a, nn2.right.get))
            ps <- tx.addStatement(Statement(nn2.right.get, a, nn2.right.get))
          } yield ps.right.get
        }
        res <- instance.query(testDataset).use { tx =>
          tx.statementForContext(ps.context)
        }
      } yield res
    }.runSyncUnsafe()
    assertEquals(res.right.get.get, PersistedStatement(Statement(Entity(2), a, Entity(2)), Entity(4)))
  }

  test("matching statements in datasets") {
    val (all, as, hellos, helloa) = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.write(testDataset).use { tx =>
          for {
            ent1 <- tx.newEntity()
            ent2 <- tx.newEntity()
            ent3 <- tx.newEntity()
            _    <- tx.addStatement(Statement(ent1.right.get, a, StringLiteral("Hello")))
            _    <- tx.addStatement(Statement(ent2.right.get, a, ent1.right.get))
            _    <- tx.addStatement(Statement(ent2.right.get, a, ent3.right.get))
            _    <- tx.addStatement(Statement(ent3.right.get, b, ent2.right.get))
            _    <- tx.addStatement(Statement(ent3.right.get, b, StringLiteral("Hello")))
          } yield()
        }
        res <- instance.query(testDataset).use { tx =>
          for {
            all <- tx.matchStatements(None, None, None).toListL
            as  <- tx.matchStatements(None, Some(a), None).toListL
            hellos <- tx.matchStatements(None, None, Some(StringLiteral("Hello"))).toListL
            helloa <- tx.matchStatements(None, Some(a), Some(StringLiteral("Hello"))).toListL
          } yield (all, as, hellos, helloa)
        }
      } yield res
    }.runSyncUnsafe()
    assertEquals(all.map(_.right.get).toSet.size, 5)
    assertEquals(as.map(_.right.get).toSet.size, 3)
    assertEquals(hellos.map(_.right.get).toSet.size, 2)
    assertEquals(helloa.map(_.right.get).toSet.size, 1)
  }

  test("matching statements with literals and ranges in datasets") {
    val (res1, res2, res3) = createLigature.instance.use { instance  =>
      for {
        _ <- instance.createDataset(testDataset)
        _ <- instance.write(testDataset).use { tx =>
          for {
            ent1 <- tx.newEntity()
            ent2 <- tx.newEntity()
            ent3 <- tx.newEntity()
            _    <- tx.addStatement(Statement(ent1.right.get, a, ent2.right.get))
            _    <- tx.addStatement(Statement(ent1.right.get, b, FloatLiteral(1.1)))
            _    <- tx.addStatement(Statement(ent1.right.get, a, IntergerLiteral(5L)))
            _    <- tx.addStatement(Statement(ent2.right.get, a, IntergerLiteral(3L)))
            _    <- tx.addStatement(Statement(ent2.right.get, a, FloatLiteral(10.0)))
            _    <- tx.addStatement(Statement(ent2.right.get, b, ent3.right.get))
            _    <- tx.addStatement(Statement(ent3.right.get, a, IntergerLiteral(7L)))
            _    <- tx.addStatement(Statement(ent3.right.get, b, FloatLiteral(12.5)))
          } yield()
        }
        res <- instance.query(testDataset).use { tx =>
          for {
            res1 <- tx.matchStatementsRange(None, None, FloatLiteralRange(1.0, 11.0)).toListL
            res2  <- tx.matchStatementsRange(None, None, IntergerLiteralRange(3,5)).toListL
            res3 <- tx.matchStatementsRange(None, Some(b), FloatLiteralRange(1.0, 11.0)).toListL
          } yield (res1, res2, res3)
        }
      } yield res
    }.runSyncUnsafe()
    assertEquals(res1.map(_.right.get).toSet.size, 2)
    assertEquals(res2.map(_.right.get).toSet.size, 1)
    assertEquals(res3.map(_.right.get).toSet.size, 1)
  }
}

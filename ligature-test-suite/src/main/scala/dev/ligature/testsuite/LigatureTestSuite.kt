/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.testsuite

import dev.ligature.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet

abstract class LigatureTestSuite : FunSpec() {
    abstract fun createLigature(): Ligature

    val testDataset = Dataset("test/test")
    val testDataset2 = Dataset("test/test2")
    val testDataset3 = Dataset("test3/test")
    val a = Attribute("a")
    val b = Attribute("b")

    init {
        test("create and close store") {
            val instance = createLigature()
            val res = instance.allDatasets().toList()
            assert(res.isEmpty())
        }

        test("creating a new dataset") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            val res = instance.allDatasets().toList()
            res shouldBe listOf(testDataset)
        }

        test("check if datasets exist") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            val exists1 = instance.datasetExists(testDataset).getOrThrow()
            val exists2 = instance.datasetExists(testDataset2).getOrThrow()
            assert(exists1)
            assert(exists2)
        }

        test("match datasets prefix") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            instance.createDataset(testDataset2)
            instance.createDataset(testDataset3)
            val res1 = instance.matchDatasetsPrefix("test").toList()
            val res2 = instance.matchDatasetsPrefix("test/").toList()
            val res3 = instance.matchDatasetsPrefix("snoo").toList()
            res1.size shouldBe 3
            res2.size shouldBe 2
            res3.size shouldBe 0
        }

        test("match datasets range") {
            val instance = createLigature()
            instance.createDataset(Dataset("a"))
            instance.createDataset(Dataset("app"))
            instance.createDataset(Dataset("b"))
            instance.createDataset(Dataset("be"))
            instance.createDataset(Dataset("bee"))
            instance.createDataset(Dataset("test1/test"))
            instance.createDataset(Dataset("test2/test2"))
            instance.createDataset(Dataset("test3/test"))
            instance.createDataset(Dataset("test4"))
            instance.createDataset(Dataset("z"))
            val res = instance.allDatasets().toList()
            val res1 = instance.matchDatasetsRange("a", "b").toList()
            val res2 = instance.matchDatasetsRange("be", "test3").toList()
            val res3 = instance.matchDatasetsRange("snoo", "zz").toList()
            res.size shouldBe 10
            res1.size shouldBe 2 //TODO check instances not just counts
            res2.size shouldBe 4 //TODO check instances not just counts
            res3.size shouldBe 5 //TODO check instances not just counts
        }

        test("create and delete new dataset") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            instance.deleteDataset(testDataset)
            instance.deleteDataset(testDataset2)
            val res = instance.allDatasets().toList()
            assert(res.isEmpty())
        }

        test("new datasets should be empty") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            val res = instance.query(testDataset) { tx ->
                tx.allStatements().toList()
            }
            assert(res.isEmpty())
        }

        test("create new entity") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            instance.write(testDataset) { tx ->
                tx.newEntity()
                tx.newEntity()
            }
            val res = instance.write(testDataset) { tx ->
                val entity3 = tx.newEntity()
                val entity4 = tx.newEntity()
                Pair(entity3, entity4)
            }
            res.first.getOrThrow().id shouldBe 3L
            res.second.getOrThrow().id shouldBe 4L
        }

        test("allow canceling WriteTx") {
            val instance = createLigature()
                instance.createDataset(testDataset)
                instance.write(testDataset) { tx ->
                    tx.newEntity()
                    tx.newEntity()
                    tx.cancel()
                }
            val res = instance.write(testDataset) { tx ->
                val entity1 = tx.newEntity()
                val entity2 = tx.newEntity()
                Pair(entity1, entity2)
            }
            res.first.getOrThrow().id shouldBe  1L
            res.second.getOrThrow().id shouldBe 2L
        }

        test("adding statements to datasets") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            instance.write(testDataset) { tx ->
                val ent1 = tx.newEntity().getOrThrow()
                val ent2 = tx.newEntity().getOrThrow()
                val ent3 = tx.newEntity().getOrThrow()
                tx.addStatement(Statement(ent1, a, ent2))
                tx.addStatement(Statement(ent1, a, ent2)) //dupes get added since they'll have unique contexts
                tx.addStatement(Statement(ent1, a, ent3))
            }
            val res = instance.query(testDataset) { tx ->
                tx.allStatements().toSet()
            }
            res shouldBe setOf(
                    PersistedStatement(Statement(Entity(1), a, Entity(2)), Entity(4)),
                    PersistedStatement(Statement(Entity(1), a, Entity(2)), Entity(5)),
                    PersistedStatement(Statement(Entity(1), a, Entity(3)), Entity(6)))
        }

        test("removing statements from datasets") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            instance.write(testDataset) { tx ->
                val nn1 = tx.newEntity().getOrThrow()
                val nn2 = tx.newEntity().getOrThrow()
                val nn3 = tx.newEntity().getOrThrow()
                val ps1 = tx.addStatement(Statement(nn1, a, nn2)).getOrThrow()
                tx.addStatement(Statement(nn3, a, nn2))
                tx.removeStatement(ps1)
                tx.removeStatement(ps1)
                tx.removeStatement(PersistedStatement(Statement(nn2, a, nn1), Entity(5)))
            }
            val res = instance.query(testDataset) { tx ->
                tx.allStatements().map { it.getOrThrow().statement }.toSet()
            }
            res shouldBe setOf(Statement(Entity(3), a, Entity(2)))
        }

        test("get persisted statement from context") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            val ps = instance.write(testDataset) { tx ->
                val nn1 = tx.newEntity().getOrThrow()
                val nn2 = tx.newEntity().getOrThrow()
                tx.addStatement(Statement(nn1, a, nn2))
                tx.addStatement(Statement(nn2, a, nn2))
            }.getOrThrow()
            val res = instance.query(testDataset) { tx ->
                tx.statementForContext(ps.context)
            }.getOrThrow()
            res shouldBe PersistedStatement(Statement(Entity(2), a, Entity(2)), Entity(4))
        }

        test("matching statements in datasets") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            instance.write(testDataset) { tx ->
                val ent1 = tx.newEntity()
                val ent2 = tx.newEntity()
                val ent3 = tx.newEntity()
                tx.addStatement(Statement(ent1.getOrThrow(), a, StringLiteral("Hello")))
                tx.addStatement(Statement(ent2.getOrThrow(), a, ent1.getOrThrow()))
                tx.addStatement(Statement(ent2.getOrThrow(), a, ent3.getOrThrow()))
                tx.addStatement(Statement(ent3.getOrThrow(), b, ent2.getOrThrow()))
                tx.addStatement(Statement(ent3.getOrThrow(), b, StringLiteral("Hello")))
            }
            val (all, allAs, hellos, helloa) = instance.query(testDataset) { tx ->
                val all = tx.matchStatements(null, null, null).toList()
                val allAs  = tx.matchStatements(null, a, null).toList()
                val hellos = tx.matchStatements(null, null, StringLiteral("Hello")).toList()
                val helloa = tx.matchStatements(null, a, StringLiteral("Hello")).toList()
                listOf(all, allAs, hellos, helloa)
            }
            all.size shouldBe 5
            allAs.size shouldBe 3
            hellos.size shouldBe 2
            helloa.size shouldBe 1
        }

        test("matching statements with literals and ranges in datasets") {
            val instance = createLigature()
            instance.createDataset(testDataset)
            instance.write(testDataset) { tx ->
                val ent1 = tx.newEntity().getOrThrow()
                val ent2 = tx.newEntity().getOrThrow()
                val ent3 = tx.newEntity().getOrThrow()
                tx.addStatement(Statement(ent1, a, ent2))
                tx.addStatement(Statement(ent1, b, FloatLiteral(1.1)))
                tx.addStatement(Statement(ent1, a, IntegerLiteral(5L)))
                tx.addStatement(Statement(ent2, a, IntegerLiteral(3L)))
                tx.addStatement(Statement(ent2, a, FloatLiteral(10.0)))
                tx.addStatement(Statement(ent2, b, ent3))
                tx.addStatement(Statement(ent3, a, IntegerLiteral(7L)))
                tx.addStatement(Statement(ent3, b, FloatLiteral(12.5)))
            }
            val (res1, res2, res3) = instance.query(testDataset) { tx ->
                val res1 = tx.matchStatementsRange(null, null, FloatLiteralRange(1.0, 11.0)).toList()
                val res2  = tx.matchStatementsRange(null, null, IntegerLiteralRange(3,5)).toList()
                val res3 = tx.matchStatementsRange(null, b, FloatLiteralRange(1.0, 11.0)).toList()
                listOf(res1, res2, res3)
            }
            res1.size shouldBe 2
            res2.size shouldBe 1
            res3.size shouldBe 1
        }
    }
}

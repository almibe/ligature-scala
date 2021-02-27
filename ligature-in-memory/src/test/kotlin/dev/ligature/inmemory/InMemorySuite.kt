package dev.ligature.inmemory

import dev.ligature.Ligature
import dev.ligature.testsuite.LigatureTestSuite

class InMemorySuite: LigatureTestSuite() {
    override fun createLigature(): Ligature = InMemoryLigature()
}

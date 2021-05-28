# ligature-jvm

An implementation of the [Ligature knowledge graph](https://github.com/almibe/ligature-specification)
written in Kotlin for the JVM.

## Goals
Slonky exists as the second implementation of Ligature.
It focuses on providing a server for working with Ligature's data model
and is built to run on the JVM or natively with GraalVM.

## Subprojects
 - ligature - The main api for Ligature.
 - lig - A support for serialization with Ligature.
 - wander-grammar - ANTLR grammar for Wander.
 - wander - A scripting language for working with Ligature.
 - ligature-test-suite - A single test suite for the Ligature api.
 - ligature-in-memory - An implementation of Ligature that stores all data in memory.
 - ligature-lmdb - An implementation of Ligature that uses lmdb for storage.
 - slonky - An implementation of Ligature's HTTP specifications.

## Related Projects
 - [ligature-specification](https://github.com/almibe/ligature-specification) - The specification for Ligature and related specifications.
 - [ligature](https://github.com/almibe/ligature) - Another implementation of Ligature based on Rust.
 - [ligature-lab](https://github.com/almibe/ligature-lab) - A webapp for working with Ligature and RDF.

## Building
This project requires gradle to build.

```
gradle compile
gradle test
```

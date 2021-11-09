# ligature-scala
An implementation of [Ligature knowledge graph](https://github.com/almibe/ligature) library written in Scala for the JVM/JS.

## Goals
Slonky exists as the second implementation of Ligature.
It focuses on providing a server for working with Ligature's data model and is built to run on the JVM, JS, or natively with GraalVM.

## Building
This project requires sbt to build.
See https://www.scala-sbt.org/

### Basics

Run `sbt` to bring up the sbt shell.

Use `projects` to see the list of sub-projects.

Use `project {project-name}` to switch projects.

Use `test` to run tests.

### Projects

| Name               | Description                                                                      | JVM | JS |
| ------------------ | -------------------------------------------------------------------------------- | --- | -- |
| ligature           | Common data types and traits for Ligature                                        | ✅   | ✅ |
| lig                | A simple serialization format for Ligature                                       | ✅   | ✅ |
| ligature-in-memory | An in-memory implementation of Ligature                                          | ✅   | ✅ |
| ligature-indexeddb | An implementation of Ligature targeting the browser using IndexedDB for storage. |     | ✅ |
| slonky             | A JVM based server for Ligature following the ligature-http spec.                |     | ✅ |
| wander             | A scripting language for working with Ligature.                                  | ✅   | ✅ |

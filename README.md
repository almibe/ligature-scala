# ligature-scala
An implementation of [Ligature knowledge graph](https://github.com/almibe/ligature-specification) library written in Scala for the JVM/JS.

## Status

This project is currently not being worked on.
See https://github.com/almibe/ligature-rs for the implementation I'm currently focusing on.

### Projects

| Name                | Description                                                                      | JVM | JS |
| ------------------- | -------------------------------------------------------------------------------- | --- | -- |
| ligature            | Common data types and traits for Ligature                                        | ✅   | ✅ |
| ligature-test-suite | A shared test suite for Ligature                                                 | ✅   | ✅ |
| idgen               | A basic, unsecure implementation of nanoid for use with Ligature                 | ✅   | ✅ |
| lig                 | A simple serialization format as well as DLig an input format for Ligature       | ✅   | ✅ |
| ligature-in-memory  | An in-memory implementation of Ligature                                          | ✅   | ✅ |
| wander              | A scripting language for working with Ligature.                                  | ✅   | ✅ |
| gaze                | A utility library for parsing text.                                              | ✅   | ✅ |
| slonky              | A JVM based server for Ligature following the ligature-http spec.                | ✅   |   |
| ligature-indexeddb  | An implementation of Ligature targeting the browser using IndexedDB for storage. |     | ✅ |

## Building
This project requires sbt to build.
See https://www.scala-sbt.org/

### Basics

For the most part development of this project only requires the basics that SBT provides.

Run `sbt` to bring up the sbt shell.

Use `projects` to see the list of sub-projects.

Use `project {project-name}` to switch projects.

Use `compile` to compile code.

Use `test` to run tests.

Use `scalafmtAll` to format code.

### Credits

The idgen package contains a Scala port of https://github.com/aventrix/jnanoid.

### JavaScript Notes

...

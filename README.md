# ligature-scala
An implementation of [Ligature knowledge graph](https://github.com/almibe/ligature-specification) library written in Scala for the JVM/JS.

### Projects

| Name                | Description                                                                      | JVM | JS |
| ------------------- | -------------------------------------------------------------------------------- | --- | -- |
| ligature            | Common data types and traits for Ligature                                        | ✅   | ✅ |
| ligature-test-suite | A shared test suite for Ligature                                                 | ✅   | ✅ |
| lig                 | A simple serialization format for Ligature                                       | ✅   | ✅ |
| ligature-in-memory  | An in-memory implementation of Ligature                                          | ✅   | ✅ |
| wander              | A scripting language for working with Ligature.                                  | ✅   | ✅ |
| gaze                | A utility library for parsing text.                                              | ✅   | ✅ |
| slonky              | A JVM based server for Ligature following the ligature-http spec.                | ✅   |   |
| ligature-indexeddb  | An implementation of Ligature targeting the browser using IndexedDB for storage. |     | ✅ |
| ligature-lab        | A simple UI for working with Ligature in the browser.                            |     | ✅ |

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

### Project Specific Notes

#### ligature-lab

ligature-lab is a web ui project that uses ligature in the browser.
Building it is a little bit different than other projects since it is exclusively and Scala.js project.
To build the project switch to the `ligature-lab` project by typing `project ligatureLabJS` in the SBT command line.
Then type `fastLinkJS` and open the index.html file that is located in the root of the ligature-lab project.
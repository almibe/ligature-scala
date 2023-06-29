# ligature-scala
An implementation of [Ligature knowledge graph](https://ligature.dev) library written in Scala for the JVM/JS.

### Projects

| Name                     | Description                                                                      |
| ------------------------ | -------------------------------------------------------------------------------- |
| ligature                 | Common data types and traits for Ligature                                        |
| ligature-test-suite      | A shared test suite for Ligature                                                 |
| idgen                    | A basic, unsecure, implementation of nanoid for use with Ligature                |
| lig                      | A simple serialization format as well as DLig an input format for Ligature       |
| ligature-in-memory       | An in-memory implementation of Ligature                                          |
| ligature-xodus           | An implementation of Ligature using LMDB for persistence.                        |
| wander                   | A scripting language for working with Ligature.                                  |
| gaze                     | A utility library for parsing text/data structures.                              |
| ligature-pad             | A simple desktop application for working with Wander and Ligature.               |
| ligature-repl            | A REPL for working with Wander and Ligature.                                     |

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

### Running the console REPL

To run the ligature-repl project run the following command outside of the sbt shell (sometimes if you run the REPL from within the sbt shell the two will cause issues, usually on the second time the REPL is ran).

`sbt ligatureZeromq/run`

### Running the Server

The server can be ran using the `serve` command.

`sbt serve`

If you are using the interaction shell, I recommend using

`reStart`

This will let you reload the server whenever you want by repeating the command.

### Credits

The idgen package contains a Scala port of https://github.com/aventrix/jnanoid.

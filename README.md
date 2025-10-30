# ligature-scala

Note: moved to https://codeberg/almibe/ligature-scala

An implementation of [Ligature knowledge graph](https://ligature.dev) library written in Scala for the JVM.

### Projects

| Name                     | Description                                                                      |
| ------------------------ | -------------------------------------------------------------------------------- |
| ligature                 | Common data types and traits for Ligature.                                       |
| ligature-test-suite      | A shared test suite for Ligature instances.                                      |
| idgen                    | A basic implementation of nanoid for use with Ligature.                          |
| ligature-in-memory       | An in-memory implementation of Ligature.                                         |
| ligature-zeromq          | A ZeroMQ end point for working with Ligature.                                    |
| gaze                     | A utility library for parsing text/data structures.                              |

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

### Running the Server

`sbt ligatureZeroMQ/run`

If you are using the interaction shell, I recommend using

`reStart`

This will let you reload the server whenever you want by repeating the command.
To stop the server run.

`reStop`

### Credits

The idgen package contains a Scala port of https://github.com/aventrix/jnanoid.

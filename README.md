# ligature-scala
An implementation of [Ligature knowledge graph](https://ligature.dev) library written in Scala for the JVM.

## Notice

Work on this project is on hold.
See [ligature-rs](https://github.com/almibe/ligature-rs/) for a Rust implementation of Ligature that is under active development.

### Projects

| Name                     | Description                                                                      |
| ------------------------ | -------------------------------------------------------------------------------- |
| ligature                 | Common data types and traits for Ligature.                                       |
| ligature-test-suite      | A shared test suite for Ligature instances.                                      |
| idgen                    | A basic implementation of nanoid for use with Ligature.                          |
| lig                      | A simple serialization format for Ligature.                                      |
| ligature-in-memory       | An in-memory implementation of Ligature.                                         |
| ligature-xodus           | An implementation of Ligature using Xodus's key-value store for persistence.     |
| wander                   | A scripting language for working with Ligature.                                  |
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

The server can also be ran using the `serve` command.

`sbt serve`

If you are using the interaction shell, I recommend using

`reStart`

This will let you reload the server whenever you want by repeating the command.
To stop the server run.

`reStop`

### Credits

The idgen package contains a Scala port of https://github.com/aventrix/jnanoid.

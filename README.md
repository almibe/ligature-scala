# ligature-scala
An implementation of [Ligature knowledge graph](https://ligature.dev) library written in Scala for the JVM.

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
| wander-ligature          | Support for working with Ligature in Wander.                                     |
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

### Running Wander's Test Suite

Check out https://github.com/almibe/wander-test-suite.

Set an environment variable called WANDER_TEST_SUITE to point to this directory.

Now you should be able to run Wander's full test suite.

### Running the Server

`sbt ligatureZeroMQ/run`

If you are using the interaction shell, I recommend using

`reStart`

This will let you reload the server whenever you want by repeating the command.
To stop the server run.

`reStop`

### Credits

The idgen package contains a Scala port of https://github.com/aventrix/jnanoid.

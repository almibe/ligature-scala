# Note
This project is in the middle of a rewrite, so it's kind of a mess.

# ligature-scala
An implementation of [Ligature knowledge graph](https://ligature.dev) library written in Scala for the JVM/JS.
See additional documentation [here](https://github.com/almibe/ligature-documentation).

### Projects

| Name                     | Description                                                                      | JVM | JS |
| ------------------------ | -------------------------------------------------------------------------------- | --- | -- |
| ligature                 | Common data types and traits for Ligature                                        | ✅   | ✅ |
| ligature-test-suite      | A shared test suite for Ligature                                                 | ✅   | ✅ |
| idgen                    | A basic, unsecure, implementation of nanoid for use with Ligature                | ✅   | ✅ |
| lig                      | A simple serialization format as well as DLig an input format for Ligature       | ✅   | ✅ |
| ligature-in-memory       | An in-memory implementation of Ligature                                          | ✅   | ✅ |
| ligature-xodus           | An implementation of Ligature using LMDB for persistence.                        | ✅   |   |
| wander                   | A scripting language for working with Ligature.                                  | ✅   | ✅ |
| gaze                     | A utility library for parsing text/data structures.                              | ✅   | ✅ |
| ligature-http            | A JVM based server for Ligature following the ligature-http spec.                | ✅   |   |
| ligature-http-test-suite | A test suite for ligature-http                                                   | ✅   |   |
| ligature-http-in-memory  | An implementation of ligature-http that stores everything in memory.             | ✅   |   |
| ligature-http-xodus      | An implementation of ligature-http that uses ligature-xodus for storage.         | ✅   |   |

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

The server can be ran using the `serve` command.

`sbt serve`

If you are using the interaction shell, I recommend using

`reStart`

This will let you reload the server whenever you want by repeating the command.

### Server Configuration

For now ligature-http only accepts configuration via the command line.
This is likely to change but for now just using command line arguments is very helpful during development.

#### Common Configuration

All ligature-http implementations accept the following arguemnts

| Name   | Description                          | Default | Note                               |
|--------|--------------------------------------|---------|------------------------------------|
| --port | The port ligature-http is served on. | 4202    | Can be any valid port number.      |
 | --mode | The mode ligature-http is ran in.    | local   | Only local is currently supported. |

#### In Memory Configuration

The ligature-http-in-memory implementation only supports the common configuration.

#### Xodus Configuration

ligature-http-xodus adds the following configuration on top of the common configuration.

| Name      | Description                                       | Default    | Note                                                                      |
|-----------|---------------------------------------------------|------------|---------------------------------------------------------------------------|
| --storage | The directory where the Xodus database is stored. | ~/ligature | Must be a valid directory, directory will be created if it doesn't exist. |

### Credits

The idgen package contains a Scala port of https://github.com/aventrix/jnanoid.

### JavaScript Notes

...

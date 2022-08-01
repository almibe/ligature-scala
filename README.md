# Note
This project is in the middle of a rewrite, so it's kind of a mess.

# ligature-kt
An implementation of [Ligature knowledge graph](https://ligature.dev) library written in Kotlin for the JVM/JS.
See additional documentation [here](https://github.com/almibe/ligature-documentation).

### Projects

| Name                     | Description                                                                      | JVM | JS |
| ------------------------ | -------------------------------------------------------------------------------- | --- | -- |
| ligature                 | Common data types and interfaces for Ligature                                    | ✅   | ✅ |
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
This project uses Gradle to build.
See https://www.gradle.org/

### Basics

For the most part development of this project only requires the basics that Gradle provides.

Use `gradle check` to check that code builds and run tests.

### Running the Server

```bash
cd ligature-http-xodus
gradle run
```

### Server Configuration

???

#### Common Configuration

All ligature-http implementations accept the following arguments

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

The idgen package contains a Kotlin port of https://github.com/aventrix/jnanoid.

### JavaScript Notes

...

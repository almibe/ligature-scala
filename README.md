# s&nbsp;&nbsp;l&nbsp;&nbsp;o&nbsp;&nbsp;n&nbsp;&nbsp;k&nbsp;&nbsp;y
*A transactional API for ordered, key-value stores in Scala.*

Slonky's goal is to make it easier to work with low-level, byte-centric, transactional, ordered, key-value stores.
It does this by providing a common API that works across multiple stores.
This API builds on existing Scala libraries namely, Monix and Cats Effect.
This project simply provides a set of Scala traits that define the interface for Slonky.
See the list of related projects below for instances of these interfaces.

## Building
This project requires SBT to be installed.
On Linux/Mac I recommend using https://sdkman.io/ to manage SBT installs.
Once that is set up use `sbt publishLocal` to install the artifact locally.

## Related Projects
| Name                | Description                                         | URL                                           |
| ------------------- | --------------------------------------------------- | --------------------------------------------- |
| slonky-test-suite   | A common test suite for Slonky implementations.     | https://github.com/almibe/slonky-test-suite   |
| slonky-in-memory    | A simple in-memory implementation for Slonky.       | https://github.com/almibe/slonky-in-memory    |
| slonky-xodus        | An implementation for Slonky based on Xodus.        | https://github.com/almibe/slonky-xodus        |
| slonky-rocksdb      | An implementation for Slonky based on RocksDB.      | https://github.com/almibe/slonky-rocksdb      |
| slonky-swaydb       | An implementation for Slonky based on SwayDB.       | https://github.com/almibe/slonky-swaydb       |
| slonky-foundationdb | An implementation for Slonky based on FoundationDB. | https://github.com/almibe/slonky-foundationdb |

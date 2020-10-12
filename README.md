# s&nbsp;&nbsp;l&nbsp;&nbsp;o&nbsp;&nbsp;n&nbsp;&nbsp;k&nbsp;&nbsp;y
*A transactional API for ordered, key-value stores in Scala.*

Slonky's goal is to make it easier to work with low-level, transactional, ordered, key-value stores.
It does this by providing a common API that works across multiple stores.
This API builds on existing Scala libraries namely, Monix and Cats Effect.

## Possible backends
 * In-Memory using standard Scala collections
 * Xodus
 * RocksDB
 * SwayDB
 * FoundationDB
 * *???*

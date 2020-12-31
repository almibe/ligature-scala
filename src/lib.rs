/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

use async_trait::async_trait;
use futures_core::stream::Stream;

struct SlonkyError(String)

trait Slonky {
  fn read() -> Resource<Task, SlonkyReadTx>;
  fn write() -> Resource<Task, SlonkyWriteTx>;
}

trait SlonkyReadTx {
  fn keyExists(key -> ByteVector) -> Task<Boolean>;
  fn prefixExists(prefix -> ByteVector) -> Task<Boolean>;
  fn get(key -> ByteVector) -> Task<Option<ByteVector>>;
  fn prefixScan(prefix -> ByteVector) -> Observable<(ByteVector, ByteVector)>;
  fn rangeScan(from -> ByteVector, to -> ByteVector) -> Observable<(ByteVector, ByteVector)>;
  fn scanAll() -> Observable<(ByteVector, ByteVector)>;
}

trait SlonkyWriteTx {
  fn keyExists(key -> ByteVector) -> Task<Boolean>;
  fn prefixExists(prefix -> ByteVector) -> Task<Boolean>;
  fn get(key -> ByteVector) -> Task<Option<ByteVector>>;
  fn put(key -> ByteVector, value -> ByteVector) -> Task<Unit>;
  fn remove(key -> ByteVector) -> Task<Unit>;
  fn cancel() -> Task<Unit>;
}

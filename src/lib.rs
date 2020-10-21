/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

trait Slonky {
    async fn read(f: dyn FnOnce(dyn ReadTx) -> Result<T, String>) -> Result<T, String>;
    async fn write(f: dyn FnOnce(dyn ReadTx) -> Result<T, String>) -> Result<T, String>;
}

trait ReadTx {
    async fn key_exists(key: ByteVector) -> Boolean;
    async fn prefix_exists(prefix: ByteVector) -> Boolean;
    async fn get(key: ByteVector) -> Option<ByteVector>;
    async fn prefix_scan(prefix: ByteVector) -> Stream<(ByteVector, ByteVector)>;
    async fn range_scan(from: ByteVector, to: ByteVector) -> Stream<(ByteVector, ByteVector)>;
    async fn scan_all() -> Stream<(ByteVector, ByteVector)>;
}

trait WriteTx {
    async fn key_exists(key: ByteVector) -> Boolean;
    async fn prefix_exists(prefix: ByteVector) -> Boolean;
    async fn get(key: ByteVector) -> Option<ByteVector>;
    async fn put(key: ByteVector, value: ByteVector) -> (ByteVector, ByteVector);
    async fn remove(key: ByteVector) -> (ByteVector, ByteVector);
    async fn cancel() -> Unit;
}

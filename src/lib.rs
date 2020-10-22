/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

use futures::stream::Stream;
use async_trait::async_trait;

#[async_trait]
pub trait Slonky {
    async fn read<T, E>(&self, f: &dyn FnOnce(dyn ReadTx) -> Result<T, E>) -> Result<T, E>;
    async fn write<E>(&self, f: &dyn FnOnce(dyn WriteTx) -> Result<(), E>) -> Result<(), E>;
}

#[async_trait]
pub trait ReadTx {
    async fn key_exists(&self, key: &[u8]) -> bool;
    async fn prefix_exists(&self, prefix: &[u8]) -> bool;
    async fn get(&self, key: &[u8]) -> Option<Vec<u8>>;
    async fn prefix_scan(&self, prefix: &[u8]) -> dyn Stream<Item=(Vec<u8>, Vec<u8>)>;
    async fn range_scan(&self, from: &[u8], to: &[u8]) -> dyn Stream<Item=(Vec<u8>, Vec<u8>)>;
    async fn scan_all(&self) -> dyn Stream<Item=(Vec<u8>, Vec<u8>)>;
}

#[async_trait]
pub trait WriteTx {
    async fn key_exists(&self, key: &[u8]) -> bool;
    async fn prefix_exists(&self, prefix: &[u8]) -> bool;
    async fn get(&self, key: &[u8]) -> Option<Vec<u8>>;
    async fn put(&self, key: &[u8], value: &[u8]) -> (Vec<u8>, Vec<u8>);
    async fn remove(&self, key: &[u8]) -> (Vec<u8>, Vec<u8>);
    async fn cancel(&self);
}

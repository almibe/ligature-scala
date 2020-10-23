/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

pub trait Slonky {
    fn read<T, E>(&self, f: Box<dyn Fn(Box<&dyn ReadTx>) -> Result<T, E>>) -> Result<T, E>;
    fn write<E>(&self, f: Box<dyn Fn(Box<&dyn WriteTx>) -> Result<(), E>>) -> Result<(), E>;
}

pub trait ReadTx {
    fn key_exists(&self, key: &[u8]) -> bool;
    fn prefix_exists(&self, prefix: &[u8]) -> bool;
    fn get(&self, key: &[u8]) -> Option<Vec<u8>>;
    fn prefix_scan(&self, prefix: &[u8]) -> Box<dyn Iterator<Item=(Vec<u8>, Vec<u8>)>>;
    fn range_scan(&self, from: &[u8], to: &[u8]) -> Box<dyn Iterator<Item=(Vec<u8>, Vec<u8>)>>;
    fn scan_all(&self) -> Box<dyn Iterator<Item=(Vec<u8>, Vec<u8>)>>;
}

pub trait WriteTx {
    fn key_exists(&self, key: &[u8]) -> bool;
    fn prefix_exists(&self, prefix: &[u8]) -> bool;
    fn get(&self, key: &[u8]) -> Option<Vec<u8>>;
    fn put(&self, key: &[u8], value: &[u8]) -> (Vec<u8>, Vec<u8>);
    fn remove(&self, key: &[u8]) -> (Vec<u8>, Vec<u8>);
    fn cancel(&self);
}

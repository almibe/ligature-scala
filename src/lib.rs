/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

use async_trait::async_trait;
use futures_core::stream::Stream;

pub struct SlonkyError(String);

#[async_trait]
pub trait Slonky {
  async fn read(self) -> Result<Box<dyn SlonkyReadTx>, SlonkyError>;
  async fn write(self) -> Result<Box<dyn SlonkyWriteTx>, SlonkyError>;
}

#[async_trait]
pub trait SlonkyReadTx {
  async fn key_exists(self, key: Vec<u8>) -> Result<bool, SlonkyError>;
  async fn prefix_exists(self, prefix: Vec<u8>) -> Result<bool, SlonkyError>;
  async fn get(self, key: Vec<u8>) -> Result<Option<Vec<u8>>, SlonkyError>;
  fn prefix_scan(self, prefix: Vec<u8>) -> Box<dyn Stream<Item = (Vec<u8>, Vec<u8>)>>;
  fn range_scan(self, from: Vec<u8>, to: Vec<u8>) -> Box<dyn Stream<Item = (Vec<u8>, Vec<u8>)>>;
  fn scan_all(self) -> Box<dyn Stream<Item = (Vec<u8>, Vec<u8>)>>;
}

#[async_trait]
pub trait SlonkyWriteTx {
  async fn key_exists(self, key: Vec<u8>) -> Result<bool, SlonkyError>;
  async fn prefix_exists(self, prefix: Vec<u8>) -> Result<bool, SlonkyError>;
  async fn get(self, key: Vec<u8>) -> Result<Option<Vec<u8>>, SlonkyError>;
  async fn put(self, key: Vec<u8>, value: Vec<u8>) -> Result<(), SlonkyError>;
  async fn remove(self, key: Vec<u8>) -> Result<(), SlonkyError>;
  async fn cancel(self) -> Result<(), SlonkyError>;
}

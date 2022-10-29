/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander.library

// def instanceModeBindings(bindings: Bindings): Bindings = {
// function instanceScope(scope: ExecutionScope, bindings: Bindings) {
//     // allDatasets(): Promise<Array<Dataset>>;
//     bindings.bind(Name("allDatasets"), NativeFunction([], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     // datasetExists(dataset: Dataset): Promise<boolean>;
//     bindings.bind(Name("datasetExists"), NativeFunction(["dataset"], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     // matchDatasetPrefix(prefix: string): Promise<Array<Dataset>>;
//     bindings.bind(Name("matchDatasetPrefix"), NativeFunction(["prefix"], (_bindings: Bindings) =>
// {
//         return TODO()
//     }))
//     // matchDatasetRange(start: string, end: string): Promise<Array<Dataset>>;
//     bindings.bind(Name("matchDatasetRange"), NativeFunction(["start", "end"], (_bindings:
// Bindings) => {
//         return TODO()
//     }))
//     // createDataset(dataset: Dataset): Promise<Dataset>;
//     bindings.bind(Name("createDataset"), NativeFunction(["dataset"], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     // deleteDataset(dataset: Dataset): Promise<Dataset>;
//     bindings.bind(Name("deleteDataset"), NativeFunction(["dataset"], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     // query<T>(dataset: Dataset, fn: (readTx: ReadTx) => Promise<T>): Promise<T>;
//     bindings.bind(Name("query"), NativeFunction(["dataset", "fn"], (_bindings: Bindings) => {
//         return TODO()
//     }))
//     // write<T>(dataset: Dataset, fn: (writeTx: WriteTx) => Promise<T>): Promise<T>;
//     bindings.bind(Name("write"), NativeFunction(["dataset", "fn"], (_bindings: Bindings) => {
//         return TODO()
//     }))
// }

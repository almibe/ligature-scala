/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.slonky

import com.google.gson.Gson

import dev.ligature.Dataset
import dev.ligature.Ligature

import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

class DatasetRequests(private val ligature: Ligature) {
    private val gson = Gson()

    suspend fun createDataset(rc: RoutingContext) {
        val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
        ligature.createDataset(dataset)
        rc.response().send()
    }

    suspend fun deleteDataset(rc: RoutingContext) {
        val dataset = Dataset(rc.normalizedPath().removePrefix("/"))
        ligature.deleteDataset(dataset)
        rc.response().send()
    }

    suspend fun queryDatasets(rc: RoutingContext) {
        val prefix = rc.queryParam("prefix")
        val rangeStart = rc.queryParam("start")
        val rangeEnd = rc.queryParam("end")
        if (prefix.size == 1 && rangeStart.isEmpty() && rangeEnd.isEmpty()) {
                val res = ligature.matchDatasetsPrefix(prefix.first()).map { it.getOrThrow().name }
                rc.response().send(gson.toJson(res.toList()))
        } else if (prefix.isEmpty() && rangeStart.size == 1 && rangeEnd.size == 1) {
                val res = ligature.matchDatasetsRange(rangeStart.first(), rangeEnd.first()).map { it.getOrThrow().name }
                rc.response().send(gson.toJson(res.toList()))
        } else { //TODO make sure that pathParams are empty + other checks
                val res = ligature.allDatasets().map { it.getOrThrow().name }
                rc.response().send(gson.toJson(res.toList()))
        }
    }
}

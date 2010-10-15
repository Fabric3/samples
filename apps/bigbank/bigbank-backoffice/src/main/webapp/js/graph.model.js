/*
 * Copyright (c) 2010 Metaform Systems
 *
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Encapsulates data for a continuously updating graph.
 *
 * @param seriesNumber the number of series in the graph
 * @param size the number of data points to track at a given time
 */
function GraphModel(seriesNumber, size) {
    this.data = [];
    this.series = [];
    this.counter = 0;
    this.seriesNumber = seriesNumber;
    this.dataSize = size;

    this.init = function() {
        for (var i = 0; i < this.seriesNumber; i++) {
            this.series[i] = [];
            for (var n = 0; n < this.dataSize; n++) {
                this.series[i].push([n,0]);
            }
            this.data.push(this.series[i]);
        }
        this.counter = this.dataSize;
    };

    /**
     * Adds a data point to the given series. If the series data exceeds the size, the oldest value will be truncated.
     * @param dataPoints         the new data ordered by series
     */
    this.add = function(dataPoints) {
        for (var i = 0; i < dataPoints.length; i++) {
            if (this.series[i].length == this.dataSize) {
                this.series[i].shift();
            }
            this.series[i].push([this.counter, dataPoints[i]]);
            this.counter++;
        }
    };


}
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.kapi

import tech.tablesaw.api.NumericColumn

/**
 *
 */
interface NumericCol : Col {

    override fun toDoubleArray(): DoubleArray

    fun getFloat(index: Int): Float

    fun max(): Double

    fun min(): Double

    fun product(): Double

    fun mean(): Double

    fun median(): Double

    fun quartile1(): Double

    fun quartile3(): Double

    fun percentile(percentile: Double): Double

    fun range(): Double

    fun variance(): Double

    fun populationVariance(): Double

    fun standardDeviation(): Double

    fun sumOfLogs(): Double

    fun sumOfSquares(): Double

    fun geometricMean(): Double

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    fun quadraticMean(): Double

    fun kurtosis(): Double

    fun skewness(): Double

    fun target(): NumericColumn

}

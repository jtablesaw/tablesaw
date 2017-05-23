package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.NumericColumn

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

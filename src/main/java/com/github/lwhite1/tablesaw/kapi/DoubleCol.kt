package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.DoubleColumn

/**
 *
 */
class DoubleCol(val target: DoubleColumn) {


/*
    operator fun plus(c : DoubleCol): DoubleColumn = target.add(c.target);

    operator fun plus(value : Double): DoubleColumn = target.addToEach(value);
*/



/*
    operator fun minus(c : DoubleCol): DoubleColumn = target.subtract(c.target);

    operator fun minus(value : Int): IntColumn = target.addToEach(-value);
*/


//    operator fun div(value : Float): DoubleColumn = target.divide(value); //todo

    operator fun div(c: IntCol): DoubleColumn = target.divide(c.target);

    operator fun div(c: DoubleCol): DoubleColumn = target.divide(c.target);


    operator fun times(c: IntCol): DoubleColumn = target.multiply(c.target);

    operator fun times(c: DoubleCol): DoubleColumn = target.multiply(c.target);


    // math functions
    fun sum(): Double = target.sum()
    fun product(): Double = target.product()
    fun sumOfLogs(): Double = target.sumOfLogs()
    fun sumOfSquares(): Double = target.sumOfSquares()

    fun mean(): Double = target.mean()
    fun geometricMean(): Double = target.geometricMean()
    fun quadraticMean(): Double = target.quadraticMean()
    fun median(): Double = target.median()
    fun quartile1(): Double = target.quartile1()
    fun quartile3(): Double = target.quartile3()
    fun percentile(percentile : Double): Double = target.percentile(percentile)

    fun max(): Double = target.max()
    fun min(): Double = target.min()

    fun range(): Double = target.range()
    fun variance(): Double = target.variance()
    fun populationVariance(): Double = target.populationVariance()
    fun standardDeviation(): Double = target.standardDeviation()

    fun skewness(): Double = target.skewness()
    fun kurtosis(): Double = target.kurtosis()


}
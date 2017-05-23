package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.FloatColumn
import com.github.lwhite1.tablesaw.util.Selection

/**
 *
 */
class FloatCol(val target: FloatColumn) {


/*
    operator fun plus(c : FloatCol): FloatColumn = target.add(c.target);

    operator fun plus(value : Float): FloatColumn = target.addToEach(value);
*/



/*
    operator fun minus(c : FloatCol): FloatColumn = target.subtract(c.target);

    operator fun minus(value : Int): IntColumn = target.addToEach(-value);
*/


//    operator fun div(value : Float): FloatColumn = target.divide(value); //todo

    operator fun div(c: IntCol): FloatColumn = target.divide(c.target);

    operator fun div(c: FloatCol): FloatColumn = target.divide(c.target);


    operator fun times(c: IntCol): FloatColumn = target.multiply(c.target);

    operator fun times(c: FloatCol): FloatColumn = target.multiply(c.target);

    operator fun rem(c: FloatCol): FloatColumn = target.remainder(c.target);


    // descriptive statistics
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


    // math functions
    fun cube(): FloatCol = FloatCol(target.cube())
    fun cubeRoot(): FloatCol = FloatCol(target.cubeRoot())

    fun square(): FloatCol = FloatCol(target.square())
    fun sqrt(): FloatCol = FloatCol(target.sqrt())

    fun abs(): FloatCol = FloatCol(target.abs())
    fun round(): FloatCol = FloatCol(target.round())
    
    fun log1p(): FloatCol = FloatCol(target.log1p())
    fun log10(): FloatCol = FloatCol(target.log10())
    fun logN(): FloatCol = FloatCol(target.logN())

    // comparisons

    fun isGreaterThan(value: Float): Selection = target.isGreaterThan(value);
    fun isGreaterThanOrEqualTo(value: Float): Selection = target.isGreaterThanOrEqualTo(value);
    fun isLessThanOrEqualTo(value: Float): Selection = target.isLessThanOrEqualTo(value);
    fun isLessThan(value: Float): Selection = target.isLessThan(value);
    fun isEqualTo(value: Float): Selection = target.isEqualTo(value);

    // other boolean expressions
    fun isZero(): Selection = target.isZero();
    fun isMissing(): Selection = target.isMissing();
    fun isNotMissing(): Selection = target.isNotMissing();
    fun isNegative(): Selection = target.isNegative();
    fun isPositive(): Selection = target.isPositive();
    fun isNonNegative(): Selection = target.isNonNegative();

}
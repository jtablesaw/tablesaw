package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.DoubleColumn
import com.github.lwhite1.tablesaw.api.NumericColumn
import com.github.lwhite1.tablesaw.store.ColumnMetadata
import com.github.lwhite1.tablesaw.util.Selection

/**
 *
 */
class DoubleCol(val target: DoubleColumn) : NumericCol {

    override fun target(): NumericColumn = target

    override fun toDoubleArray(): DoubleArray = target.toDoubleArray()

    override fun getFloat(index: Int): Float = target.getFloat(index)

    override fun size(): Int = target.size()

    override fun summary(): Dataframe = Dataframe(target.summary())

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): Col = DoubleCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun name(): String = target.name()

    override fun getString(row: Int): String = target.getString(row)

    override fun copy(): Col = DoubleCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = DoubleCol(target.emptyCopy())

    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun isEmpty(): Boolean = target.isEmpty

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun print(): String = target.print()


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
    fun cube(): DoubleCol = DoubleCol(target.cube())
    fun cubeRoot(): DoubleCol = DoubleCol(target.cubeRoot())

    fun square(): DoubleCol = DoubleCol(target.square())
    fun sqrt(): DoubleCol = DoubleCol(target.sqrt())

    fun abs(): DoubleCol = DoubleCol(target.abs())
    fun round(): DoubleCol = DoubleCol(target.round())

    fun log1p(): DoubleCol = DoubleCol(target.log1p())
    fun log10(): DoubleCol = DoubleCol(target.log10())
    fun logN(): DoubleCol = DoubleCol(target.logN())

    // summary statistics
    fun sum(): Double = target.sum()
    override fun product(): Double = target.product()
    override fun sumOfLogs(): Double = target.sumOfLogs()
    override fun sumOfSquares(): Double = target.sumOfSquares()

    override fun mean(): Double = target.mean()
    override fun geometricMean(): Double = target.geometricMean()
    override fun quadraticMean(): Double = target.quadraticMean()
    override fun median(): Double = target.median()
    override fun quartile1(): Double = target.quartile1()
    override fun quartile3(): Double = target.quartile3()
    override fun percentile(percentile : Double): Double = target.percentile(percentile)

    override fun max(): Double = target.max()
    override fun min(): Double = target.min()

    override fun range(): Double = target.range()
    override fun variance(): Double = target.variance()
    override fun populationVariance(): Double = target.populationVariance()
    override fun standardDeviation(): Double = target.standardDeviation()

    override fun skewness(): Double = target.skewness()
    override fun kurtosis(): Double = target.kurtosis()

    // comparisons

    fun isGreaterThan(value: Double): Selection = target.isGreaterThan(value)
    fun isGreaterThanOrEqualTo(value: Double): Selection = target.isGreaterThanOrEqualTo(value)
    fun isLessThanOrEqualTo(value: Double): Selection = target.isLessThanOrEqualTo(value)
    fun isLessThan(value: Double): Selection = target.isLessThan(value)
    fun isEqualTo(value: Double): Selection = target.isEqualTo(value)

    // other boolean expressions
    fun isZero(): Selection = target.isZero
    fun isMissing(): Selection = target.isMissing
    fun isNotMissing(): Selection = target.isNotMissing
    fun isNegative(): Selection = target.isNegative
    fun isPositive(): Selection = target.isPositive
    fun isNonNegative(): Selection = target.isNonNegative

}
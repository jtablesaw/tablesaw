package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.LongColumn
import com.github.lwhite1.tablesaw.api.NumericColumn
import com.github.lwhite1.tablesaw.store.ColumnMetadata

/**
 *
 */
class LongCol(val target: LongColumn) : NumericCol {

    override fun target(): NumericColumn = target

    override fun toDoubleArray(): DoubleArray = target.toDoubleArray()

    override fun getFloat(index: Int): Float = target.getFloat(index)

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun name(): String = target.name()

    override fun summary(): Dataframe = Dataframe(target.summary())

    operator fun plus(c: LongCol): LongCol = LongCol(target.append(c.target))

    operator fun minus(c: LongCol): LongCol = LongCol(target.subtract(c.target))

/*
    operator fun plus(value: Long): LongCol = LongCol(target.addToEach(value))

    operator fun minus(value: Long): LongCol = LongCol(target.addToEach(-value))

    operator fun div(value: Int): FloatCol = FloatCol(target.divide(value))

    operator fun div(c: LongCol): FloatCol = FloatCol(target.divide(c.target))
*/

    operator fun div(c: FloatCol): FloatCol = FloatCol(target.divide(c.target))

    operator fun times(c: LongCol): LongCol = LongCol(target.multiply(c.target))

    operator fun times(c: FloatCol): FloatCol = FloatCol(target.multiply(c.target))

    operator fun rem(c: LongCol): LongCol = LongCol(target.remainder(c.target))

    operator fun get(index: Int): Long = target.get(index)

    operator fun contains(i: Long): Boolean = target.contains(i)

/*
    fun isMissing(): LongCol = LongCol(target.select(target.isMissing()))

    fun isNotMissing(): LongCol = LongCol(target.select(target.isNotMissing()))
*/

    override fun size(): Int = target.size()

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): LongCol = LongCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun getString(row: Int): String = target.getString(row)

    override fun isEmpty(): Boolean = target.isEmpty()

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun print(): String = target.print()

    override fun toString(): String = target.toString()

    override fun copy(): Col = LongCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = LongCol(target.emptyCopy())


    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

    //override fun append(column: LongColumn) = target.append(column)

    // math functions
    fun sum(): Long = target.sum()              // TODO(should this return double for consistency)
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
    fun isLessThan(i: Long): LongCol = LongCol(target.select(target.isLessThan(i)))
    fun isGreaterThan(i: Long): LongCol = LongCol(target.select(target.isGreaterThan(i)))
    fun isGreaterThanOrEqualTo(i: Long): LongCol = LongCol(target.select(target.isGreaterThanOrEqualTo(i)))
    fun isLessThanOrEqualTo(i: Long): LongCol = LongCol(target.select(target.isLessThanOrEqualTo(i)))
    fun isEqualTo(i: Long): LongCol = LongCol(target.select(target.isEqualTo(i)))

    // other boolean tests
    fun isPositive(): LongCol = LongCol(target.select(target.isPositive))
    fun isNegative(): LongCol = LongCol(target.select(target.isNegative))
    fun isNonNegative(): LongCol = LongCol(target.select(target.isNonNegative))
    fun isZero(): LongCol = LongCol(target.select(target.isZero))
    fun isEven(): LongCol = LongCol(target.select(target.isEven))
    fun isOdd(): LongCol = LongCol(target.select(target.isOdd))

}
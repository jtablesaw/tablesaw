package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.NumericColumn
import com.github.lwhite1.tablesaw.api.ShortColumn
import com.github.lwhite1.tablesaw.store.ColumnMetadata

/**
 *
 */
class ShortCol(val target: ShortColumn) : NumericCol {

    override fun target(): NumericColumn = target

    override fun toDoubleArray(): DoubleArray = target.toDoubleArray()

    override fun getFloat(index: Int): Float = target.getFloat(index)

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun summary(): Dataframe = Dataframe(target.summary())

    operator fun plus(c: ShortCol): IntCol = IntCol(target.append(c.target))

    operator fun minus(c: ShortCol): IntCol = IntCol(target.subtract(c.target))

    override fun name(): String = target.name()

/*
    operator fun plus(value: Int): ShortCol = ShortCol(target.addToEach(value))

    operator fun minus(value: Int): ShortCol = ShortCol(target.addToEach(-value))

    operator fun div(value: Int): FloatCol = FloatCol(target.divide(value))

    operator fun div(c: ShortCol): FloatCol = FloatCol(target.divide(c.target))
*/

    operator fun div(c: FloatCol): FloatCol = FloatCol(target.divide(c.target))

    operator fun times(c: ShortCol): IntCol = IntCol(target.multiply(c.target))

    operator fun times(c: FloatCol): FloatCol = FloatCol(target.multiply(c.target))

    operator fun rem(c: ShortCol): IntCol = IntCol(target.remainder(c.target))

    operator fun get(index: Int): Short = target.get(index)

    operator fun contains(i: Short) = target.contains(i)

/*
    fun isMissing(): ShortCol = ShortCol(target.select(target.isMissing()))

    fun isNotMissing(): ShortCol = ShortCol(target.select(target.isNotMissing()))
*/

    override fun size(): Int = target.size()

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): ShortCol = ShortCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun getString(row: Int): String = target.getString(row)

    override fun isEmpty(): Boolean = target.isEmpty()

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun print(): String = target.print()

    override fun toString(): String = target.toString()

    override fun copy(): Col = ShortCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = ShortCol(target.emptyCopy())


    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

    //override fun append(column: ShortColumn) = target.append(column)

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
    fun isLessThan(i: Int): ShortCol = ShortCol(target.select(target.isLessThan(i)))

    fun isGreaterThan(i: Int): ShortCol = ShortCol(target.select(target.isGreaterThan(i)))
    fun isGreaterThanOrEqualTo(i: Int): ShortCol = ShortCol(target.select(target.isGreaterThanOrEqualTo(i)))
    fun isLessThanOrEqualTo(i: Int): ShortCol = ShortCol(target.select(target.isLessThanOrEqualTo(i)))
    fun isEqualTo(i: Int): ShortCol = ShortCol(target.select(target.isEqualTo(i)))

    // other boolean tests
    fun isPositive(): ShortCol = ShortCol(target.select(target.isPositive))
    fun isNegative(): ShortCol = ShortCol(target.select(target.isNegative))
    fun isNonNegative(): ShortCol = ShortCol(target.select(target.isNonNegative))
    fun isZero(): ShortCol = ShortCol(target.select(target.isZero))
    fun isEven(): ShortCol = ShortCol(target.select(target.isEven))
    fun isOdd(): ShortCol = ShortCol(target.select(target.isOdd))
}
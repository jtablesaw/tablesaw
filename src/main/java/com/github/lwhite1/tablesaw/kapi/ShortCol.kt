package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.ShortColumn

/**
 *
 */
class ShortCol(val target: ShortColumn) : AbstractColumn() {

    operator fun plus(c: ShortCol): IntCol = IntCol(target.add(c.target))

    operator fun minus(c: ShortCol): IntCol = IntCol(target.subtract(c.target))

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

    override fun metadata(): String = target.metadata()

    override fun print(): String = target.print()

    override fun toString(): String = target.toString()

    override fun columnWidth(): Int = target.columnWidth()

    override fun byteSize(): Int = target.byteSize()

    override fun emptyCopy(): ShortCol = ShortCol(target.emptyCopy())

    override fun copy(): Column = ShortCol(target.copy())

    override fun emptyCopy(rowSize: Int): Column = ShortCol(target.emptyCopy())


    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

    //override fun append(column: ShortColumn) = target.append(column)

    // math functions
    fun sum(): Long = target.sum()              // TODO(should this return double for consistency)
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
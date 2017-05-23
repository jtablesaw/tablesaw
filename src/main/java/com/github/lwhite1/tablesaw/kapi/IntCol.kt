package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.IntColumn
import com.github.lwhite1.tablesaw.filtering.IntBiPredicate
import com.github.lwhite1.tablesaw.filtering.IntPredicate
import com.github.lwhite1.tablesaw.store.ColumnMetadata
import com.github.lwhite1.tablesaw.util.Selection
import com.github.lwhite1.tablesaw.util.Stats
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntSet

/**
 *
 */
class IntCol(val target: IntColumn) : AbstractColumn() {

    companion object Factory {
        fun create(name: String, arraySize: Int = IntColumn.DEFAULT_ARRAY_SIZE): IntCol
                = IntCol(IntColumn.create(name, arraySize))

        fun create(columnMetadata: ColumnMetadata): IntCol
                = IntCol(IntColumn.create(columnMetadata))
    }

    // column metadata
    override fun type(): ColumnType = target.type()
    override fun id(): String = target.id()
    override fun metadataString(): String = target.metadata()
    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()
    override fun name(): String = target.name()

    // column-wise (vector) math operation
    operator fun plus(c: IntCol): IntCol = IntCol(target.add(c.target))
    operator fun plus(value: Int): IntCol = IntCol(target.addToEach(value))

    operator fun minus(c: IntCol): IntCol = IntCol(target.subtract(c.target))
    operator fun minus(value: Int): IntCol = IntCol(target.addToEach(-value))

    operator fun div(value: Int): FloatCol = FloatCol(target.divide(value))
    operator fun div(value: Double): FloatCol = FloatCol(target.divide(value))
    operator fun div(c: IntCol): FloatCol = FloatCol(target.divide(c.target))
    operator fun div(c: FloatCol): FloatCol = FloatCol(target.divide(c.target))

    operator fun times(c: IntCol): IntCol = IntCol(target.multiply(c.target))
    operator fun times(value: Int): IntCol = IntCol(target.multiply(value))
    operator fun times(c: FloatCol): FloatCol = FloatCol(target.multiply(c.target))
    operator fun times(value: Double): FloatCol = FloatCol(target.multiply(value))

    operator fun rem(c: IntCol): IntCol = IntCol(target.remainder(c.target))

    // cell access
    operator fun get(index: Int): Int = target.get(index)
    override fun getString(row: Int): String = target.getString(row)

    fun getFloat(row: Int): Float = target.getFloat(row)

    operator fun contains(i: Int) = target.contains(i)

    // query about column contents
    override fun size(): Int = target.size()
    override fun isEmpty(): Boolean = target.isEmpty()
    override fun countMissing(): Int = target.countMissing()
    override fun countUnique(): Int = target.countUnique()

    override fun unique(): IntCol = IntCol(target.unique())

    fun firstElement(): Int = target.firstElement()
    fun bottom(n: Int): IntArrayList = target.bottom(n)
    fun top(n: Int): IntArrayList = target.top(n)

    // summarize
    override fun print(): String = target.print()
    override fun toString(): String = target.toString()
    override fun summary(): Dataframe = Dataframe(target.summary())

    // copying
    override fun copy(): Column = IntCol(target.copy())
    override fun emptyCopy(rowSize: Int): Column = IntCol(target.emptyCopy())

    override fun clear() = target.clear()

    // sorting
    override fun sortAscending() = target.sortAscending()
    override fun sortDescending() = target.sortDescending()

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

/*
    fun append(column: Column) {
        if (column.type() == ColumnType.INTEGER) {
            target.append(column.target)
        }
    }
*/

    // summary and descriptive statistics
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

    // TODO(lwhite) Review this and see if it does what it should
    fun stats(): Stats = target.stats()

    // TODO(lwhite): Consider replacing these special cases with something that supports any summary op
    fun countIf(predicate: IntPredicate): Long = target.countIf(predicate)
    fun sumIf(predicate: IntPredicate): Long = target.sumIf(predicate)

    fun selectIf(predicate: IntPredicate): IntCol = IntCol(target.selectIf(predicate))
    fun select(predicate: IntBiPredicate, value : Int) = target.select(predicate, value)

    // Wrapping any boolean operation below with this call returns an IntCol
    //TODO("Should we return selections so they can be used for updates?")
    fun select(selection: Selection): IntCol = IntCol(target.select(selection))

    // comparisons
    fun isLessThan(i: Int): IntCol = IntCol(target.select(target.isLessThan(i)))
    fun isGreaterThan(i: Int): IntCol = IntCol(target.select(target.isGreaterThan(i)))
    fun isGreaterThanOrEqualTo(i: Int): IntCol = IntCol(target.select(target.isGreaterThanOrEqualTo(i)))
    fun isLessThanOrEqualTo(i: Int): IntCol = IntCol(target.select(target.isLessThanOrEqualTo(i)))
    fun isEqualTo(i: Int): IntCol = IntCol(target.select(target.isEqualTo(i)))
    fun isBetween(low: Int, high: Int): IntCol {
        val selection :Selection = target.isGreaterThan(low)
        selection.and(target.isLessThan(high))
        return IntCol(target.select(selection))
    }
    fun isIn(list: IntArray): IntCol = IntCol(target.select(target.isIn(*list)))

    // other boolean tests
    fun isPositive(): IntCol = IntCol(target.select(target.isPositive))
    fun isNegative(): IntCol = IntCol(target.select(target.isNegative))
    fun isNonNegative(): IntCol = IntCol(target.select(target.isNonNegative))
    fun isZero(): IntCol = IntCol(target.select(target.isZero))
    fun isEven(): IntCol = IntCol(target.select(target.isEven))
    fun isOdd(): IntCol = IntCol(target.select(target.isOdd))
    fun isMissing(): IntCol = IntCol(target.select(target.isMissing))
    fun isNotMissing(): IntCol = IntCol(target.select(target.isNotMissing))

    // conversions
    fun toFloatArray(): FloatArray {
        val output = FloatArray(target.size())
        for ((i, aData) in target.withIndex()) {
            output[i] = aData.toFloat()
        }
        return output
    }

    fun toIntArray(): IntArray {
        val output = IntArray(target.size())
        for (i in target) {
            output[i] = target[i]
        }
        return output
    }

    override fun toDoubleArray(): DoubleArray {
        val output = DoubleArray(target.size())
        for ((i) in target.withIndex()) {
            output[i] = target.get(i).toDouble()
        }
        return output
    }

    fun asSet(): IntSet = target.asSet()

    fun difference(): IntCol = IntCol(target.difference())
}
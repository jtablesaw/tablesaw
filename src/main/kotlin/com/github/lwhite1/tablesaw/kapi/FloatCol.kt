package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.FloatColumn
import com.github.lwhite1.tablesaw.api.NumericColumn
import com.github.lwhite1.tablesaw.store.ColumnMetadata
import com.github.lwhite1.tablesaw.util.Selection

/**
 *
 */
class FloatCol(val target: FloatColumn) : NumericCol {

    override fun target(): NumericColumn = target

    override fun toDoubleArray(): DoubleArray = target.toDoubleArray()

    override fun getFloat(index: Int): Float = target.getFloat(index)

    override fun size(): Int = target.size()

    override fun summary(): Dataframe = Dataframe(target.summary())

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): Col = FloatCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun name(): String = target.name()

    override fun getString(row: Int): String = target.getString(row)

    override fun copy(): Col = FloatCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = FloatCol(target.emptyCopy(rowSize))

    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun isEmpty(): Boolean = target.isEmpty

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun print(): String = target.print()



    operator fun plus(c : FloatCol): FloatCol = FloatCol(target.add(c.target))
    operator fun plus(value : Float): FloatCol = FloatCol(target.addToEach(value))

    operator fun minus(c : FloatCol): FloatCol = FloatCol(target.subtract(c.target))
    operator fun minus(value : Int): FloatCol = FloatCol(target.addToEach(-value))


//    operator fun div(value : Float): FloatColumn = target.divide(value); //todo
    operator fun div(c: IntCol): FloatCol = FloatCol(target.divide(c.target))
    operator fun div(c: FloatCol): FloatCol = FloatCol(target.divide(c.target))

    operator fun times(c: IntCol): FloatCol = FloatCol(target.multiply(c.target))
    operator fun times(c: FloatCol): FloatCol = FloatCol(target.multiply(c.target))

    operator fun rem(c: FloatCol): FloatCol = FloatCol(target.remainder(c.target))


    // descriptive statistics
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

    fun isGreaterThan(value: Float): Selection = target.isGreaterThan(value)
    fun isGreaterThanOrEqualTo(value: Float): Selection = target.isGreaterThanOrEqualTo(value)
    fun isLessThanOrEqualTo(value: Float): Selection = target.isLessThanOrEqualTo(value)
    fun isLessThan(value: Float): Selection = target.isLessThan(value)
    fun isEqualTo(value: Float): Selection = target.isEqualTo(value)

    // other boolean expressions
    fun isZero(): Selection = target.isZero
    fun isMissing(): Selection = target.isMissing
    fun isNotMissing(): Selection = target.isNotMissing
    fun isNegative(): Selection = target.isNegative
    fun isPositive(): Selection = target.isPositive
    fun isNonNegative(): Selection = target.isNonNegative

}
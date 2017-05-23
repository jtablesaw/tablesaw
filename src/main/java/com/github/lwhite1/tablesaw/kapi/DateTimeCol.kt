package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.DateTimeColumn
import com.github.lwhite1.tablesaw.store.ColumnMetadata
import com.github.lwhite1.tablesaw.util.Selection
import java.time.LocalDateTime

/**
 */
class DateTimeCol(val target: DateTimeColumn) : Col {

    override fun size(): Int = target.size()

    override fun summary(): Dataframe = Dataframe(target.summary())

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): Col = DateTimeCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun name(): String = target.name()

    override fun getString(row: Int): String = target.getString(row)

    override fun copy(): Col = DateTimeCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = DateTimeCol(target.emptyCopy(rowSize))

    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun isEmpty(): Boolean = target.isEmpty

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun print(): String = target.print()


    fun append(value: Long) = target.append(value)
    fun append(value: LocalDateTime) = target.append(value)
    override fun appendCell(stringValue: String) = target.appendCell(stringValue)
//fun append(c: Column) = target.append(c)

    fun isAfter(value: Long): Selection = target.isAfter(value)
    fun isAfter(value: LocalDateTime): Selection = target.isAfter(value)
    fun isOnOrAfter(value: LocalDateTime): Selection = target.isOnOrAfter(value)
    fun isOnOrAfter(value: Long): Selection = target.isOnOrAfter(value)

    fun isBefore(value: Long): Selection = target.isBefore(value)
    fun isBefore(value: LocalDateTime): Selection = target.isBefore(value)
    fun isOnOrBefore(value: LocalDateTime): Selection = target.isOnOrBefore(value)
    fun isOnOrBefore(value: Long): Selection = target.isOnOrBefore(value)

    fun isMonday(): Selection = target.isMonday
    fun isTuesday(): Selection = target.isTuesday
    fun isWednesday(): Selection = target.isWednesday
    fun isThursday(): Selection = target.isThursday
    fun isFriday(): Selection = target.isFriday
    fun isSaturday(): Selection = target.isSaturday
    fun isSunday(): Selection = target.isSunday

    fun isInJanuary(): Selection = target.isInJanuary
    fun isInFebruary(): Selection = target.isInFebruary
    fun isInMarch(): Selection = target.isInMarch
    fun isInApril(): Selection = target.isInApril
    fun isInMay(): Selection = target.isInMay
    fun isInJune(): Selection = target.isInJune
    fun isInJuly(): Selection = target.isInJuly
    fun isInAugust(): Selection = target.isInAugust
    fun isInSeptember(): Selection = target.isInSeptember
    fun isInOctober(): Selection = target.isInOctober
    fun isInNovember(): Selection = target.isInNovember
    fun isInDecember(): Selection = target.isInDecember

    fun isFirstDayOfMonth(): Selection = target.isFirstDayOfMonth
    fun isLastDayOfMonth(): Selection = target.isLastDayOfMonth

    fun isInQ1(): Selection = target.isInQ1
    fun isInQ2(): Selection = target.isInQ2
    fun isInQ3(): Selection = target.isInQ3
    fun isInQ4(): Selection = target.isInQ4

    fun isInYear(year: Int): Selection = target.isInYear(year)

    fun max(): LocalDateTime? = target.max()
    fun min(): LocalDateTime? = target.min()

//  operator fun contains(localDate: LocalDateTime): Boolean = target.contains(localDate)
}
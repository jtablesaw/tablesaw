/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.kapi

import tech.tablesaw.api.CategoryColumn
import tech.tablesaw.api.ColumnType
import tech.tablesaw.api.DateColumn
import tech.tablesaw.api.FloatColumn
import tech.tablesaw.api.ShortColumn
import tech.tablesaw.api.TimeColumn
import tech.tablesaw.columns.Column
import tech.tablesaw.store.ColumnMetadata
import tech.tablesaw.util.Selection
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

class DateCol(val target: DateColumn) : Col {

    override fun size(): Int = target.size()

    override fun summary(): Dataframe = Dataframe(target.summary())

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): Col = DateCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun name(): String = target.name()

    override fun getString(row: Int): String = target.getString(row)

    override fun copy(): Col = DateCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = DateCol(target.emptyCopy(rowSize))

    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun isEmpty(): Boolean = target.isEmpty

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun print(): String = target.print()


    override fun appendCell(stringValue: String) = target.appendCell(stringValue)
    //fun append(c: Column) = target.append(c)

    fun isAfter(value: Int): Selection = target.isAfter(value)
    fun isAfter(value: LocalDate): Selection = target.isAfter(value)
    fun isOnOrAfter(value: LocalDate): Selection = target.isOnOrAfter(value)
    fun isOnOrAfter(value: Int): Selection = target.isOnOrAfter(value)

    fun isBefore(value: Int): Selection = target.isBefore(value)
    fun isBefore(value: LocalDate): Selection = target.isBefore(value)
    fun isOnOrBefore(value: LocalDate): Selection = target.isOnOrBefore(value)
    fun isOnOrBefore(value: Int): Selection = target.isOnOrBefore(value)


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

    fun max(): LocalDate? = target.max()
    fun min(): LocalDate? = target.min()

  //  operator fun contains(localDate: LocalDate): Boolean = target.contains(localDate)


    // Mapping utilities

    fun dateColumnName(column1: Column, value: Int, unit: TemporalUnit): String {
        return column1.name() + ": " + value + " " + unit.toString() + "(s)"
    }

    fun differenceInDays(column2: DateCol): FloatColumn = target.differenceInDays(column2.target)

    fun differenceInWeeks(column2: DateCol): FloatColumn = target.differenceInWeeks(column2.target)

    fun differenceInMonths(column2: DateCol): FloatColumn = target.differenceInMonths(column2.target)

    fun differenceInYears(column2: DateCol): FloatColumn = target.differenceInYears(column2.target)

    /**
     * Calculates the temporal difference between each element of the receiver and the respective element of the
     * argument
     *
     *
     * Missing values in either result in a Missing Value for the new column
     */
    fun difference(column1: DateCol, column2: DateCol, unit: ChronoUnit): FloatCol
            = FloatCol(target.difference(column1.target, column2.target, unit))

    // These functions fill some amount of time to a date, producing a new date column
    fun plusDays(days: Int): DateCol = plus(days, ChronoUnit.DAYS)

    fun plusWeeks(weeks: Int): DateCol = plus(weeks, ChronoUnit.WEEKS)

    fun plusYears(years: Int): DateCol = plus(years, ChronoUnit.YEARS)

    fun plusMonths(months: Int): DateCol = plus(months, ChronoUnit.MONTHS)

    // These functions subtract some amount of time from a date, producing a new date column

    fun minusDays(days: Int): DateCol = plusDays(-days)

    fun minusWeeks(weeks: Int): DateCol = plusWeeks(-weeks)

    fun minusYears(years: Int): DateCol = plusYears(-years)

    fun minusMonths(months: Int): DateCol = plusMonths(-months)

    fun plus(value: Int, unit: TemporalUnit): DateCol = DateCol(target.plus(value, unit))

    fun minus(value: Int, unit: TemporalUnit): DateCol = DateCol(target.minus(value, unit))

    fun atStartOfDay(): DateTimeCol = DateTimeCol(target.atStartOfDay())

    /**
     * Returns a DateTime column where each value consists of the dates from this column combined with the corresponding
     * times from the other column
     */
    fun atTime(time: LocalTime): DateTimeCol = DateTimeCol(target.atTime(time))

    /**
     * Returns a DateTime column where each value consists of the dates from this column combined with the corresponding
     * times from the other column
     */
    fun atTime(timeColumn: TimeColumn): DateTimeCol = DateTimeCol(target.atTime(timeColumn))

    operator fun get(index: Int): LocalDate? = target.get(index);

    fun dayOfWeek(): CategoryColumn = target.dayOfWeek()

    fun dayOfWeekValue(): ShortColumn = target.dayOfWeekValue()

    fun dayOfMonth(): ShortColumn = target.dayOfMonth()

    fun dayOfYear(): ShortColumn = target.dayOfYear()

    fun monthValue(): ShortColumn = target.monthValue()

    fun month(): CategoryColumn = target.month()

    fun year(): ShortColumn = target.year()


}

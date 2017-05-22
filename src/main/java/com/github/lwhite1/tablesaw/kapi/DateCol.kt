package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.DateColumn
import com.github.lwhite1.tablesaw.util.Selection
import java.time.LocalDate

/**
 *
 */
internal class DateCol(val target: DateColumn) {

    fun append(value: Int) = target.append(value)
    fun appendCell(value: String) = target.appendCell(value)
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

}

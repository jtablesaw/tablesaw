package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.TimeColumn
import com.github.lwhite1.tablesaw.util.Selection
import java.time.LocalTime

/**
 *
 */
class TimeCol(val target: TimeColumn) {

    fun append(value: Int) = target.append(value)
    fun append(value: LocalTime) = target.append(value)
    fun appendCell(value: String) = target.appendCell(value)
    //fun append(c: Column) = target.append(c)

    fun isAfter(value: Int): Selection = target.isAfter(value)
    fun isAfter(value: LocalTime): Selection = target.isAfter(value)
    fun isOnOrAfter(value: LocalTime): Selection = target.isOnOrAfter(value)
    fun isOnOrAfter(value: Int): Selection = target.isOnOrAfter(value)

    fun isBefore(value: Int): Selection = target.isBefore(value)
    fun isBefore(value: LocalTime): Selection = target.isBefore(value)
    fun isOnOrBefore(value: LocalTime): Selection = target.isOnOrBefore(value)
    fun isOnOrBefore(value: Int): Selection = target.isOnOrBefore(value)


    fun max(): LocalTime? = target.max()
    fun min(): LocalTime? = target.min()

  //  operator fun contains(localDate: LocalDate): Boolean = target.contains(localDate)

}

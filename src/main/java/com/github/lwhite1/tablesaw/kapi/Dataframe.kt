package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.api.QueryHelper.column
import com.github.lwhite1.tablesaw.api.Table


/**
 *  Kotlin wrapper for Table.java
 */

class Dataframe (val target : Table) {

    fun name(): String = target.name()

    override fun toString(): String = target.toString()
    fun print(): String = target.print()
    fun printHtml(): String = target.printHtml()

    fun summary(): String = target.summary()
    fun shape(): String = target.shape()

    fun emptyCopy(): Dataframe = Dataframe(target.emptyCopy())
    fun emptyCopy(rowCount: Int): Dataframe = Dataframe(target.emptyCopy(rowCount))

    fun first(nRows: Int): Dataframe = Dataframe(target.first(nRows))
    fun last(nRows: Int): Dataframe = Dataframe(target.last(nRows))

    fun rowCount(): Int = target.rowCount()
    fun columnCount(): Int = target.columnCount()

    fun columnIndex(columnName: String): Int = target.columnIndex(columnName)
    fun append(dataframe: Dataframe) = target.append(dataframe.target)

    operator fun get(c: Int, r: Int): String = target.get(c, r)

    fun columnNames(): List<String> = target.columnNames()

    // fun select(vararg columnName : String) : Projection = target.select(columnName)  // todo review
    fun selectWhere(): Table = target.selectWhere(column("name").isEqualTo("t"))  // todo review

    companion object Factory {
        fun createFromCsv(
                csvString : String,
                header: Boolean = false,
                delimiter: Char = ',')

                : Dataframe
                = Dataframe(Table.createFromCsv(csvString, header, delimiter))
    }
}
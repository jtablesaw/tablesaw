package com.github.lwhite1.tablesaw.kapi

import com.github.lwhite1.tablesaw.api.ColumnType
import com.github.lwhite1.tablesaw.util.Selection

/**
 *
 */
public interface Column {

    abstract fun size(): Int

//    abstract fun summary(): Table

    fun subset(rows: Selection): Column {
        val c = this.emptyCopy()
        for (row in rows) {
            c.appendCell(getString(row))
        }
        return c
    }

    /**
     * Returns the count of missing values in this column
     */
    abstract fun countMissing(): Int

    /**
     * Returns the count of unique values in this column
     */
    abstract fun countUnique(): Int

    /**
     * Returns a column of the same type as the receiver, containing only the unique values of the receiver
     */
    abstract fun unique(): Column

    /**
     * Returns this column's ColumnType
     */
    abstract fun type(): ColumnType

    /**
     * Returns a string representation of the value at the given row
     */
    abstract fun getString(row: Int): String

    /**
     * Returns a copy of the receiver with no data. The column name and type are the same
     */
    abstract fun emptyCopy(): Column

    /**
     * Returns a deep copy of the receiver
     */
    abstract fun copy(): Column

    /**
     * Returns an empty copy of the receiver, with its internal storage initialized to the given row size
     */
    abstract fun emptyCopy(rowSize: Int): Column

    abstract fun clear()

    abstract fun sortAscending()

    abstract fun sortDescending()

    /**
     * Returns true if the column has no data
     */
    abstract fun isEmpty(): Boolean

    abstract fun appendCell(stringValue: String)

    /**
     * Returns a unique string that identifies this column
     */
    abstract fun id(): String

    /**
     * Returns a String containing the column's metadata in json format
     */
    abstract fun metadata(): String

//    abstract fun columnMetadata(): ColumnMetadata

//    abstract fun rowComparator(): IntComparator

    fun first(): String {
        return getString(0)
    }

    fun last(): String {
        return getString(size() - 1)
    }

    // abstract fun append(column: Column)

    fun first(numRows: Int): Column {
        val col = emptyCopy()
        val rows = Math.min(numRows, size())
        for (i in 0..rows - 1) {
            col.appendCell(getString(i))
        }
        return col
    }

    fun last(numRows: Int): Column {
        val col = emptyCopy()
        val rows = Math.min(numRows, size())
        for (i in size() - rows..size() - 1) {
            col.appendCell(getString(i))
        }
        return col
    }

    abstract fun print(): String

/*
    fun title(): String {
        return "Column: " + name() + '\n'
    }
*/

    fun toDoubleArray(): DoubleArray {
        throw UnsupportedOperationException("Method toDoubleArray() is not supported on non-numeric columns")
    }

    abstract fun columnWidth(): Int

//    abstract fun isMissing(): Selection

//    abstract fun isNotMissing(): Selection

    /**
     * Returns the width of a cell in this column, in bytes
     */
    abstract fun byteSize(): Int

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    //  abstract fun asBytes(rowNumber: Int): ByteArray
}

/*
    */
/**
 * Returns a new column of the same type as the receiver, such that the values in the new column
 * contain the difference between each cell in the original and it's predecessor.
 * The Missing Value Indicator is used for the first cell in the new column.
 * (e.g. IntColumn.MISSING_VALUE)
 */
/*

    abstract fun difference(): E
*/

package tech.tablesaw.kapi

import tech.tablesaw.api.ColumnType
import tech.tablesaw.api.TimeColumn
import tech.tablesaw.store.ColumnMetadata
import tech.tablesaw.util.Selection
import java.time.LocalTime

class TimeCol(val target: TimeColumn) : Col {

    override fun size(): Int = target.size()

    override fun summary(): Dataframe = Dataframe(target.summary())

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): Col = TimeCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun name(): String = target.name()

    override fun getString(row: Int): String = target.getString(row)

    override fun copy(): Col = TimeCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = TimeCol(target.emptyCopy(rowSize))

    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun isEmpty(): Boolean = target.isEmpty

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun print(): String = target.print()

    fun append(value: LocalTime) = target.append(value)
    override fun appendCell(stringValue: String) = target.appendCell(stringValue)
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

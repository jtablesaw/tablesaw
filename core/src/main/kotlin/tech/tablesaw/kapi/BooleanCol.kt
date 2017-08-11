package tech.tablesaw.kapi

import tech.tablesaw.api.BooleanColumn
import tech.tablesaw.api.ColumnType
import tech.tablesaw.store.ColumnMetadata
import tech.tablesaw.util.Selection

/**
 *
 */
class BooleanCol(val target: BooleanColumn): Col {

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

    override fun size(): Int = target.size()

    override fun summary(): Dataframe = Dataframe(target.summary())

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): Col = BooleanCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun name(): String = target.name()

    override fun getString(row: Int): String = target.getString(row)

    override fun copy(): Col = BooleanCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = BooleanCol(target.emptyCopy(rowSize))

    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun isEmpty(): Boolean = target.isEmpty

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun print(): String = target.print()

    fun countTrue(): Int = target.countTrue()

    fun countFalse(): Int = target.countFalse()

    fun isFalse(): Selection = target.isFalse

    fun isTrue(): Selection = target.isTrue

}

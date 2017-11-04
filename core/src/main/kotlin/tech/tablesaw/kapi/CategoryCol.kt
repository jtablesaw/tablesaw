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
import tech.tablesaw.columns.Column
import tech.tablesaw.store.ColumnMetadata
import tech.tablesaw.util.Selection

/**
 *
 */
class CategoryCol(val target: CategoryColumn): Col {

    override fun appendCell(stringValue: String) = target.appendCell(stringValue)

    override fun size(): Int = target.size()

    override fun summary(): Dataframe = Dataframe(target.summary())

    override fun countMissing(): Int = target.countMissing()

    override fun countUnique(): Int = target.countUnique()

    override fun unique(): Col = CategoryCol(target.unique())

    override fun type(): ColumnType = target.type()

    override fun name(): String = target.name()

    override fun getString(row: Int): String = target.getString(row)

    override fun copy(): Col = CategoryCol(target.copy())

    override fun emptyCopy(rowSize: Int): Col = CategoryCol(target.emptyCopy(rowSize))

    override fun clear() = target.clear()

    override fun sortAscending() = target.sortAscending()

    override fun sortDescending() = target.sortDescending()

    override fun isEmpty(): Boolean = target.isEmpty

    override fun id(): String = target.id()

    override fun metadataString(): String = target.metadata()

    override fun columnMetadata(): ColumnMetadata = target.columnMetadata()

    override fun print(): String = target.print()

    /**
     * Creates a new column, replacing each string in this column with a new string formed by
     * replacing any substring that matches the regex
     */
    fun replaceAll(regexArray: Array<String>, replacement: String): CategoryCol
            = CategoryCol(target.replaceAll(regexArray, replacement))


    /**
     * Splits on Whitespace and returns the lexicographically sorted result
     */
    fun tokenizeAndSort(): CategoryCol = CategoryCol(target.tokenizeAndSort())
    fun tokenizeAndSort(separator: String): CategoryCol = CategoryCol(target.tokenizeAndSort(separator))

    fun tokenizeAndRemoveDuplicates(): CategoryCol = CategoryCol(target.tokenizeAndRemoveDuplicates())

    fun isIn(vararg strings: String): Selection = target.isIn(*strings)

    fun upperCase(): CategoryCol = CategoryCol(target.upperCase())
    fun lowerCase(): CategoryCol = CategoryCol(target.lowerCase())

    fun trim(): CategoryCol = CategoryCol(target.trim())

    fun replaceAll(regex: String, replacement: String): CategoryCol = CategoryCol(target.replaceAll(regex, replacement))
    fun replaceFirst(regex: String, replacement: String): CategoryCol
            = CategoryCol(target.replaceFirst(regex, replacement))

    fun substring(start: Int, end: Int): CategoryCol = CategoryCol(target.substring(start, end))
    fun substring(start: Int): CategoryCol = CategoryCol(target.substring(start))

    fun abbreviate(maxWidth: Int): CategoryCol = CategoryCol(target.abbreviate(maxWidth))

    fun padEnd(minLength: Int, padChar: Char): CategoryCol = CategoryCol(target.padEnd(minLength, padChar))
    fun padStart(minLength: Int, padChar: Char): CategoryCol = CategoryCol(target.padStart(minLength, padChar))

    fun commonPrefix(column2: Column): CategoryCol = CategoryCol(target.commonPrefix(column2))
    fun commonSuffix(column2: Column): CategoryCol = CategoryCol(target.commonSuffix(column2))

    /**
     * Returns a column containing the levenshtein distance between the two given string columns
     */
    fun distance(column2: Column): Column = target.distance(column2)

    fun join(column2: Column, delimiter: String): CategoryCol = CategoryCol(target.join(column2, delimiter))
}

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

package tech.tablesaw.table;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.string.DataFramePrinter;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * A tabular data structure like a table in a relational database, but not formally implementing the relational algebra
 */
public abstract class Relation {

    public abstract Relation addColumn(Column... cols);

    public abstract Relation setName(String name);

    public boolean isEmpty() {
        return rowCount() == 0;
    }

    public String shape() {
        return rowCount() + " rows X " + columnCount() + " cols";
    }

    public Relation removeColumn(int columnIndex) {
        removeColumns(column(columnIndex));
        return this;
    }

    /**
     * Removes the given columns from the receiver
     */
    public abstract Relation removeColumns(Column... columns);

    public Relation removeColumns(String... columnName) {
        Column[] cols = new Column[columnName.length];
        for (int i = 0; i < columnName.length; i++) {
            cols[i] = column(columnName[i]);
        }
        removeColumns(cols);
        return this;
    }

    public abstract Table first(int nRows);

    /**
     * Returns the index of the column with the given columnName
     */
    public int columnIndex(String columnName) {
        for (int i = 0; i < columnCount(); i++) {
            if (columnNames().get(i).equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        throw new IllegalArgumentException(String.format("Column %s is not present in table %s", columnName, name
                ()));
    }

    /**
     * Returns the column with the given columnName, ignoring case
     */
    public Column column(String columnName) {
        for (Column column : columns()) {
            String name = column.name().trim();
            if (name.equalsIgnoreCase(columnName)) {
                return column;
            }
        }
        throw new IllegalStateException(String.format("Column %s does not exist in table %s", columnName, name()));
    }

    /**
     * Returns the column at columnIndex (0-based)
     *
     * @param columnIndex an integer at least 0 and less than number of columns in the relation
     * @return the column at the given index
     */
    public abstract Column column(int columnIndex);

    /**
     * Returns the number of columns in the relation
     */
    public abstract int columnCount();

    /**
     * Returns the number of rows in the relation
     */
    public abstract int rowCount();

    /**
     * Returns a list of all the columns in the relation
     */
    public abstract List<Column> columns();

    /**
     * Returns the index of the given column
     */
    public abstract int columnIndex(Column col);

    /**
     * Returns a String representing the value found at column index c and row index r
     */
    public abstract String get(int r, int c);

    /**
     * Returns the name of this relation
     */
    public abstract String name();

    /**
     * Clears all the dat in the relation, leaving the structure intact
     */
    public abstract void clear();

    public abstract List<String> columnNames();

    /**
     * Returns an array of the column types of all columns in the relation, including duplicates as appropriate,
     * and maintaining order
     */
    public ColumnType[] columnTypes() {
        ColumnType[] columnTypes = new ColumnType[columnCount()];
        for (int i = 0; i < columnCount(); i++) {
            columnTypes[i] = columns().get(i).type();
        }
        return columnTypes;
    }

    /**
     * Returns an array of column widths for printing tables
     */
    public int[] colWidths() {
        int cols = columnCount();
        int[] widths = new int[cols];

        for (int i = 0; i < columnCount(); i++) {
            widths[i] = columns().get(i).columnWidth();
        }
        return widths;
    }

    @Override
    public String toString() {
      return "Table " + name() + ": Size = " + rowCount() + " x " + columnCount();
    }

    public String print(int rowLimit) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataFramePrinter printer = new DataFramePrinter(rowLimit, baos);
      printer.print(this);
      return new String(baos.toByteArray());
    }

    public String print() {
      return print(20);
    }

    public Table structure() {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append("Table: ")
                .append(name())
                .append(" - ")
                .append(rowCount())
                .append(" observations (rows) of ")
                .append(columnCount())
                .append(" variables (cols)");

        Table structure = Table.create(nameBuilder.toString());
        structure.addColumn(new IntColumn("Index"));
        structure.addColumn(new CategoryColumn("Column Name"));
        structure.addColumn(new CategoryColumn("Type"));
        structure.addColumn(new IntColumn("Unique Values"));
        structure.addColumn(new CategoryColumn("First"));
        structure.addColumn(new CategoryColumn("Last"));

        for (Column column : columns()) {
            structure.intColumn("Index").append(columnIndex(column));
            structure.categoryColumn("Column Name").append(column.name());
            structure.categoryColumn("Type").append(column.type().name());
            structure.intColumn("Unique Values").append(column.countUnique());
            structure.categoryColumn("First").append(column.first());
            structure.categoryColumn("Last").append(column.getString(column.size() - 1));
        }
        return structure;
    }

    public String summary() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n")
                .append("Table summary for: ")
                .append(name())
                .append("\n");
        for (Column column : columns()) {
            builder.append(column.summary().print());
            builder.append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    public BooleanColumn booleanColumn(int columnIndex) {
        return (BooleanColumn) column(columnIndex);
    }

    public BooleanColumn booleanColumn(String columnName) {
        return (BooleanColumn) column(columnName);
    }

    public NumericColumn numericColumn(int columnIndex) {
        Column c = column(columnIndex);
        if (c.type() == ColumnType.CATEGORY) {
            CategoryColumn categoryColumn = (CategoryColumn) c;
            return categoryColumn.toIntColumn();
        } else if (c.type() == ColumnType.BOOLEAN) {
            BooleanColumn booleanColumn = (BooleanColumn) c;
            return booleanColumn.toIntColumn();
        }
        return (NumericColumn) column(columnIndex);
    }

    public NumericColumn numericColumn(String columnName) {
        Column c = column(columnName);
        if (c.type() == ColumnType.CATEGORY) {
            CategoryColumn categoryColumn = (CategoryColumn) c;
            return categoryColumn.toIntColumn();
        } else if (c.type() == ColumnType.BOOLEAN) {
            BooleanColumn booleanColumn = (BooleanColumn) c;
            return booleanColumn.toIntColumn();
        }
        return (NumericColumn) column(columnName);
    }

    /**
     * Returns the column with the given name cast to a NumericColumn
     * <p>
     * Shorthand for numericColumn()
     */
    public NumericColumn nCol(String columnName) {
        return numericColumn(columnName);
    }

    /**
     * Returns the column with the given name cast to a NumericColumn
     * <p>
     * Shorthand for numericColumn()
     */
    public NumericColumn nCol(int columnIndex) {
        return numericColumn(columnIndex);
    }

    public FloatColumn floatColumn(int columnIndex) {
        return (FloatColumn) column(columnIndex);
    }

    public FloatColumn floatColumn(String columnName) {
        return (FloatColumn) column(columnName);
    }

    public DoubleColumn doubleColumn(int columnIndex) {
        return (DoubleColumn) column(columnIndex);
    }

    public DoubleColumn doubleColumn(String columnName) {
        return (DoubleColumn) column(columnName);
    }

    public IntColumn intColumn(String columnName) {
        return (IntColumn) column(columnName);
    }

    public IntColumn intColumn(int columnIndex) {
        return (IntColumn) column(columnIndex);
    }

    public ShortColumn shortColumn(String columnName) {
        return (ShortColumn) column(columnName);
    }

    public ShortColumn shortColumn(int columnIndex) {
        return (ShortColumn) column(columnIndex);
    }

    public LongColumn longColumn(String columnName) {
        return (LongColumn) column(columnName);
    }

    public LongColumn longColumn(int columnIndex) {
        return (LongColumn) column(columnIndex);
    }

    public DateColumn dateColumn(int columnIndex) {
        return (DateColumn) column(columnIndex);
    }

    public DateColumn dateColumn(String columnName) {
        return (DateColumn) column(columnName);
    }

    public TimeColumn timeColumn(String columnName) {
        return (TimeColumn) column(columnName);
    }

    public TimeColumn timeColumn(int columnIndex) {
        return (TimeColumn) column(columnIndex);
    }

    public CategoryColumn categoryColumn(String columnName) {
        return (CategoryColumn) column(columnName);
    }

    public CategoryColumn categoryColumn(int columnIndex) {
        return (CategoryColumn) column(columnIndex);
    }

    public DateTimeColumn dateTimeColumn(int columnIndex) {
        return (DateTimeColumn) column(columnIndex);
    }

    public DateTimeColumn dateTimeColumn(String columnName) {
        return (DateTimeColumn) column(columnName);
    }
}

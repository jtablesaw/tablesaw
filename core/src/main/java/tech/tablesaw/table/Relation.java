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

import it.unimi.dsi.fastutil.ints.IntArrays;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.conversion.TableConverter;
import tech.tablesaw.conversion.smile.SmileConverter;
import tech.tablesaw.io.string.DataFramePrinter;
import tech.tablesaw.sorting.comparators.DescendingIntComparator;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A tabular data structure like a table in a relational database, but not formally implementing the relational algebra
 */
public abstract class Relation {

    public abstract Relation addColumns(Column<?>... cols);

    public abstract Relation setName(String name);

    public boolean isEmpty() {
        return rowCount() == 0;
    }

    public String shape() {
        return rowCount() + " rows X " + columnCount() + " cols";
    }

    public Relation removeColumns(int... columnIndexes) {
        IntArrays.quickSort(columnIndexes, DescendingIntComparator.instance());
        for (int i : columnIndexes) {
            removeColumns(column(i));
        }
        return this;
    }

    /**
     * Removes the given columns from the receiver
     */
    public abstract Relation removeColumns(Column<?>... columns);

    public Relation removeColumns(String... columnName) {
        Column<?>[] cols = new Column<?>[columnName.length];
        for (int i = 0; i < columnName.length; i++) {
            cols[i] = column(columnName[i]);
        }
        removeColumns(cols);
        return this;
    }

    public List<Column<?>> columnsOfType(ColumnType type) {
        return columns().stream()
                .filter(column -> column.type() == type)
                .collect(Collectors.toList());
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
    public Column<?> column(String columnName) {
        for (Column<?> column : columns()) {
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
    public abstract Column<?> column(int columnIndex);

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
    public abstract List<Column<?>> columns();

    /**
     * Returns the columns whose names are given in the input array
     */
    public List<Column<?>> columns(String... columnName) {
        List<Column<?>> cols = new ArrayList<>(columnName.length);
        for (String aColumnName : columnName) {
            cols.add(column(aColumnName));
        }
        return cols;
    }

    /**
     * Returns the columns whose indices are given in the input array
     */
    public List<Column<?>> columns(int... columnIndices) {
        List<Column<?>> cols = new ArrayList<>(columnIndices.length);
        for (int i : columnIndices) {
            cols.add(column(i));
        }
        return cols;
    }

    /**
     * Returns the index of the given column
     */
    public abstract int columnIndex(Column<?> col);

    /**
     * Returns the value at the given row and column indexes
     *
     * @param r the row index, 0 based
     * @param c the column index, 0 based
     */
    public Object get(int r, int c) {
        Column<?> column = column(c);
        return column.get(r);
    }

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
        return print();
    }

    public String printAll() {
        return print(rowCount());
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
        structure.addColumns(DoubleColumn.create("Index"));
        structure.addColumns(StringColumn.create("Column Name"));
        structure.addColumns(StringColumn.create("Type"));
        structure.addColumns(DoubleColumn.create("Unique Values"));
        structure.addColumns(StringColumn.create("First"));
        structure.addColumns(StringColumn.create("Last"));

        for (Column<?> column : columns()) {
            structure.intColumn("Index").append(columnIndex(column));
            structure.stringColumn("Column Name").append(column.name());
            structure.stringColumn("Type").append(column.type().name());
            structure.intColumn("Unique Values").append(column.countUnique());
            structure.stringColumn("First").append(column.getString(0));
            structure.stringColumn("Last").append(column.getString(column.size() - 1));
        }
        return structure;
    }

    public String summary() {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator())
                .append("Table summary for: ")
                .append(name())
                .append(System.lineSeparator());
        for (Column<?> column : columns()) {
            builder.append(column.summary().print());
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    public BooleanColumn booleanColumn(int columnIndex) {
        return (BooleanColumn) column(columnIndex);
    }

    public BooleanColumn booleanColumn(String columnName) {
        return (BooleanColumn) column(columnName);
    }

    /**
     * Returns the NumberColumn at the given index.
     * If the index points to a String or a boolean column, a new NumberColumn is created and returned
     * TODO(lwhite):Consider separating the indexed access and the column type mods, which must be for ML functions (in smile or elsewhere)
     * @param columnIndex The 0-based index of a column in the table
     * @return A number column
     * @throws ClassCastException if the cast to NumberColumn fails
     */
    public NumberColumn<?> numberColumn(int columnIndex) {
        Column<?> c = column(columnIndex);
        if (c.type() == ColumnType.STRING) {
            return ((StringColumn) c).asDoubleColumn();
        } else if (c.type() == ColumnType.BOOLEAN) {
            return ((BooleanColumn) c).asDoubleColumn();
        } else if (c.type() == ColumnType.LOCAL_DATE) {
            return ((DateColumn) c).asDoubleColumn();
        } else if (c.type() == ColumnType.LOCAL_DATE_TIME) {
            return ((DateTimeColumn) c).asDoubleColumn();
        } else if (c.type() == ColumnType.INSTANT) {
            return ((InstantColumn) c).asDoubleColumn();
        } else if (c.type() == ColumnType.LOCAL_TIME) {
            return ((TimeColumn) c).asDoubleColumn();
        }
        return (NumberColumn<?>) column(columnIndex);
    }

    public NumberColumn<?> numberColumn(String columnName) {
        return numberColumn(columnIndex(columnName));
    }

    public DoubleColumn doubleColumn(String columnName) {
        return doubleColumn(columnIndex(columnName));
    }

    public DoubleColumn doubleColumn(int columnIndex) {
        return (DoubleColumn) column(columnIndex);
    }

    public StringColumn[] stringColumns() {
        return columns().stream().filter(e->e.type() == ColumnType.STRING).toArray(StringColumn[]::new);
    }

    public NumericColumn<?>[] numberColumns() {
        return columns().stream().filter(e->e instanceof NumericColumn<?>).toArray(NumericColumn[]::new);
    }

    /**
     * Returns all the NumericColumns in the relation
     */
    public List<NumericColumn<?>> numericColumns() {
        return Arrays.asList(numberColumns());
    }

    /**
     * Returns all the NumericColumns in the relation
     */
    public List<NumericColumn<?>> numericColumns(int... columnIndices) {
        List<NumericColumn<?>> cols = new ArrayList<>();
        for (int i : columnIndices) {
            cols.add(numberColumn(i));
        }

        return cols;
    }

    /**
     * Returns all the NumericColumns in the relation
     */
    public List<NumericColumn<?>> numericColumns(String ... columnNames) {
        List<NumericColumn<?>> cols = new ArrayList<>();
        for (String name : columnNames) {
            cols.add(numberColumn(name));
        }

        return cols;
    }


    public BooleanColumn[] booleanColumns() {
        return columns().stream().filter(e -> e.type() == ColumnType.BOOLEAN).toArray(BooleanColumn[]::new);
    }

    public DateColumn[] dateColumns() {
        return columns().stream().filter(e -> e.type() == ColumnType.LOCAL_DATE).toArray(DateColumn[]::new);
    }

    public DateTimeColumn[] dateTimeColumns() {
        return columns().stream().filter(e -> e.type() == ColumnType.LOCAL_DATE_TIME).toArray(DateTimeColumn[]::new);
    }

    public InstantColumn[] instantColumns() {
        return columns().stream().filter(e -> e.type() == ColumnType.INSTANT).toArray(InstantColumn[]::new);
    }

    public TimeColumn[] timeColumns() {
        return columns().stream().filter(e -> e.type() == ColumnType.LOCAL_TIME).toArray(TimeColumn[]::new);
    }

    public CategoricalColumn<?> categoricalColumn(String columnName) {
        return (CategoricalColumn<?>) column(columnName);
    }

    public CategoricalColumn<?> categoricalColumn(int columnNumber) {
        return (CategoricalColumn<?>) column(columnNumber);
    }

    /**
     * Returns the columns whose names are given in the input array
     */
    public List<CategoricalColumn<?>> categoricalColumns(String... columnName) {
        List<CategoricalColumn<?>> cols = new ArrayList<>(columnName.length);
        for (String aColumnName : columnName) {
            cols.add(categoricalColumn(aColumnName));
        }
        return cols;
    }

    
    /**
     * Returns the column with the given name cast to a NumberColumn
     * <p>
     * Shorthand for numberColumn()
     */
    public NumberColumn<?> nCol(String columnName) {
        return numberColumn(columnName);
    }

    /**
     * Returns the column with the given name cast to a NumberColumn
     * <p>
     * Shorthand for numberColumn()
     */
    public NumberColumn<?> nCol(int columnIndex) {
        return numberColumn(columnIndex);
    }

    public IntColumn intColumn(String columnName) {
        return intColumn(columnIndex(columnName));
    }

    public IntColumn intColumn(int columnIndex) {
        return (IntColumn) column(columnIndex);
    }
    
    public ShortColumn shortColumn(String columnName) {
        return shortColumn(columnIndex(columnName));
    }

    public ShortColumn shortColumn(int columnIndex) {
        return (ShortColumn) column(columnIndex);
    }

    public LongColumn longColumn(String columnName) {
        return longColumn(columnIndex(columnName));
    }

    public LongColumn longColumn(int columnIndex) {
        return (LongColumn) column(columnIndex);
    }
    
    public FloatColumn floatColumn(String columnName) {
        return floatColumn(columnIndex(columnName));
    }

    public FloatColumn floatColumn(int columnIndex) {
        return (FloatColumn) column(columnIndex);
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

    public StringColumn stringColumn(String columnName) {
        return (StringColumn) column(columnName);
    }

    public StringColumn stringColumn(int columnIndex) {
        return (StringColumn) column(columnIndex);
    }

    public TextColumn textColumn(String columnName) {
        return (TextColumn) column(columnName);
    }

    public TextColumn textColumn(int columnIndex) {
        return (TextColumn) column(columnIndex);
    }

    public DateTimeColumn dateTimeColumn(int columnIndex) {
        return (DateTimeColumn) column(columnIndex);
    }

    public DateTimeColumn dateTimeColumn(String columnName) {
        return (DateTimeColumn) column(columnName);
    }

    public InstantColumn instantColumn(int columnIndex) {
        return (InstantColumn) column(columnIndex);
    }

    public InstantColumn instantColumn(String columnName) {
        return (InstantColumn) column(columnName);
    }

    public TableConverter as() {
        return new TableConverter(this);
    }

    public SmileConverter smile() {
        return new SmileConverter(this);
    }

    /**
     * Returns a string representation of the value at the given row and column indexes
     *
     * @param r the row index, 0 based
     * @param c the column index, 0 based
     */
    public String getUnformatted(int r, int c) {
        Column<?> column = column(c);
        return column.getUnformattedString(r);
    }

    /**
     * Returns a string representation of the value at the given row and column indexes
     *
     * @param r          the row index, 0 based
     * @param columnName the name of the column to be returned
     *                   <p>
     *                   // TODO: performance would be enhanced if columns could be referenced via a hashTable
     */
    public String getString(int r, String columnName) {
        return getString(r, columnIndex(columnName));
    }

    /**
     * Returns a string representation of the value at the given row and column indexes
     *
     * @param r          the row index, 0 based
     * @param columnIndex the index of the column to be returned
     *                   <p>
     *                   // TODO: performance would be enhanced if columns could be referenced via a hashTable
     */
    public String getString(int r, int columnIndex) {
        Column<?> column = column(columnIndex);
        return column.getString(r);
    }

    public boolean containsColumn(Column<?> column) {
        return columns().contains(column);
    }
}

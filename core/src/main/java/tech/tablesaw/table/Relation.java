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
public interface Relation {

    void addColumn(Column... cols);

    void setName(String name);

    default boolean isEmpty() {
        return rowCount() == 0;
    }

    default String shape() {
        return rowCount() + " rows X " + columnCount() + " cols";
    }

    default void removeColumn(int columnIndex) {
        removeColumns(column(columnIndex));
    }

    /**
     * Removes the given columns from the receiver
     */
    void removeColumns(Column... columns);

    default void removeColumns(String... columnName) {
        Column[] cols = new Column[columnName.length];
        for (int i = 0; i < columnName.length; i++) {
            cols[i] = column(columnName[i]);
        }
        removeColumns(cols);
    }

    Table first(int nRows);

    /**
     * Returns the index of the column with the given columnName
     */
    default int columnIndex(String columnName) {
        int columnIndex = -1;
        for (int i = 0; i < columnCount(); i++) {
            if (columnNames().get(i).equalsIgnoreCase(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            throw new IllegalArgumentException(String.format("Column %s is not present in table %s", columnName, name
                    ()));
        }
        return columnIndex;
    }

    /**
     * Returns the column with the given columnName, ignoring case
     */
    default Column column(String columnName) {
        Column result = null;
        for (Column column : columns()) {
            String name = column.name().trim();
            if (name.equalsIgnoreCase(columnName)) {
                result = column;
                break;
            }
        }
        if (result == null) {
            throw new IllegalStateException(String.format("Column %s does not exist in table %s", columnName, name()));
        }
        return result;
    }

    /**
     * Returns the column at columnIndex (0-based)
     *
     * @param columnIndex an integer at least 0 and less than number of columns in the relation
     * @return the column at the given index
     */
    Column column(int columnIndex);

    /**
     * Returns the number of columns in the relation
     */
    int columnCount();

    /**
     * Returns the number of rows in the relation
     */
    int rowCount();

    /**
     * Returns a list of all the columns in the relation
     */
    List<Column> columns();

    /**
     * Returns the index of the given column
     */
    int columnIndex(Column col);

    /**
     * Returns a String representing the value found at column index c and row index r
     */
    String get(int c, int r);

    /**
     * Returns the name of this relation
     */
    String name();

    /**
     * Clears all the dat in the relation, leaving the structure intact
     */
    void clear();

    List<String> columnNames();

    /**
     * Returns an array of the column types of all columns in the relation, including duplicates as appropriate,
     * and maintaining order
     */
    default ColumnType[] columnTypes() {
        ColumnType[] columnTypes = new ColumnType[columnCount()];
        for (int i = 0; i < columnCount(); i++) {
            columnTypes[i] = columns().get(i).type();
        }
        return columnTypes;
    }

    /**
     * Returns an array of column widths for printing tables
     */
    default int[] colWidths() {
        int cols = columnCount();
        int[] widths = new int[cols];

        for (int i = 0; i < columnCount(); i++) {
            widths[i] = columns().get(i).columnWidth();
        }
        return widths;
    }

    default String print() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataFramePrinter printer = new DataFramePrinter(Integer.MAX_VALUE, baos);
        printer.print(this);
        return new String(baos.toByteArray());
    }

    default Table structure() {

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
            structure.categoryColumn("Column Name").add(column.name());
            structure.categoryColumn("Type").add(column.type().name());
            structure.intColumn("Unique Values").append(column.countUnique());
            structure.categoryColumn("First").add(column.first());
            structure.categoryColumn("Last").add(column.getString(column.size() - 1));
        }
        return structure;
    }

    default String summary() {
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

    default BooleanColumn booleanColumn(int columnIndex) {
        return (BooleanColumn) column(columnIndex);
    }

    default BooleanColumn booleanColumn(String columnName) {
        return (BooleanColumn) column(columnName);
    }

    default NumericColumn numericColumn(int columnIndex) {
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

    default NumericColumn numericColumn(String columnName) {
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
    default NumericColumn nCol(String columnName) {
        return numericColumn(columnName);
    }

    /**
     * Returns the column with the given name cast to a NumericColumn
     * <p>
     * Shorthand for numericColumn()
     */
    default NumericColumn nCol(int columnIndex) {
        return numericColumn(columnIndex);
    }

    default FloatColumn floatColumn(int columnIndex) {
        return (FloatColumn) column(columnIndex);
    }

    default FloatColumn floatColumn(String columnName) {
        return (FloatColumn) column(columnName);
    }

    default DoubleColumn doubleColumn(int columnIndex) {
        return (DoubleColumn) column(columnIndex);
    }

    default DoubleColumn doubleColumn(String columnName) {
        return (DoubleColumn) column(columnName);
    }

    default IntColumn intColumn(String columnName) {
        return (IntColumn) column(columnName);
    }

    default IntColumn intColumn(int columnIndex) {
        return (IntColumn) column(columnIndex);
    }

    default ShortColumn shortColumn(String columnName) {
        return (ShortColumn) column(columnName);
    }

    default ShortColumn shortColumn(int columnIndex) {
        return (ShortColumn) column(columnIndex);
    }

    default LongColumn longColumn(String columnName) {
        return (LongColumn) column(columnName);
    }

    default LongColumn longColumn(int columnIndex) {
        return (LongColumn) column(columnIndex);
    }

    default DateColumn dateColumn(int columnIndex) {
        return (DateColumn) column(columnIndex);
    }

    default DateColumn dateColumn(String columnName) {
        return (DateColumn) column(columnName);
    }

    default TimeColumn timeColumn(String columnName) {
        return (TimeColumn) column(columnName);
    }

    default TimeColumn timeColumn(int columnIndex) {
        return (TimeColumn) column(columnIndex);
    }

    default CategoryColumn categoryColumn(String columnName) {
        return (CategoryColumn) column(columnName);
    }

    default CategoryColumn categoryColumn(int columnIndex) {
        return (CategoryColumn) column(columnIndex);
    }

    default DateTimeColumn dateTimeColumn(int columnIndex) {
        return (DateTimeColumn) column(columnIndex);
    }

    default DateTimeColumn dateTimeColumn(String columnName) {
        return (DateTimeColumn) column(columnName);
    }
}

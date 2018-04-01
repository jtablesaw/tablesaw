package tech.tablesaw.joining;

import com.google.common.collect.Streams;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.index.CategoryIndex;
import tech.tablesaw.index.IntIndex;
import tech.tablesaw.index.LongIndex;
import tech.tablesaw.util.selection.Selection;

public class DataFrameJoiner {

    private final Table table;
    private final Column column;

    public DataFrameJoiner(Table table, String column) {
        this.table = table;
        this.column = table.column(column);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table inner(Table table2, String col2Name) {
        Table result = emptyTableFromColumns(table, table2, col2Name);
        if (column instanceof DateColumn) {
            IntIndex index = new IntIndex(table2.dateColumn(col2Name));
            DateColumn col1 = (DateColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                int value = col1.getIntInternal(i);
                Table table1Rows = table.selectWhere(Selection.withRow(i));
                Table table2Rows = table2.selectWhere(index.get(value));
                table2Rows.removeColumns(col2Name);
                crossProduct(result, table1Rows, table2Rows);
            }
        } else if (column instanceof DateTimeColumn) {
            LongIndex index = new LongIndex(table2.dateTimeColumn(col2Name));
            DateTimeColumn col1 = (DateTimeColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                long value = col1.getLongInternal(i);
                Table table1Rows = table.selectWhere(Selection.withRow(i));
                Table table2Rows = table2.selectWhere(index.get(value));
                table2Rows.removeColumns(col2Name);
                crossProduct(result, table1Rows, table2Rows);
            }
        } else if (column instanceof TimeColumn) {
            IntIndex index = new IntIndex(table2.timeColumn(col2Name));
            TimeColumn col1 = (TimeColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                int value = col1.getIntInternal(i);
                Table table1Rows = table.selectWhere(Selection.withRow(i));
                Table table2Rows = table2.selectWhere(index.get(value));
                table2Rows.removeColumns(col2Name);
                crossProduct(result, table1Rows, table2Rows);
            }
        } else if (column instanceof StringColumn) {
            CategoryIndex index = new CategoryIndex(table2.stringColumn(col2Name));
            StringColumn col1 = (StringColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                String value = col1.get(i);
                Table table1Rows = table.selectWhere(Selection.withRow(i));
                Table table2Rows = table2.selectWhere(index.get(value));
                table2Rows.removeColumns(col2Name);
                crossProduct(result, table1Rows, table2Rows);
            }
        } else if (column instanceof NumberColumn) {
            LongIndex index = new LongIndex(table2.numberColumn(col2Name));
            NumberColumn col1 = (NumberColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                long value = col1.getLong(i);
                Table table1Rows = table.selectWhere(Selection.withRow(i));
                Table table2Rows = table2.selectWhere(index.get(value));
                table2Rows.removeColumns(col2Name);
                crossProduct(result, table1Rows, table2Rows);
            }
        } else {
            throw new IllegalArgumentException(
                    "Joining is supported on numeric, string, and date-like columns. Column "
                            + column.name() + " is of type " + column.type());
        }

        return result;
    }

    private Table emptyTableFromColumns(Table table1, Table table2, String col2Name) {
        Column[] cols = Streams.concat(
                table1.columns().stream(),
                table2.columns().stream().filter(c -> !c.name().equals(col2Name))
        ).map(col -> col.emptyCopy(col.size())).toArray(Column[]::new);
        return Table.create(table1.name(), cols);
    }

    private void crossProduct(Table destination, Table table1, Table table2) {
        for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
            for (int r1 = 0; r1 < table1.rowCount(); r1++) {
                for (int r2 = 0; r2 < table2.rowCount(); r2++) {
                    if (c < table1.columnCount()) {
                        destination.column(c).appendCell(table1.getUnformatted(r1, c));
                    } else {
                        destination.column(c).appendCell(table2.getUnformatted(r2, c - table1.columnCount()));
                    }
                }
            }
        }
    }

}

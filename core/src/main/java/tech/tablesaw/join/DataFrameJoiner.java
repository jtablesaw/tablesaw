package tech.tablesaw.join;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.google.common.collect.Streams;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.index.CategoryIndex;
import tech.tablesaw.index.DateIndex;
import tech.tablesaw.index.DateTimeIndex;
import tech.tablesaw.index.IntIndex;
import tech.tablesaw.index.LongIndex;
import tech.tablesaw.index.TimeIndex;

public class DataFrameJoiner {

  private final Table table;
  private final Column column;
  
  public DataFrameJoiner(Table table, String column) {
    this.table = table;
    this.column = table.column(column);
  }

  public Table inner(Table table2, String col2Name) {
    Table result = emptyTableFromColumns(table, table2, col2Name);
    if (column instanceof DateColumn) {
      DateIndex index = new DateIndex(table2.dateColumn(col2Name));
      DateColumn col1 = (DateColumn) column;
      for (int i = 0; i < col1.size(); i++) {
        LocalDate value = col1.get(i);
        Table table1Rows = table.selectRow(i);
        Table table2Rows = table2.selectWhere(index.get(value));
        table2Rows.removeColumns(col2Name);
        crossProduct(result, table1Rows, table2Rows);
      }
    } else if (column instanceof DateTimeColumn) {
      DateTimeIndex index = new DateTimeIndex(table2.dateTimeColumn(col2Name));
      DateTimeColumn col1 = (DateTimeColumn) column;
      for (int i = 0; i < col1.size(); i++) {
        LocalDateTime value = col1.get(i);
        Table table1Rows = table.selectRow(i);
        Table table2Rows = table2.selectWhere(index.get(value));
        table2Rows.removeColumns(col2Name);
        crossProduct(result, table1Rows, table2Rows);
      }
    } else if (column instanceof TimeColumn) {
      TimeIndex index = new TimeIndex(table2.timeColumn(col2Name));
      TimeColumn col1 = (TimeColumn) column;
      for (int i = 0; i < col1.size(); i++) {
        LocalTime value = col1.get(i);
        Table table1Rows = table.selectRow(i);
        Table table2Rows = table2.selectWhere(index.get(value));
        table2Rows.removeColumns(col2Name);
        crossProduct(result, table1Rows, table2Rows);
      }
    } else if (column instanceof CategoryColumn) {
      CategoryIndex index = new CategoryIndex(table2.categoryColumn(col2Name));
      CategoryColumn col1 = (CategoryColumn) column;
      for (int i = 0; i < col1.size(); i++) {
        String value = col1.get(i);
        Table table1Rows = table.selectRow(i);
        Table table2Rows = table2.selectWhere(index.get(value));
        table2Rows.removeColumns(col2Name);
        crossProduct(result, table1Rows, table2Rows);
      }
    } else if (column instanceof LongColumn) {
      LongIndex index = new LongIndex(table2.longColumn(col2Name));
      LongColumn col1 = (LongColumn) column;
      for (int i = 0; i < col1.size(); i++) {
        long value = col1.get(i);
        Table table1Rows = table.selectRow(i);
        Table table2Rows = table2.selectWhere(index.get(value));
        table2Rows.removeColumns(col2Name);
        crossProduct(result, table1Rows, table2Rows);
      }
    } else if (column instanceof IntColumn) {
      IntIndex index = new IntIndex(table2.intColumn(col2Name));
      IntColumn col1 = (IntColumn) column;
      for (int i = 0; i < col1.size(); i++) {
        int value = col1.get(i);
        Table table1Rows = table.selectRow(i);
        Table table2Rows = table2.selectWhere(index.get(value));
        table2Rows.removeColumns(col2Name);
        crossProduct(result, table1Rows, table2Rows);
      }
    } else {
      throw new IllegalArgumentException(
          "Joining is supported on int, long, category, and date-like columns. Column "
              + column.name() + " is of type " + column.type());
    }

    return result;
  }

  private Table emptyTableFromColumns(Table table1, Table table2, String col2Name) {
    Column[] cols = Streams.concat(
            table1.columns().stream(),
            table2.columns().stream().filter(c -> !c.name().equals(col2Name))
       ).map(col -> col.columnMetadata().createColumn()).toArray(size -> new Column[size]);
    return Table.create(table1.name(), cols);
  }

  private void crossProduct(Table destination, Table table1, Table table2) {
    for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
      for (int r1 = 0; r1 < table1.rowCount(); r1++) {
        for (int r2 = 0; r2 < table2.rowCount(); r2++) {
          if (c < table1.columnCount()) {
            destination.column(c).appendCell(table1.get(r1, c));
          } else {
            destination.column(c).appendCell(table2.get(r2, c - table1.columnCount()));
          }
        }
      }
    }
  }

}

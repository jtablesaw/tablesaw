package tech.tablesaw.join;

import java.time.LocalDate;

import com.google.common.collect.Streams;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.index.DateIndex;

public class DataFrameJoiner {

  private final Table table;
  private final Column column;
  
  public DataFrameJoiner(Table table, String column) {
    this.table = table;
    this.column = table.column(column);
  }

  public Table inner(Table table2, String col2Name) {
    DateIndex index;
    if (column instanceof DateColumn) {
      index = new DateIndex(table2.dateColumn(col2Name));
    } else {
      throw new IllegalArgumentException(
          "Only joining on date columns is supported so far");
    }

    Table result = emptyTableFromColumns(table, table2, col2Name);

    DateColumn col1 = (DateColumn) column;
    for (int i = 0; i < col1.size(); i++) {
      LocalDate date = col1.get(i);
      Table table1Rows = table.selectRow(i);
      Table table2Rows = table2.selectWhere(index.get(date));
      table2Rows.removeColumns(col2Name);
      crossProduct(result, table1Rows, table2Rows);
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
      for (int r = 0; r < table1.rowCount() * table2.rowCount(); r++) {
        if (c < table1.columnCount()) {
          destination.column(c).appendCell(table1.get(r, c));          
        } else {
          destination.column(c).appendCell(table2.get(r, c - table1.columnCount()));
        }
      }    
    }
  }

}

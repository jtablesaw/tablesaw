package com.github.lwhite1.tablesaw.table;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.api.TimeColumn;
import com.github.lwhite1.tablesaw.api.LongColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.ShortColumn;

import java.util.List;

/**
 * A specialization of the standard Relation used for tables formed by grouping operations on a Relation
 */
public class SubTable extends Table {

  /**
   * The values that will be summarized on
   */
  private List<String> values;

  /**
   * Returns a new SubTable from the given table that will include summaries for the given values
   *
   * @param original The table from which this one was derived
   */
  SubTable(Table original) {
    super(original.name(),
        original.emptyCopy().columns().toArray(new Column[original.columnCount()]));
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  /**
   * Adds a single row to this table from sourceTable, copying every column in sourceTable
   */
  void addRow(int rowIndex, Table sourceTable) {
    for (int i = 0; i < columnCount(); i++) {
      Column column = column(i);
      ColumnType type = column.type();
      switch (type) {
        case FLOAT:
          FloatColumn floatColumn = (FloatColumn) column;
          floatColumn.add(sourceTable.floatColumn(i).get(rowIndex));
          break;
        case INTEGER:
          IntColumn intColumn = (IntColumn) column;
          intColumn.add(sourceTable.intColumn(i).get(rowIndex));
          break;
        case SHORT_INT:
          ShortColumn shortColumn = (ShortColumn) column;
          shortColumn.add(sourceTable.shortColumn(i).get(rowIndex));
          break;
        case LONG_INT:
          LongColumn longColumn = (LongColumn) column;
          longColumn.add(sourceTable.longColumn(i).get(rowIndex));
          break;
        case BOOLEAN:
          BooleanColumn booleanColumn = (BooleanColumn) column;
          booleanColumn.add(sourceTable.booleanColumn(i).get(rowIndex));
          break;
        case LOCAL_DATE:
          DateColumn localDateColumn = (DateColumn) column;
          localDateColumn.add(sourceTable.dateColumn(i).getInt(rowIndex));
          break;
        case LOCAL_TIME:
          TimeColumn timeColumn = (TimeColumn) column;
          timeColumn.add(sourceTable.timeColumn(i).getInt(rowIndex));
          break;
        case LOCAL_DATE_TIME:
          DateTimeColumn localDateTimeColumn = (DateTimeColumn) column;
          localDateTimeColumn.add(sourceTable.dateTimeColumn(i).getLong(rowIndex));
          break;
        case CATEGORY:
          CategoryColumn categoryColumn = (CategoryColumn) column;
          categoryColumn.add(sourceTable.categoryColumn(i).get(rowIndex));
          break;
        default:
          throw new RuntimeException("Unhandled column type updating columns");
      }
    }
  }
}

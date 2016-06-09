package com.deathrayresearch.outlier.io;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import org.junit.Test;

import static com.deathrayresearch.outlier.columns.ColumnType.*;
import static com.deathrayresearch.outlier.api.QueryHelper.column;

/**
 *
 */
public class IoTest1 {

  @Test
  public void testWithBusData() throws Exception {
    // Read the CSV file
    ColumnType[] types = {INTEGER, CAT, CAT, FLOAT, FLOAT};
    Table table = CsvReader.read(types, "data/bus_stop_test.csv");

    // Look at the column names
    print(table.columnNames());

    print(table.head(3).print());

    table = table.sortDescendingOn("stop_id");
    print(table.head(3).print());
    table.removeColumns("stop_desc");
    print(table.columnNames());

    Column c = table.floatColumn("stop_lat");

    print(table.floatColumn("stop_lon").describe());

    View v = table.select("stop_lon", "stop_id").where(column("stop_lon").isGreaterThan(-0.1f)).run();
    print(v.print());
    print(v.rowCount());
  }

  @Test
  public void testWithBushData() throws Exception {
    // Read the CSV file
    ColumnType[] types = {LOCAL_DATE, INTEGER, CAT};
    Table table = CsvReader.read(types, "data/BushApproval.csv");

    // Look at the column names
    print(table.columnNames());

    print(table.print());
    print(table.rowCount());
  }

  private void print(Object o) {
    System.out.println(o);
  }
}

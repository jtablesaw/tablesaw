package com.deathrayresearch.outlier.io;

import com.deathrayresearch.outlier.Column;
import com.deathrayresearch.outlier.ColumnType;
import com.deathrayresearch.outlier.FloatColumn;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import org.junit.Test;

import static com.deathrayresearch.outlier.ColumnType.*;
import static com.deathrayresearch.outlier.QueryUtil.valueOf;

/**
 *
 */
public class IoTest1 {

  @Test
  public void testWithBusData() throws Exception {
    // Read the CSV file
    ColumnType[] types = {INTEGER, TEXT, TEXT, FLOAT, FLOAT};
    Table table = CsvReader.read("data/bus_stop_test.csv", types);

    // Look at the column names
    print(table.columnNames());

    print(table.head(3).print());

    table = table.sortDescendingOn("stop_id");
    print(table.head(3).print());
    table.removeColumn("stop_desc");
    print(table.columnNames());

    Column c = table.floatColumn("stop_lat");

    print(table.floatColumn("stop_lon").describe());

    View v = table.select("stop_lon", "stop_id").where(valueOf("stop_lon").isGreaterThan(-0.1f)).run();
    print(v.print());
    print(v.rowCount());
  }

  @Test
  public void testWithBushData() throws Exception {
    // Read the CSV file
    ColumnType[] types = {LOCAL_DATE, INTEGER, CAT};
    Table table = CsvReader.read("data/BushApproval.csv", types);

    // Look at the column names
    print(table.columnNames());

    print(table.print());
    print(table.rowCount());
  }

  private void print(Object o) {
    System.out.println(o);
  }
}

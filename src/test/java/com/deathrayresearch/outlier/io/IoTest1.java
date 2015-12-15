package com.deathrayresearch.outlier.io;

import com.deathrayresearch.outlier.ColumnType;
import com.deathrayresearch.outlier.Table;
import org.junit.Test;

import static com.deathrayresearch.outlier.ColumnType.*;

/**
 *
 */
public class IoTest1 {

  @Test
  public void testWithBusData() throws Exception {
    // Read the CSV file
    ColumnType[] types = {FLOAT, TEXT, TEXT, FLOAT, FLOAT};
    Table table = CsvReader.read("data/bus_stop_test.csv", types);

    // Look at the column names
    print(table.columnNames());

    print(table.print());
  }

  @Test
  public void testWithBushData() throws Exception {
    // Read the CSV file
    ColumnType[] types = {LOCAL_DATE, INTEGER, CAT};
    Table table = CsvReader.read("data/BushApproval.csv", types);

    // Look at the column names
    print(table.columnNames());

    print(table.print());
  }

  private void print(Object o) {
    System.out.println(o);
  }
}

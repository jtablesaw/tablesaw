package com.github.lwhite1.tablesaw.io;

import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import org.junit.Test;

import java.util.Arrays;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static com.github.lwhite1.tablesaw.api.QueryHelper.column;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for CSV Reading
 */
public class IoTest1 {

  private final ColumnType[] bus_types = {SHORT_INT, CATEGORY, CATEGORY, FLOAT, FLOAT};

  @Test
  public void testWithBusData() throws Exception {
    // Read the CSV file
    Table table = CsvReader.read(bus_types, "data/bus_stop_test.csv");

    // Look at the column names
    assertEquals("[stop_id, stop_name, stop_desc, stop_lat, stop_lon]", table.columnNames().toString());

    table = table.sortDescendingOn("stop_id");
    table.removeColumns("stop_desc");

    Column c = table.floatColumn("stop_lat");
    Table v = table.selectWhere(column("stop_lon").isGreaterThan(-0.1f));
  }

  @Test
  public void testWithBushData() throws Exception {

    // Read the CSV file
    ColumnType[] types = {LOCAL_DATE, INTEGER, CATEGORY};
    Table table = CsvReader.read(types, "data/BushApproval.csv");

    // Look at the column names
    assertEquals("[date, approval, who]", table.columnNames().toString());
  }

  @Test
  public void testDataTypeDetection() throws Exception {
    ColumnType[] columnTypes = CsvReader.detectColumnTypes("data/bus_stop_test.csv", true, ',');
    assertTrue(Arrays.equals(bus_types, columnTypes));
  }

  @Test
  public void testDataTypeDetection2() throws Exception {
    ColumnType[] columnTypes = CsvReader.detectColumnTypes("data/BushApproval.csv", true, ',');
    assertEquals(ColumnType.LOCAL_DATE, columnTypes[0]);
    assertEquals(ColumnType.SHORT_INT, columnTypes[1]);
    assertEquals(ColumnType.CATEGORY, columnTypes[2]);
  }
}

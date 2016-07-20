package com.github.lwhite1.tablesaw.io.csv;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static com.github.lwhite1.tablesaw.api.QueryHelper.column;
import static org.junit.Assert.*;

/**
 * Tests for CSV Reading
 */
public class CsvReaderTest {

  private final ColumnType[] bus_types = {SHORT_INT, CATEGORY, CATEGORY, FLOAT, FLOAT};

  @Test
  public void testWithBusData() throws Exception {
    // Read the CSV file
    Table table = CsvReader.read(bus_types, true, ',', "data/bus_stop_test.csv");

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
    ColumnType[] types = {LOCAL_DATE, SHORT_INT, CATEGORY};
    Table table = CsvReader.read(types, "data/BushApproval.csv");

    assertEquals(323, table.rowCount());

    // Look at the column names
    assertEquals("[date, approval, who]", table.columnNames().toString());
  }

  @Test
  public void testDataTypeDetection() throws Exception {
    ColumnType[] columnTypes = CsvReader.detectColumnTypes("data/bus_stop_test.csv", true, ',');
    assertTrue(Arrays.equals(bus_types, columnTypes));
  }

  @Test
  public void testPrintStructure() throws Exception {
    String output =
        "ColumnType[] columnTypes = {\n" +
        "LOCAL_DATE, // 0     date        \n" +
        "SHORT_INT,  // 1     approval    \n" +
        "CATEGORY,   // 2     who         \n" +
        "}\n";
    assertEquals(output, CsvReader.printColumnTypes("data/BushApproval.csv", true, ','));
  }

  @Test
  public void testDataTypeDetection2() throws Exception {
    ColumnType[] columnTypes = CsvReader.detectColumnTypes("data/BushApproval.csv", true, ',');
    assertEquals(ColumnType.LOCAL_DATE, columnTypes[0]);
    assertEquals(ColumnType.SHORT_INT, columnTypes[1]);
    assertEquals(ColumnType.CATEGORY, columnTypes[2]);
  }

  @Ignore
  @Test
  public void testLoadFromUrl() throws Exception {
    ColumnType[] types = {LOCAL_DATE, SHORT_INT, CATEGORY};
    String location = "https://raw.githubusercontent.com/lwhite1/tablesaw/master/data/BushApproval.csv";
    Table table;
    try (InputStream input = new URL(location).openStream()) {
      table = Table.createFromStream(types, true, ',', input, "Bush approval ratings");
    }
    assertNotNull(table);
  }
}

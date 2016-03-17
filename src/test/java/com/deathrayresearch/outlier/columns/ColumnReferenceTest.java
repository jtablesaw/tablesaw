package com.deathrayresearch.outlier.columns;

import static com.deathrayresearch.outlier.columns.ColumnType.*;
import static com.deathrayresearch.outlier.QueryUtil.valueOf;

import static org.junit.Assert.*;

import com.deathrayresearch.outlier.Query;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Test;

/**
 *
 */
public class ColumnReferenceTest {

  @Test
  public void testColumn() throws Exception {
    ColumnType[] types = {FLOAT, TEXT, TEXT, TEXT, FLOAT};
    Table table = CsvReader.read(types, "data/bus_stop_test.csv");
    Query query = table.select();
    assertNotNull(query);
    View view = query.run();
    System.out.println(view.columnNames());
    assertFalse(view.columnNames().isEmpty());
    assertEquals(table.rowCount(), view.rowCount());

    query = table.select("stop_name", "stop_id").where(valueOf("stop_name").isEqualTo("cat"));

    View view1 = query.run();
    System.out.println(view1.columnNames());
    assertEquals(0, view1.rowCount());
  }
}
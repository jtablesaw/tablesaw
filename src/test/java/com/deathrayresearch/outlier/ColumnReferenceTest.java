package com.deathrayresearch.outlier;

import static com.deathrayresearch.outlier.ColumnType.*;
import static com.deathrayresearch.outlier.QueryUtil.valueOf;

import static org.junit.Assert.*;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Test;

/**
 *
 */
public class ColumnReferenceTest {

  @Test
  public void testColumn() throws Exception {
    ColumnType[] types = {FLOAT, TEXT, TEXT, TEXT, FLOAT};
    Table table = CsvReader.read("data/bus_stop_test.csv", types);
    Query query = table.select();
    assertNotNull(query);
    View view = query.run();
    System.out.println(view.columnNames());
    assertFalse(view.columnNames().isEmpty());
    assertEquals(table.rowCount(), view.rowCount());
    query = table.select().where(valueOf("stop_name").isEqualTo("cat"));
    View view1 = query.run();
    assertEquals(0, view1.rowCount());
  }
}
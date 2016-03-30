package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static com.deathrayresearch.outlier.columns.ColumnType.CAT;
import static com.deathrayresearch.outlier.columns.ColumnType.INTEGER;
import static com.deathrayresearch.outlier.columns.ColumnType.LOCAL_DATE;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ColumnTest {

  private static final ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CAT             // polling org
  };

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
  }

  @Test
  public void testHead() throws Exception {
    System.out.println(table.column(0).head(5).print());
    System.out.println(table.column(1).head(5).print());
    System.out.println(table.column(2).head(5).print());
  }

  @Test
  public void testTail() throws Exception {
    System.out.println(table.column(0).tail(5).print());
    System.out.println(table.column(1).tail(5).print());
    System.out.println(table.column(2).tail(5).print());
  }

  @Test
  public void testName() throws Exception {
    System.out.println(table.columnNames());
    Column c = table.intColumn("approval");
    assertEquals("approval", c.name());
  }

  @Test
  public void testComment() throws Exception {
    System.out.println(table.columnNames());
    Column c = table.intColumn("approval");
    c.setComment("Dumb comment");
    assertEquals("Dumb comment", c.comment());
  }

  @Test
  public void testType() throws Exception {
    System.out.println(table.columnNames());
    Column c = table.intColumn("approval");
    assertEquals(ColumnType.INTEGER, c.type());
  }
}

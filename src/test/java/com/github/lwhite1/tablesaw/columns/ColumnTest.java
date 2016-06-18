package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static com.github.lwhite1.tablesaw.api.ColumnType.CATEGORY;
import static com.github.lwhite1.tablesaw.api.ColumnType.INTEGER;
import static com.github.lwhite1.tablesaw.api.ColumnType.LOCAL_DATE;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ColumnTest {

  private static final ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CATEGORY             // polling org
  };

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
  }

  @Test
  public void testHead() throws Exception {
    System.out.println(table.column(0).first(5).print());
    System.out.println(table.column(1).first(5).print());
    System.out.println(table.column(2).first(5).print());
  }

  @Test
  public void testTail() throws Exception {
    System.out.println(table.column(0).last(5).print());
    System.out.println(table.column(1).last(5).print());
    System.out.println(table.column(2).last(5).print());
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

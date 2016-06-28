package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.Table;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 *  Tests for LocalDate Column
 */
public class LocalDateColumnTest {

  private DateColumn column1;

  @Before
  public void setUp() throws Exception {
    Table table = new Table("Test");
    table.addColumn(column1);
    column1 = DateColumn.create("Game date");
  }

  @Test
  public void testAddCell() throws Exception {
    column1.addCell("2013-10-23");
    column1.addCell("12/23/1924");
    column1.addCell("12-May-2015");
    column1.addCell("12-Jan-2015");
    assertEquals(4, column1.size());
    LocalDate date = LocalDate.now();
    column1.add(date);
    assertEquals(5, column1.size());
  }

  @Test
  public void testDayOfMonth() throws Exception {
    column1.addCell("2013-10-23");
    column1.addCell("12/24/1924");
    column1.addCell("12-May-2015");
    column1.addCell("14-Jan-2015");
    IntColumn c2 = column1.dayOfMonth();
    assertEquals(23, c2.get(0));
    assertEquals(24, c2.get(1));
    assertEquals(12, c2.get(2));
    assertEquals(14, c2.get(3));
  }

  @Test
  public void testMonth() throws Exception {
    column1.addCell("2013-10-23");
    column1.addCell("12/24/1924");
    column1.addCell("12-May-2015");
    column1.addCell("14-Jan-2015");
    IntColumn c2 = column1.monthValue();
    assertEquals(10, c2.get(0));
    assertEquals(12, c2.get(1));
    assertEquals(5, c2.get(2));
    assertEquals(1, c2.get(3));
  }

  @Test
  public void testSummary() throws Exception {
    column1.addCell("2013-10-23");
    column1.addCell("12/24/1924");
    column1.addCell("12-May-2015");
    column1.addCell("14-Jan-2015");
    Table summary = column1.summary();
    assertEquals(4, summary.rowCount());
    assertEquals(2, summary.columnCount());
    assertEquals("Date", summary.column(0).name());
    assertEquals("Count", summary.column(1).name());
  }
}

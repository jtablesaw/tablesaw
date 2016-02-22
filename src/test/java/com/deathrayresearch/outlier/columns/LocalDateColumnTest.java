package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class LocalDateColumnTest {

  Table table = new Table("Test");
  LocalDateColumn column1 = LocalDateColumn.create("Game date");

  @Before
  public void setUp() throws Exception {
    table.addColumn(column1);
  }

  @Test
  public void testAddCell() throws Exception {
    column1.addCell("2013-10-23");
    column1.addCell("12/23/1924");
    column1.addCell("12-May-2015");
    column1.addCell("12-Jan-2015");
    Calendar calendar = new GregorianCalendar();
    LocalDate date = LocalDate.now();
    column1.add(date);
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
    IntColumn c2 = column1.month();
    assertEquals(10, c2.get(0));
    assertEquals(12, c2.get(1));
    assertEquals(5, c2.get(2));
    assertEquals(1, c2.get(3));
  }

  @Test
  public void testSize() throws Exception {
    LocalDate date = LocalDate.now();
    System.out.println(date.toEpochDay());

    LocalDateTime time = LocalDateTime.now();
    LocalDateTime time2 = time.plusYears(100);
    long t1 = time.toEpochSecond(ZoneOffset.UTC);
    long t2 = time2.toEpochSecond(ZoneOffset.UTC);
    System.out.println(t1);
    System.out.println(t2 - t1);
  }

  @Test
  public void testGroupBy() throws Exception {
  }
}

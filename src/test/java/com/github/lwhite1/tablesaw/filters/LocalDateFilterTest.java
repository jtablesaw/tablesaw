package com.github.lwhite1.tablesaw.filters;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import com.github.lwhite1.tablesaw.filter.LocalDatePredicate;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsFirstDayOfTheMonth;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInFebruary;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInMarch;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInYear;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsMonday;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsSunday;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsLastDayOfTheMonth;
import org.junit.Before;
import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 *
 */
public class LocalDateFilterTest {

  LocalDateColumn localDateColumn = LocalDateColumn.create("testing");
  Table table = new Table("test");

  @Before
  public void setUp() throws Exception {
    localDateColumn.add(LocalDate.of(2016, 2, 28));
    localDateColumn.add(LocalDate.of(2016, 2, 29));
    localDateColumn.add(LocalDate.of(2016, 3, 1));
    table.addColumn(localDateColumn);
  }

  @Test
  public void testIsSunday() {
    ColumnReference reference = new ColumnReference("testing");
    LocalDateIsSunday isSunday = new LocalDateIsSunday(reference);
    RoaringBitmap bitmap = isSunday.apply(table);
    assertTrue(bitmap.contains(0));
    assertFalse(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsMonday() {
    ColumnReference reference = new ColumnReference("testing");
    LocalDateIsMonday isSunday = new LocalDateIsMonday(reference);
    RoaringBitmap bitmap = isSunday.apply(table);
    assertFalse(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsFebruary() {
    ColumnReference reference = new ColumnReference("testing");
    LocalDateIsInFebruary isFebruary = new LocalDateIsInFebruary(reference);
    RoaringBitmap bitmap = isFebruary.apply(table);
    assertTrue(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsMarch() {
    ColumnReference reference = new ColumnReference("testing");
    LocalDateIsInMarch result = new LocalDateIsInMarch(reference);
    RoaringBitmap bitmap = result.apply(table);
    assertFalse(bitmap.contains(0));
    assertFalse(bitmap.contains(1));
    assertTrue(bitmap.contains(2));
  }

  @Test
  public void testIsFirstDayOfTheMonth() {
    ColumnReference reference = new ColumnReference("testing");
    LocalDateIsFirstDayOfTheMonth result = new LocalDateIsFirstDayOfTheMonth(reference);
    RoaringBitmap bitmap = result.apply(table);
    assertFalse(bitmap.contains(0));
    assertFalse(bitmap.contains(1));
    assertTrue(bitmap.contains(2));
  }

  @Test
  public void testIsLastDayOfTheMonth() {
    ColumnReference reference = new ColumnReference("testing");
    LocalDateIsLastDayOfTheMonth result = new LocalDateIsLastDayOfTheMonth(reference);
    RoaringBitmap bitmap = result.apply(table);
    assertFalse(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsInYear() {
    ColumnReference reference = new ColumnReference("testing");
    LocalDateIsInYear result = new LocalDateIsInYear(reference, 2016);
    RoaringBitmap bitmap = result.apply(table);
    assertTrue(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertTrue(bitmap.contains(2));
    result = new LocalDateIsInYear(reference, 2015);
    bitmap = result.apply(table);
    assertFalse(bitmap.contains(0));
    assertFalse(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testColumnFilters() {

    LocalDatePredicate after_2_28 = new LocalDatePredicate() {
      LocalDate date = LocalDate.of(2016, 2, 28);
      @Override
      public boolean test(LocalDate i) {
        return i.isAfter(date);
      }
    };

    LocalDateColumn filtered = localDateColumn.selectIf(after_2_28);

  }

  private void print(Object o) {
    System.out.println(o);
  }
}

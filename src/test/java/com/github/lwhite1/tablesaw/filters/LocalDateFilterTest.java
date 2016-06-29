package com.github.lwhite1.tablesaw.filters;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.filtering.LocalDatePredicate;
import com.github.lwhite1.tablesaw.filtering.datetimes.IsFirstDayOfTheMonth;
import com.github.lwhite1.tablesaw.filtering.datetimes.IsInFebruary;
import com.github.lwhite1.tablesaw.filtering.datetimes.IsInMarch;
import com.github.lwhite1.tablesaw.filtering.datetimes.IsInYear;
import com.github.lwhite1.tablesaw.filtering.datetimes.IsLastDayOfTheMonth;
import com.github.lwhite1.tablesaw.filtering.datetimes.IsMonday;
import com.github.lwhite1.tablesaw.filtering.datetimes.IsSunday;
import org.junit.Before;
import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 *
 */
public class LocalDateFilterTest {

  DateColumn localDateColumn = DateColumn.create("testing");
  Table table = Table.create("test");

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
    IsSunday isSunday = new IsSunday(reference);
    RoaringBitmap bitmap = isSunday.apply(table);
    assertTrue(bitmap.contains(0));
    assertFalse(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsMonday() {
    ColumnReference reference = new ColumnReference("testing");
    IsMonday isSunday = new IsMonday(reference);
    RoaringBitmap bitmap = isSunday.apply(table);
    assertFalse(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsFebruary() {
    ColumnReference reference = new ColumnReference("testing");
    IsInFebruary isFebruary = new IsInFebruary(reference);
    RoaringBitmap bitmap = isFebruary.apply(table);
    assertTrue(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsMarch() {
    ColumnReference reference = new ColumnReference("testing");
    IsInMarch result = new IsInMarch(reference);
    RoaringBitmap bitmap = result.apply(table);
    assertFalse(bitmap.contains(0));
    assertFalse(bitmap.contains(1));
    assertTrue(bitmap.contains(2));
  }

  @Test
  public void testIsFirstDayOfTheMonth() {
    ColumnReference reference = new ColumnReference("testing");
    IsFirstDayOfTheMonth result = new IsFirstDayOfTheMonth(reference);
    RoaringBitmap bitmap = result.apply(table);
    assertFalse(bitmap.contains(0));
    assertFalse(bitmap.contains(1));
    assertTrue(bitmap.contains(2));
  }

  @Test
  public void testIsLastDayOfTheMonth() {
    ColumnReference reference = new ColumnReference("testing");
    IsLastDayOfTheMonth result = new IsLastDayOfTheMonth(reference);
    RoaringBitmap bitmap = result.apply(table);
    assertFalse(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertFalse(bitmap.contains(2));
  }

  @Test
  public void testIsInYear() {
    ColumnReference reference = new ColumnReference("testing");
    IsInYear result = new IsInYear(reference, 2016);
    RoaringBitmap bitmap = result.apply(table);
    assertTrue(bitmap.contains(0));
    assertTrue(bitmap.contains(1));
    assertTrue(bitmap.contains(2));
    result = new IsInYear(reference, 2015);
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

    DateColumn filtered = localDateColumn.selectIf(after_2_28);

  }

  private void print(Object o) {
    System.out.println(o);
  }
}

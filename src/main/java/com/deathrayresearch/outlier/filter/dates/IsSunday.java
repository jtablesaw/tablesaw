package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Row;
import com.deathrayresearch.outlier.filter.AbstractFilter;
import com.deathrayresearch.outlier.filter.ColumnReference;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 *
 */
public class IsSunday extends AbstractFilter {

  public IsSunday(ColumnReference columnReference) {
    super(columnReference);
  }

  public static IsSunday isSunday(ColumnReference columnReference) {
    return new IsSunday(columnReference);
  }

  @Override
  public boolean matches(Row row) {
    LocalDate date = (LocalDate) row.get(this.columnReference.getColumn());
    return date.getDayOfWeek() == DayOfWeek.SUNDAY;
  }
}

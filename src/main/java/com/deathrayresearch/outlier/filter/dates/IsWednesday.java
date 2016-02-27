package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Row;
import com.deathrayresearch.outlier.filter.AbstractFilter;
import com.deathrayresearch.outlier.filter.ColumnReference;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 *
 */
public class IsWednesday extends AbstractFilter {

  public IsWednesday(ColumnReference columnReference) {
    super(columnReference);
  }

  @Override
  public boolean matches(Row row) {
    LocalDate date = (LocalDate) row.get(columnName());
    return date.getDayOfWeek() == DayOfWeek.WEDNESDAY;
  }

  static class IsWednesdayP {
    int columnNumber;

    public IsWednesdayP(int columnNumber) {
      this.columnNumber = columnNumber;
    }

    public boolean matches(Row row) {
      LocalDate date = (LocalDate) row.get(columnNumber);
      return date.getDayOfWeek() == DayOfWeek.WEDNESDAY;
    }

  }



}

package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Row;
import com.deathrayresearch.outlier.filter.AbstractFilter;
import com.deathrayresearch.outlier.filter.ColumnReference;

import java.time.LocalDate;

/**
 *
 */
public class IsInYear extends AbstractFilter {

  private final int year;

  public IsInYear(ColumnReference columnReference, int year) {
    super(columnReference);
    this.year = year;
  }

  @Override
  public boolean matches(Row row) {
    LocalDate date = (LocalDate) row.get(columnName());
    return date.getYear() == year;
  }

}

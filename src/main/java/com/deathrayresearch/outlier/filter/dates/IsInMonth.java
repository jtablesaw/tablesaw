package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Row;
import com.deathrayresearch.outlier.filter.AbstractFilter;
import com.deathrayresearch.outlier.filter.ColumnReference;

import java.time.LocalDate;
import java.time.Month;

/**
 *
 */
public class IsInMonth extends AbstractFilter {

  private Month month;

  public IsInMonth(ColumnReference columnReference, Month month) {
    super(columnReference);
    this.month = month;
  }

  @Override
  public boolean matches(Row row) {
    LocalDate date = (LocalDate) row.get(columnName());
    return date.getMonth() == month;
  }

}

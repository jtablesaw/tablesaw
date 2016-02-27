package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Row;
import com.deathrayresearch.outlier.filter.AbstractFilter;
import com.deathrayresearch.outlier.filter.ColumnReference;

import java.time.LocalDate;

/**
 *
 */
public class IsBefore extends AbstractFilter {

  LocalDate localDate;

  public IsBefore(ColumnReference columnReference, LocalDate localDate) {
    super(columnReference);
    this.localDate = localDate;
  }

  @Override
  public boolean matches(Row row) {
    LocalDate date = (LocalDate) row.get(columnName());
    return date.isBefore(localDate);
  }
}

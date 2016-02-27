package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Row;
import com.deathrayresearch.outlier.filter.AbstractFilter;
import com.deathrayresearch.outlier.filter.ColumnReference;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDate;

/**
 *
 */
@Immutable
public class IsAfter extends AbstractFilter {

  private final LocalDate localDate;

  public IsAfter(ColumnReference columnReference, LocalDate localDate) {
    super(columnReference);
    this.localDate = localDate;
  }

  @Override
  public boolean matches(Row row) {
    LocalDate date = (LocalDate) row.get(columnName());
    return date.isAfter(localDate);
  }

}

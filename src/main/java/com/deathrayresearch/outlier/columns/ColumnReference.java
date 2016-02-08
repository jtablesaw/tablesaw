package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.filter.DateEqualTo;
import com.deathrayresearch.outlier.filter.Filter;
import com.deathrayresearch.outlier.filter.FloatEqualTo;
import com.deathrayresearch.outlier.filter.FloatGreaterThan;
import com.deathrayresearch.outlier.filter.FloatGreaterThanOrEqualTo;
import com.deathrayresearch.outlier.filter.FloatLessThan;
import com.deathrayresearch.outlier.filter.FloatLessThanOrEqualTo;
import com.deathrayresearch.outlier.filter.IntEqualTo;
import com.deathrayresearch.outlier.filter.IntGreaterThan;
import com.deathrayresearch.outlier.filter.IntGreaterThanOrEqualTo;
import com.deathrayresearch.outlier.filter.IntLessThan;
import com.deathrayresearch.outlier.filter.IntLessThanOrEqualTo;
import com.deathrayresearch.outlier.filter.StringEqualTo;
import com.deathrayresearch.outlier.filter.TimeEqualTo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 */
public class ColumnReference {

  private String columnName;

  public ColumnReference(String column) {
    this.columnName = column;
  }

  public Filter isEqualTo(int value) {
    return new IntEqualTo(this, value);
  }

  public Filter isEqualTo(float value) {
    return new FloatEqualTo(this, value);
  }

  public Filter isEqualTo(LocalTime value) {
    return new TimeEqualTo(this, value);
  }

  public Filter isEqualTo(LocalDate value) {
    return new DateEqualTo(this, value);
  }

  public Filter isEqualTo(String value) {
    return new StringEqualTo(this, value);
  }

  public Filter isGreaterThan(int value) {
    return new IntGreaterThan(this, value);
  }

  public Filter isLessThan(int value) {
    return new IntLessThan(this, value);
  }

  public Filter isLessThanOrEqualTo(int value) {
    return new IntLessThanOrEqualTo(this, value);
  }

  public Filter isGreaterThanOrEqualTo(int value) {
    return new IntGreaterThanOrEqualTo(this, value);
  }

  public Filter isGreaterThan(float value) {
    return new FloatGreaterThan(this, value);
  }

  public Filter isLessThan(float value) {
    return new FloatLessThan(this, value);
  }

  public Filter isLessThanOrEqualTo(float value) {
    return new FloatLessThanOrEqualTo(this, value);
  }

  public Filter isGreaterThanOrEqualTo(float value) {
    return new FloatGreaterThanOrEqualTo(this, value);
  }

  public String getColumnName() {
    return columnName;
  }
}

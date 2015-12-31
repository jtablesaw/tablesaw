package com.deathrayresearch.outlier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.UnaryOperator;


/**
 * Defines the type of data held by a {@link Column}
 */
public enum ColumnType {

  /*
    SKIP(Object.class, SKIP_COLUMN, null),
    STRING(String.class, CONVERT_TO_STRING, ""),
    CAT(String.class, CONVERT_TO_CATEGORY, ""),
    REAL(Float.class, CONVERT_TO_REAL, Float.NaN),
    BOOLEAN(Boolean.class, CONVERT_TO_BOOLEAN, false),
    LOCAL_DATE(LocalDate.class, CONVERT_TO_LOCAL_DATE, Integer.MIN_VALUE),
    LOCAL_TIME(LocalTime.class, CONVERT_TO_LOCAL_TIME, -1),
    LOCAL_DATE_TIME(LocalDateTime.class, CONVERT_TO_LOCAL_DATE_TIME, null);
  */
  BOOLEAN(false),
  CAT(""),
  FLOAT(Float.NaN),
  INTEGER(Integer.MIN_VALUE),
  TEXT(""),
  LOCAL_DATE(Integer.MIN_VALUE),
  LOCAL_DATE_TIME(null),
  LOCAL_TIME(-1),
  SKIP(null);

  // private final Class<?> clazz;
  // private final UnaryOperator<Comparable> converter;
  private final Comparable missingValue;

  ColumnType(
      //Class<?> clazz, UnaryOperator<Comparable> converter,
      Comparable missingValue) {
    // this.clazz = clazz;
    // this.converter = converter;
    this.missingValue = missingValue;
  }
/*

  */
/**
 * Returns the java class represented by this ColumnType
 *//*

  public Class<?> clazz() {
    return clazz;
  }

  */

  /**
   * Returns a short, end-user friendly descriptor for the ColumnType
   *//*

  public String typeName() {
    return clazz.getSimpleName();
  }

  public UnaryOperator<Comparable> converter() {
    return converter;
  }

*/
  public Comparable getMissingValue() {
    return missingValue;
  }
}

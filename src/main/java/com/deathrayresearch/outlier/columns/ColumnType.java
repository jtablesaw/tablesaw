package com.deathrayresearch.outlier.columns;


/**
 * Defines the type of data held by a {@link Column}
 */
public enum ColumnType {

  BOOLEAN(false),
  CAT(""),
  FLOAT(Float.NaN),
  SHORT_INT(Short.MIN_VALUE),
  INTEGER(Integer.MIN_VALUE),
  LONG_INT(Long.MIN_VALUE),
  TEXT(""),
  LOCAL_DATE(Integer.MIN_VALUE),
  LOCAL_DATE_TIME(Long.MIN_VALUE),
  LOCAL_TIME(-1),
  PERIOD(Integer.MIN_VALUE),
  DURATION(Long.MIN_VALUE),
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

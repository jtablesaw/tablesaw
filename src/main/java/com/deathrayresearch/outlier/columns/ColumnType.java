package com.deathrayresearch.outlier.columns;


/**
 * Defines the type of data held by a {@link Column}
 */
public enum ColumnType {

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

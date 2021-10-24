package tech.tablesaw.interpolation;

import tech.tablesaw.columns.Column;

/**
 * Creates a new column with missing cells filled based off the value of nearby cells. This class
 * contains methods that are agnostic of column type.
 */
public class Interpolator<T> {

  /** The column being interpolated */
  protected final Column<T> col;

  /** Constructs an object for performing interpolation on the given column */
  public Interpolator(Column<T> column) {
    this.col = column;
  }

  /** Fills missing values with the next non-missing value */
  public Column<T> backfill() {
    Column<T> result = col.copy();
    T lastVal = null;
    for (int i = col.size() - 1; i >= 0; i--) {
      if (col.isMissing(i)) {
        if (lastVal != null) {
          result.set(i, lastVal);
        }
      } else {
        lastVal = col.get(i);
      }
    }
    return result;
  }

  /** Fills missing values with the last non-missing value */
  public Column<T> frontfill() {
    Column<T> result = col.copy();
    T lastVal = null;
    for (int i = 0; i < col.size(); i++) {
      if (col.isMissing(i)) {
        if (lastVal != null) {
          result.set(i, lastVal);
        }
      } else {
        lastVal = col.get(i);
      }
    }
    return result;
  }
}

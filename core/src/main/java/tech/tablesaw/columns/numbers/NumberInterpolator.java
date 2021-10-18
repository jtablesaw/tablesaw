package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.interpolation.Interpolator;

/**
 * Creates a new column with missing cells filled based off the value of nearby cells. <br>
 * Subclass to provide alternate interpolation strategies
 */
public class NumberInterpolator<T extends Number> extends Interpolator<T> {

  protected final NumericColumn<T> col;

  /** Constructs an interpolator for the given column */
  public NumberInterpolator(NumericColumn<T> col) {
    super(col);
    this.col = col;
  }

  /** Linearly interpolates missing values. */
  public DoubleColumn linear() {
    DoubleColumn result = col.asDoubleColumn();
    int last = -1;
    for (int i = 0; i < col.size(); i++) {
      if (!col.isMissing(i)) {
        if (last >= 0 && last != i - 1) {
          for (int j = last + 1; j < i; j++) {
            result.set(
                j,
                col.getDouble(last)
                    + (col.getDouble(i) - col.getDouble(last)) * (j - last) / (i - last));
          }
        }
        last = i;
      }
    }
    return result;
  }
}

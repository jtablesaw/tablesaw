package tech.tablesaw.table;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** Does a calculation on a rolling basis (e.g. mean for last 20 days) */
public class RollingColumn {

  /** The column providing the data for the rolling calculation */
  protected final Column<?> column;

  /** The size of the rolling window */
  protected final int window;

  /**
   * Constructs a rolling column based on calculations on a sliding window of {@code window} rows of
   * data from the given column
   */
  public RollingColumn(Column<?> column, int window) {
    this.column = column;
    this.window = window;
  }

  /**
   * Generates a name for the column based on the name of the original column and the function used
   * in the calculation
   */
  protected String generateNewColumnName(AggregateFunction<?, ?> function) {
    return column.name() + " " + window + "-period" + " " + function.functionName();
  }

  /** Performs the calculation and returns a new column containing the results */
  @SuppressWarnings({"unchecked"})
  public <INCOL extends Column<?>, OUT> Column<?> calc(AggregateFunction<INCOL, OUT> function) {
    // TODO: the subset operation copies the array. creating a view would likely be more efficient
    Column<?> result = function.returnType().create(generateNewColumnName(function));
    for (int i = 0; i < window - 1; i++) {
      result.appendMissing();
    }
    for (int origColIndex = 0; origColIndex < column.size() - window + 1; origColIndex++) {
      Selection selection = new BitmapBackedSelection();
      selection.addRange(origColIndex, origColIndex + window);
      INCOL subsetCol = (INCOL) column.subset(selection.toArray());
      OUT answer = function.summarize(subsetCol);
      if (answer instanceof Number) {
        Number number = (Number) answer;
        ((DoubleColumn) result).append(number.doubleValue());
      } else {
        result.appendObj(answer);
      }
    }
    return result;
  }
}

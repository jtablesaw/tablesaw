package tech.tablesaw.table;

import org.apache.commons.lang3.StringUtils;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;

/**
 * Does a calculation on a rolling basis (e.g. mean for last 20 days)
 */
public class RollingColumn {

  private final Column column;
  private final int window;

  public RollingColumn(Column column, int window) {
    this.column = column;
    this.window = window;
  }

  public DoubleColumn mean() {
    return calc(AggregateFunctions.mean);
  }

  public DoubleColumn sum(String resultColName) {
    return calc(AggregateFunctions.sum);
  }

  private String generateNewColumnName(AggregateFunction function) {
    boolean useSpaces = column.name().matches("\\s+");
    String separator = useSpaces ? " " : "";
    String newColumnName = new StringBuilder(column.name())
        .append(separator).append(useSpaces ? function.functionName() : StringUtils.capitalize(function.functionName()))
        .append(separator).append(window)
        .toString();
    return newColumnName;
  }

  public DoubleColumn calc(AggregateFunction function) {
    // TODO: the subset operation copies the array. creating a view would likely be more efficient
    DoubleColumn result = new DoubleColumn(generateNewColumnName(function), column.size());
    for (int i = 0; i < window - 1; i++) {
      result.append(DoubleColumn.MISSING_VALUE);
    }
    for (int origColIndex = 0; origColIndex < column.size() - window + 1; origColIndex++) {
      Selection selection = new BitmapBackedSelection();
      selection.addRange(origColIndex, origColIndex + window);
      Column windowedColumn = column.subset(selection);
      double calc;
      if (windowedColumn instanceof DoubleColumn) {
        calc = function.agg((DoubleColumn) windowedColumn);
      } else if (windowedColumn instanceof FloatColumn) {
        calc = function.agg((FloatColumn) windowedColumn);
      } else if (windowedColumn instanceof IntColumn) {
        calc = function.agg((IntColumn) windowedColumn);
      } else if (windowedColumn instanceof LongColumn) {
        calc = function.agg((LongColumn) windowedColumn);
      } else if (windowedColumn instanceof ShortColumn) {
        calc = function.agg((ShortColumn) windowedColumn);
      } else {
        throw new IllegalArgumentException("Cannot calculate " + function.functionName()
            + " on column of type " + windowedColumn.type());
      }
      result.append(calc);
    }
    return result;
  }

}

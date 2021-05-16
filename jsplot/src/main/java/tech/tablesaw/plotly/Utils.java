package tech.tablesaw.plotly;

import com.fasterxml.jackson.core.io.JsonStringEncoder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.TraceBuilder;

public class Utils {

  private Utils() {}

  public static String dataAsString(double[] data) {
    return Arrays.toString(data);
  }

  /** @return un-escaped quote of argument */
  public static String quote(String string) {
    return "'" + string + "'";
  }

  /**
   * Escapes string for Javascript, assuming but without surrounding it with doublequotes (") and
   * saves to output to the given StringBuilder.
   */
  private static void escape(String s, StringBuilder sb) {
    JsonStringEncoder.getInstance().quoteAsString(s, sb);
  }

  /** @return a Javascript array of strings (escaped if needed) */
  public static String dataAsString(Object[] data) {
    StringBuilder builder = new StringBuilder("[");
    for (int i = 0; i < data.length; i++) {
      Object o = data[i];
      builder.append("\"");
      escape(String.valueOf(o), builder);
      builder.append("\"");
      if (i < data.length - 1) {
        builder.append(",");
      }
    }
    builder.append("]");
    return builder.toString();
  }

  public static String dataAsString(double[][] data) {
    StringBuilder builder = new StringBuilder("[");
    for (double[] row : data) {
      builder.append("[");
      for (double value : row) {
        builder.append(value);
        builder.append(",");
      }
      builder.append("],");
    }
    builder.append("]");
    return builder.toString();
  }

  /**
   * Returns a array of column without missing value
   *
   * @param columns a array of columns that may contain missing value
   */
  public static Column<?>[] filterMissing(Column<?>... columns) {
    int n = columns[0].size();
    ArrayList<Integer> keep = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      boolean notNull = true;
      for (Column<?> col : columns) {
        if (col.isMissing(i)) {
          notNull = false;
          break;
        }
      }
      if (notNull) keep.add(i);
    }
    int[] selected = keep.stream().mapToInt(Integer::intValue).toArray();
    Column<?>[] results = new Column[columns.length];
    for (int i = 0; i < columns.length; i++) {
      results[i] = columns[i].subset(selected);
    }
    return results;
  }

  /**
   * Returns a boolean array indicating whether the corresponding place should be kept after missing
   * value removal
   *
   * @param counter a AtomicInteger initialized with 0, to record the number of non-missing value
   * @param args a array of Object that may contains missing value
   */
  public static boolean[] filterMissing(AtomicInteger counter, Object[]... args) {
    int n = args[0].length;
    boolean[] keep = new boolean[n];
    for (int i = 0; i < n; i++) {
      for (Object[] arr : args) {
        if (arr[i] == null) {
          keep[i] = false;
          break;
        }
        keep[i] = true;
      }
      if (keep[i]) counter.incrementAndGet();
    }
    return keep;
  }

  /**
   * Returns an Object array after missing value removal
   *
   * @param x an Object array contains missing value
   * @param counter a AtomicInteger that record the number of non-missing value
   * @param keep a boolean array indicating whether the corresponding place should be kept after missing
   *             value removal
   */
  public static Object[] filterArray(Object[] x, AtomicInteger counter, boolean[] keep) {
    Object[] xWithoutMissingValue = new Object[counter.get()];
    int i = 0;
    for (int j = 0; j < keep.length; j++) {
      if (keep[j]) {
        xWithoutMissingValue[i] = x[j];
        i++;
      }
    }
    return xWithoutMissingValue;
  }

  /**
   * Returns a double array after missing value removal
   *
   * @param y a double array before missing value removal
   * @param counter a AtomicInteger that record the number of non-missing value
   * @param keep a boolean array indicating whether the corresponding place should be kept after missing
   *             value removal
   */
  public static double[] filterArray(double[] y, AtomicInteger counter, boolean[] keep) {
    double[] yWithoutMissingValue = new double[counter.get()];
    int i = 0;
    for (int j = 0; j < keep.length; j++) {
      if (keep[j]) {
        yWithoutMissingValue[i] = y[j];
        i++;
      }
    }
    return yWithoutMissingValue;
  }
}

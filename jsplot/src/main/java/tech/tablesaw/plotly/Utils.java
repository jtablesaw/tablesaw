package tech.tablesaw.plotly;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import java.util.Arrays;

public class Utils {

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
}

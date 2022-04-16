package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.columns.Column;

public class TraceWrapper {

  protected static String[] columnToStringArray(Column<?> column) {
    String[] x = new String[column.size()];
    for (int i = 0; i < column.size(); i++) {
      x[i] = column.getString(i);
    }
    return x;
  }
}

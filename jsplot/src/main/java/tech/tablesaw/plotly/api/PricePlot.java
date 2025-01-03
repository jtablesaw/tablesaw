package tech.tablesaw.plotly.api;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

/** Abstract superclass for time series plots that have open-high-low-close data */
public abstract class PricePlot {

  public static Figure create(
      String title,
      Table table,
      String xCol,
      String openCol,
      String highCol,
      String lowCol,
      String closeCol,
      String plotType) {
    Layout layout = Layout.builder(title, xCol).build();
    Column<?> x = table.column(xCol);
    NumericColumn<?> open = table.numberColumn(openCol);
    NumericColumn<?> high = table.numberColumn(highCol);
    NumericColumn<?> low = table.numberColumn(lowCol);
    NumericColumn<?> close = table.numberColumn(closeCol);
    ScatterTrace trace;
    if (ColumnType.LOCAL_DATE.equals(x.type())) {
      trace =
          ScatterTrace.builder(table.dateColumn(xCol), open, high, low, close)
              .type(plotType)
              .build();
    } else if (ColumnType.LOCAL_DATE_TIME.equals(x.type())) {
      trace =
          ScatterTrace.builder(table.dateTimeColumn(xCol), open, high, low, close)
              .type(plotType)
              .build();
    } else if (ColumnType.INSTANT.equals(x.type())) {
      trace =
          ScatterTrace.builder(table.instantColumn(xCol), open, high, low, close)
              .type(plotType)
              .build();
    } else {
      throw new IllegalArgumentException(
          "Column containing data for the X-Axis must be of type INSTANT, LOCAL_DATE, or LOCAL_DATE_TIME");
    }
    return new Figure(layout, trace);
  }
}

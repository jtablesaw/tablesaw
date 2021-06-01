package tech.tablesaw.plotly.api;

import java.io.IOException;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Axis;
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
    if (x.type() == ColumnType.LOCAL_DATE) {
      trace =
          ScatterTrace.builder(table.dateColumn(xCol), open, high, low, close)
              .type(plotType)
              .build();
    } else if (x.type() == ColumnType.LOCAL_DATE_TIME) {
      trace =
          ScatterTrace.builder(table.dateTimeColumn(xCol), open, high, low, close)
              .type(plotType)
              .build();
    } else if (x.type() == ColumnType.INSTANT) {
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

  public static Figure create(
      String title,
      Table table,
      String xCol,
      String openCol,
      String highCol,
      String lowCol,
      String closeCol,
      String plotType,
      boolean range_slider) {
    Layout layout =
        Layout.builder(title, xCol)
            .xAxis(Axis.builder().rangeslider("{visible:" + range_slider + "}").build())
            .build();
    Column<?> x = table.column(xCol);
    NumericColumn<?> open = table.numberColumn(openCol);
    NumericColumn<?> high = table.numberColumn(highCol);
    NumericColumn<?> low = table.numberColumn(lowCol);
    NumericColumn<?> close = table.numberColumn(closeCol);
    ScatterTrace trace;
    if (x.type() == ColumnType.LOCAL_DATE) {
      trace =
          ScatterTrace.builder(table.dateColumn(xCol), open, high, low, close)
              .type(plotType)
              .build();
    } else if (x.type() == ColumnType.LOCAL_DATE_TIME) {
      trace =
          ScatterTrace.builder(table.dateTimeColumn(xCol), open, high, low, close)
              .type(plotType)
              .build();
    } else if (x.type() == ColumnType.INSTANT) {
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

  private static class TestPricePlotNoSlider {
    public static void main(String[] args) throws IOException {
      Table priceTable = Table.read().csv("./data/ohlcdata.csv");
      priceTable.addColumns(
          priceTable.dateColumn("date").atStartOfDay().setName("date time"),
          priceTable.dateColumn("date").atStartOfDay().asInstantColumn().setName("instant"));
      priceTable.numberColumn("Volume").setPrintFormatter(NumberColumnFormatter.intsWithGrouping());
      Figure fig =
          PricePlot.create(
              "Prices",
              priceTable,
              "instant",
              "open",
              "high",
              "low",
              "close",
              "candlestick",
              false);
      Plot.show(fig);
    }
  }
}

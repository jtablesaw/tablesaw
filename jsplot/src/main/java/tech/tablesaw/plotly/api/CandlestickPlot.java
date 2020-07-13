package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;

/**
 * Candlestick time series plot typically used to illustrate price trends for stocks and other
 * exchange-traded products
 *
 * @see OHLCPlot
 */
public class CandlestickPlot {

  private static final String PLOT_TYPE = "candlestick";

  /** Returns Figure containing candlestick time series plot with a default layout */
  public static Figure create(
      String title,
      Table table,
      String xCol,
      String openCol,
      String highCol,
      String lowCol,
      String closeCol) {
    return PricePlot.create(title, table, xCol, openCol, highCol, lowCol, closeCol, PLOT_TYPE);
  }
}

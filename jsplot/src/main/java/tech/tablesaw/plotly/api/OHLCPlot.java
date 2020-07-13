package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;

/**
 * Open-High-Low-Close time series plot typically used to illustrate price trends for stocks and
 * other exchange-traded products
 *
 * @see CandlestickPlot
 */
public class OHLCPlot {

  private static final String PLOT_TYPE = "ohlc";

  /** Returns Figure containing Open-High-Low-Close time series plot with a default layout */
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

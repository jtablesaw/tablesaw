package com.github.lwhite1.tablesaw.plotting;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.table.TableGroup;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.Series;
import org.knowm.xchart.style.GGPlot2Theme;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.IOException;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static com.github.lwhite1.tablesaw.api.QueryHelper.both;
import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 *
 */
public class PlotTest {
  public static void main(String[] args) throws IOException {

    Table tornadoes = Table.create(COLUMN_TYPES_OLD, "data/1950-2014_torn.csv");

    // filter out the missings and the unscaled tornados
    tornadoes = tornadoes.selectWhere(
        both(column("Start Lat").isGreaterThan(10.0f),
            (column("Scale").isGreaterThanOrEqualTo(0))));

    TableGroup group = new TableGroup(tornadoes, tornadoes.intColumn("Scale"));


    // Create Chart
    XYChart chart = new XYChartBuilder().width(800).height(600).build();

    // Customize Chart
    chart.setTitle("US Tornados");
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
    chart.getStyler().setChartTitleVisible(true);
    chart.getStyler().setTheme(new GGPlot2Theme());
    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
    chart.getStyler().setMarkerSize(2);

    Color[] colors = StandardColors.ggPlotGreys(6);
    colors[5] = Color.RED;
    colors[4] = Color.RED;
    //Color[] colors = {Color.WHITE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.RED};
    chart.getStyler().setSeriesColors(colors);

    for (Table table : group) {
      double[] xData = table.floatColumn("Start Lon").toDoubleArray();
      double[] yData = table.floatColumn("Start Lat").toDoubleArray();
      chart.addSeries(table.name(), xData, yData);
    }

    // Show it
    new SwingWrapper<>(chart).displayChart("Starting point of US Tornados: 1950-2014");
  }

  // column types for the tornado table
  private static final ColumnType[] COLUMN_TYPES_OLD = {
      INTEGER,     // number by year
      INTEGER,     // year
      INTEGER,     // month
      INTEGER,     // day
      LOCAL_DATE,  // date
      LOCAL_TIME,  // time
      CATEGORY,    // tz
      CATEGORY,    // st
      CATEGORY,    // state fips
      INTEGER,     // state torn number
      INTEGER,     // scale
      INTEGER,     // injuries
      INTEGER,     // fatalities
      FLOAT,       // loss
      FLOAT,   // crop loss
      FLOAT,   // St. Lat
      FLOAT,   // St. Lon
      FLOAT,   // End Lat
      FLOAT,   // End Lon
      FLOAT,   // length
      FLOAT,   // width
      FLOAT,   // NS
      FLOAT,   // SN
      FLOAT,   // SG
      CATEGORY,  // Count FIPS 1-4
      CATEGORY,
      CATEGORY,
      CATEGORY};
}

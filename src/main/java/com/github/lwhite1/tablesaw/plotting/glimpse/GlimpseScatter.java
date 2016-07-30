package com.github.lwhite1.tablesaw.plotting.glimpse;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.plotting.StandardColors;
import com.github.lwhite1.tablesaw.table.TemporaryView;
import com.github.lwhite1.tablesaw.table.ViewGroup;
import com.metsci.glimpse.layout.GlimpseLayout;
import com.metsci.glimpse.layout.GlimpseLayoutProvider;
import com.metsci.glimpse.painter.decoration.BackgroundPainter;
import com.metsci.glimpse.painter.decoration.BorderPainter;
import com.metsci.glimpse.painter.decoration.GridPainter;
import com.metsci.glimpse.painter.shape.DynamicPointSetPainter;
import com.metsci.glimpse.plot.Plot2D;
import com.metsci.glimpse.support.color.GlimpseColor;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.stat.StatUtils;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class GlimpseScatter implements GlimpseLayoutProvider {

  private String plotTitle;
  private String xAxisName;
  private String yAxisName;
  private NumericColumn x;
  private NumericColumn[] seriesYvalues;

  public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn, ViewGroup group)
      throws IOException {

    GroupScatter.show(chartTitle, xColumn, yColumn, group);
  }

  public GlimpseScatter(String plotTitle, String yAxisName, NumericColumn x, NumericColumn ... seriesYvalues)
      throws IOException {
    this.plotTitle = plotTitle;
    this.x = x;
    this.xAxisName = x.name();
    this.yAxisName = yAxisName;
    this.seriesYvalues = seriesYvalues;
  }

  public GlimpseScatter(String plotTitle, NumericColumn x, NumericColumn y)
      throws IOException {
    this.plotTitle = plotTitle;
    this.x = x;
    this.xAxisName = x.name();
    this.yAxisName = y.name();
    this.seriesYvalues = new NumericColumn[1];
    this.seriesYvalues[0] = y;
  }

  public GlimpseScatter(String plotTitle, String xAxisName, String yAxisName, NumericColumn x, NumericColumn ... seriesYvalues)
      throws IOException {
    this.plotTitle = plotTitle;
    this.x = x;
    this.xAxisName = xAxisName;
    this.yAxisName = yAxisName;
    this.seriesYvalues = seriesYvalues;
  }

  public GlimpseScatter(NumericColumn x, NumericColumn ... seriesYvalues) throws IOException {
    this.plotTitle = "Scatterplot";
    this.x = x;
    this.xAxisName = x.name();
    this.yAxisName = "y axis";
    this.seriesYvalues = seriesYvalues;
  }

  public static void show(NumericColumn x, NumericColumn ... seriesYvalues) {

    try {
      Display.showWithSwing(new GlimpseScatter(x, seriesYvalues));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static void show(String title, NumericColumn x, NumericColumn y) {

    try {
      Display.showWithSwing(new GlimpseScatter(title, x, y));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  public GlimpseLayout getLayout() {

    Plot2D plot = new Plot2D(String.valueOf(RandomUtils.nextLong(0, 1000000L)));
    GlimpseLayout plotLayout = plot.getLayoutCenter();

    // add a painter to paint a solid dark background on the plot
    plotLayout.addPainter(new BackgroundPainter(false));

    // add painter for drawing points
    final DynamicPointSetPainter painter = new DynamicPointSetPainter();
    painter.setPointSize(10f);

    // add the points
    DynamicPointSetPainter.BulkPointAccumulator pointAccumulator = new DynamicPointSetPainter.BulkPointAccumulator();

    List<Color> colors = StandardColors.standardColors();
    double yMin = Double.MAX_VALUE;
    double yMax = 0;

    for (int col = 0; col < seriesYvalues.length; col++) {
      yMin = Math.min(yMin, seriesYvalues[col].min());
      yMax = Math.max(yMax, seriesYvalues[col].max());
      float[] color = GlimpseColor.fromColorAwt(colors.get(col));
      for (int i = 0; i < seriesYvalues[col].size(); i++) {
        pointAccumulator.add(i, x.getFloat(i), seriesYvalues[col].getFloat(i), color);
      }
      painter.putPoints(pointAccumulator);
    }

    // set the range for the axes
    double xMin = x.min();
    double xMax = x.max();
    plot.getAxis().set(xMin, xMax, yMin, yMax);

    // add a painter to display grid lines
    GridPainter gridPainter = new GridPainter(plot.getLabelHandlerX(), plot.getLabelHandlerY());
    plotLayout.addPainter(gridPainter);

    // add the point-drawing painter
    plotLayout.addPainter(painter);

    // add a painter to paint a simple line border on the plot
    plotLayout.addPainter(new BorderPainter().setColor(GlimpseColor.getBlack()));

    // add axis and plot labels
    plot.setAxisLabelX(xAxisName);
    plot.setAxisLabelY(yAxisName);
    plot.setTitle(plotTitle);

    // don't show a z axis
    plot.getLayoutZ().setVisible(false);

    return plot;
  }

  static class SimpleScatter implements GlimpseLayoutProvider {
    private final String plotTitle;
    private final double[] xData;
    private final double[] yData;

    public SimpleScatter(String plotTitle, double[] xData, double[] yData) {
      this.plotTitle = plotTitle;
      this.xData = xData;
      this.yData = yData;
    }

    @Override
    public GlimpseLayout getLayout() {

      Plot2D plot = new Plot2D(String.valueOf(RandomUtils.nextLong(0, 1000000L)));
      GlimpseLayout plotLayout = plot.getLayoutCenter();

      // add a painter to paint a solid dark background on the plot
      plotLayout.addPainter(new BackgroundPainter(false));

      // add painter for drawing points
      final DynamicPointSetPainter painter = new DynamicPointSetPainter();
      painter.setPointSize(10f);

      // add the points
      DynamicPointSetPainter.BulkPointAccumulator pointAccumulator = new DynamicPointSetPainter.BulkPointAccumulator();

      List<Color> colors = StandardColors.standardColors();
      double yMin = StatUtils.min(yData);
      double yMax = StatUtils.max(yData);

      for (int i = 0; i < xData.length; i++) {
        pointAccumulator.add(i, (float) xData[i], (float) yData[i], GlimpseColor.fromColorAwt(colors.get(0)));
      }
      painter.putPoints(pointAccumulator);

      // set the range for the axes
      double xMin = StatUtils.min(xData);
      double xMax = StatUtils.max(xData);
      plot.getAxis().set(xMin, xMax, yMin, yMax);

      // add a painter to display grid lines
      GridPainter gridPainter = new GridPainter(plot.getLabelHandlerX(), plot.getLabelHandlerY());
      plotLayout.addPainter(gridPainter);

      // add the point-drawing painter
      plotLayout.addPainter(painter);

      // add a painter to paint a simple line border on the plot
      plotLayout.addPainter(new BorderPainter().setColor(GlimpseColor.getBlack()));

      // add axis and plot labels
      plot.setTitle(plotTitle);

      // don't show a z axis
      plot.getLayoutZ().setVisible(false);

      return plot;

    }
  }

  static class GroupScatter implements GlimpseLayoutProvider {

    private String plotTitle;
    private String xAxisName;
    private String yAxisName;
    private ViewGroup series;
    private NumericColumn xColumn;
    private NumericColumn yColumn;

    public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn, ViewGroup group)
        throws IOException {

      GroupScatter scatter = new GroupScatter();
      scatter.plotTitle = chartTitle;
      scatter.series = group;
      scatter.xAxisName = xColumn.name();
      scatter.yAxisName = yColumn.name();

      try {
        Display.showWithSwing(scatter);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }

    @Override
    public GlimpseLayout getLayout() {

      Plot2D plot = new Plot2D(String.valueOf(RandomUtils.nextLong(0, 1000000L)));
      GlimpseLayout plotLayout = plot.getLayoutCenter();

      // add a painter to paint a solid dark background on the plot
      plotLayout.addPainter(new BackgroundPainter(false));

      // add painter for drawing points
      final DynamicPointSetPainter painter = new DynamicPointSetPainter();
      painter.setPointSize(10f);

      // add the points
      DynamicPointSetPainter.BulkPointAccumulator pointAccumulator = new DynamicPointSetPainter.BulkPointAccumulator();

      List<Color> colors = StandardColors.standardColors();
      double yMin = Double.MAX_VALUE;
      double yMax = 0;

      // set the range for the axes
      double xMin = Double.MAX_VALUE;
      double xMax = 0;

      for (int viewNo = 0; viewNo < series.size(); viewNo++) {
        TemporaryView view = series.get(viewNo);
        NumericColumn xCol = view.numericColumn(xAxisName);
        NumericColumn yCol = view.numericColumn(yAxisName);
        xMin = Math.min(xMin, xCol.min());
        xMax = Math.max(xMax, xCol.max());
        yMin = Math.min(yMin, yCol.min());
        yMax = Math.max(yMax, yCol.max());

        float[] color = GlimpseColor.fromColorAwt(colors.get(viewNo));
        for (int i = 0; i < view.rowCount(); i++) {
          if (viewNo == 0) {
            System.out.print(i + ", " + xCol.getFloat(i) + ", " + yCol.getFloat(i) + ", " + color +'\n');
          }
          pointAccumulator.add(i, xCol.getFloat(i), yCol.getFloat(i), color);
        }
        painter.putPoints(pointAccumulator);
      }

      // set the range for the axes
      plot.getAxis().set(xMin, xMax, yMin, yMax);

      // add a painter to display grid lines
      GridPainter gridPainter = new GridPainter(plot.getLabelHandlerX(), plot.getLabelHandlerY());
      plotLayout.addPainter(gridPainter);

      // add the point-drawing painter
      plotLayout.addPainter(painter);

      // add a painter to paint a simple line border on the plot
      plotLayout.addPainter(new BorderPainter().setColor(GlimpseColor.getBlack()));

      // add axis and plot labels
      plot.setAxisLabelX(xAxisName);
      plot.setAxisLabelY(yAxisName);
      plot.setTitle(plotTitle);

      // don't show a z axis
      plot.getLayoutZ().setVisible(false);

      return plot;
    }
  }
}
package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.plotting.StandardColors;
import com.github.lwhite1.tablesaw.plotting.glimpse.Display;
import com.metsci.glimpse.axis.listener.mouse.AxisMouseListener;
import com.metsci.glimpse.axis.listener.mouse.AxisMouseListener2D;
import com.metsci.glimpse.layout.GlimpseLayout;
import com.metsci.glimpse.layout.GlimpseLayoutProvider;
import com.metsci.glimpse.painter.info.CursorTextPainter;
import com.metsci.glimpse.painter.plot.XYLinePainter;
import com.metsci.glimpse.plot.SimplePlot2D;
import com.metsci.glimpse.support.color.GlimpseColor;
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;
import java.io.IOException;

/**
 * A basic x/y line plot with a simple legend.
 */
public class Line implements GlimpseLayoutProvider {

  private String plotTitle;
  private String xAxisName;
  private String yAxisName;
  private NumericColumn x;
  private NumericColumn[] seriesYvalues;

  public Line(NumericColumn x, NumericColumn ... seriesYvalues) throws IOException {
    this.plotTitle = "Line plot";
    this.x = x;
    this.xAxisName = x.name();
    this.yAxisName = "y axis";
    this.seriesYvalues = seriesYvalues;
  }

  public Line(String plotTitle, NumericColumn x, NumericColumn ... seriesYvalues) throws IOException {
    this.plotTitle = plotTitle;
    this.x = x;
    this.xAxisName = x.name();
    this.yAxisName = seriesYvalues[0].name();
    this.seriesYvalues = seriesYvalues;
  }

  @Override
  public SimplePlot2D getLayout() {

    // create a plot frame
    SimplePlot2D plot = new SimplePlot2D(String.valueOf(RandomUtils.nextLong(0, 1000000L)));
    GlimpseLayout layout = plot.getLayoutCenter();

    layout.setEventConsumer(false);

    AxisMouseListener listener = new AxisMouseListener2D();
    layout.addGlimpseMouseAllListener(listener);

    // set axis labels and chart title
    plot.setTitle("Line Plot Example");
    plot.setAxisLabelX("x axis");
    plot.setAxisLabelY("data series 1");

    // set the x, y initial axis bounds
    // set the range for the axes
    double xMin = x.min();
    double xMax = x.max();
    double yMin = Double.MAX_VALUE;
    double yMax = 0;

    // don't show the square selection box, only the x and y crosshairs
    plot.getCrosshairPainter().showSelectionBox(false);

    java.util.List<Color> colors = StandardColors.randomColors(seriesYvalues.length);

    for (int i = 0; i < seriesYvalues.length; i++) {
      NumericColumn yColumn = seriesYvalues[i];
      yMin = Math.min(yMin, yColumn.min());
      yMax = Math.max(yMax, yColumn.max());
      // creating a data series painter, passing it the lineplot frame
      // this constructor will have the painter draw according to the lineplot x and y axis
      Color color = colors.get(i);
      XYLinePainter series1 = createXYLinePainter(x, yColumn, color, 1.5f);
      plot.addPainter(series1);
    }

    plot.getAxis().set(xMin, xMax, yMin, yMax);

    // add a painter to display the x and y position of the cursor
    CursorTextPainter cursorPainter = new CursorTextPainter();
    plot.addPainter(cursorPainter);

    // don't offset the text by the size of the selection box, since we aren't showing it
    cursorPainter.setOffsetBySelectionSize(false);

    plot.getLayoutZ().setVisible(false);
    plot.setAxisLabelX(xAxisName);
    plot.setAxisLabelY(yAxisName);
    plot.setTitle(plotTitle);
    return plot;
  }

  public static XYLinePainter createXYLinePainter(NumericColumn x, NumericColumn y, Color color, float lineThickness) {

    double[] dataX = x.toDoubleArray();
    double[] dataY = y.toDoubleArray();

    XYLinePainter series1 = new XYLinePainter();

    series1.setData(dataX, dataY);
    series1.setLineColor(GlimpseColor.fromColorAwt(color));
    series1.setLineThickness(lineThickness);
    series1.showPoints(false);

    return series1;
  }

  public static void show(NumericColumn x, NumericColumn y) {
    try {
      Display.showWithSwing(new Line(x, y));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static void show(String plotTitle, NumericColumn x, NumericColumn y) {
    try {
      Display.showWithSwing(new Line(plotTitle, x, y));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
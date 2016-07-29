package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.plotting.glimpse.Display;
import com.metsci.glimpse.axis.Axis1D;
import com.metsci.glimpse.layout.GlimpseLayout;
import com.metsci.glimpse.layout.GlimpseLayoutProvider;
import com.metsci.glimpse.painter.decoration.BackgroundPainter;
import com.metsci.glimpse.painter.plot.HistogramPainter;
import com.metsci.glimpse.plot.SimplePlot2D;
import com.metsci.glimpse.support.color.GlimpseColor;

/**
 *
 */
public class Histogram implements GlimpseLayoutProvider {

  private String title = "Distribution";
  private NumericColumn column;


  public static void show(NumericColumn column) {
    Histogram plot = new Histogram(column);
    try {
      Display.showWithSwing(plot);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static void show(String plotTitle, NumericColumn column) {
    Histogram plot = new Histogram(column);
    plot.title = plotTitle;
    try {
      Display.showWithSwing(plot);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public Histogram(NumericColumn column) {
    this.column = column;

  }

  @Override
  public GlimpseLayout getLayout() {
    double[] data1 = column.toDoubleArray();

    Axis1D xAxis = new Axis1D();
    xAxis.setMin(-10);
    xAxis.setMax(15);
    xAxis.setAbsoluteMin(-10);
    xAxis.setAbsoluteMax(15);

    GlimpseLayout histogramPlot2 = createHistogram(xAxis);
    GlimpseLayout layout = new GlimpseLayout();
    layout.addPainter(new BackgroundPainter(true));

    layout.addLayout(histogramPlot2);
    layout.invalidateLayout();
    return layout;
  }

  private GlimpseLayout createHistogram(Axis1D xAxis) {
    double[] data1 = column.toDoubleArray();
    SimplePlot2D histogramplot = new SimplePlot2D();

    histogramplot.setTitle(title);
    histogramplot.setAxisLabelX(column.name());
    histogramplot.setAxisLabelY("frequency");

    histogramplot.getAxis().getAxisX().setParent(xAxis);

    histogramplot.lockMinY(0);

    histogramplot.setShowMinorTicksX(true);
    histogramplot.setShowMinorTicksY(true);

    histogramplot.getCrosshairPainter().showSelectionBox(false);
    histogramplot.getCrosshairPainter().setVisible(false);

    HistogramPainter stacked = new HistogramPainter();

    stacked.setData(data1);
    stacked.setColor(GlimpseColor.fromColorRgba(1, 0, 0, 0.6f));

    // add the two painters to the plot
    histogramplot.addPainter(stacked);

    stacked.autoAdjustAxisBounds(histogramplot.getAxis());

    return histogramplot;
  }
}

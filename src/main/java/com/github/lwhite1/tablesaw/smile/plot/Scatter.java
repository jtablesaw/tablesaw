package com.github.lwhite1.tablesaw.smile.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import smile.plot.PlotCanvas;
import smile.plot.ScatterPlot;

import javax.swing.*;

/**
 * Simple API for producing basic scatter plots directly from Tablesaw tables and columns
 */
public class Scatter {

  public static PlotCanvas create(String title, NumericColumn x, NumericColumn y) {
    double[][] xy = DoubleArrays.to2dArray(x, y);
    PlotCanvas plot = ScatterPlot.plot(xy);
    plot.setTitle(title);
    plot.setAxisLabels(x.name(), y.name());
    return plot;
  }

  public static void show(String title, NumericColumn x, NumericColumn y) {
    show(title, x, y, 600, 400);
  }

  public static void show(String title, NumericColumn xColumn, NumericColumn yColumn, int width, int height) {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setSize(width, height);
    frame.getContentPane().add(create(title, xColumn, yColumn));
    frame.setVisible(true);
  }

  public static PlotCanvas create(String title, double[] x, double[] y, String xName, String yName) {

    double[][] xy = DoubleArrays.to2dArray(x, y);
    PlotCanvas plot = ScatterPlot.plot(xy);
    plot.setTitle(title);
    plot.setAxisLabels(xName, yName);
    return plot;
  }
}

package com.github.lwhite1.tablesaw.smile.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import smile.plot.PlotCanvas;

import javax.swing.*;

/**
 * Simple API for producing basic histogram plots directly from Tablesaw tables and columns
 */
public class Density {

  public static PlotCanvas create(NumericColumn column) {
    PlotCanvas canvas = smile.plot.Histogram.plot(column.toDoubleArray());
    canvas.setTitle(column.name());
    canvas.setAxisLabel(0, column.name());
    canvas.setAxisLabel(1, "proportion");

    return canvas;
  }

  public static void show(NumericColumn column) {
    JFrame frame = new JFrame("Plot");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setSize(600, 400);

    PlotCanvas canvas = create(column);
    frame.add(canvas);
    frame.setVisible(true);
  }
}

package com.github.lwhite1.tablesaw.plotting.smile;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import smile.plot.PlotCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * Simple API for producing basic histogram plots directly from Tablesaw tables and columns
 */
public class SmileHist {

  private static final String WINDOW_TITLE = "Tablesaw";

  public static PlotCanvas create(String plotTitle, String xTitle, String yTitle, NumericColumn column) {
    PlotCanvas canvas = smile.plot.Histogram.plot(column.toDoubleArray());
    canvas.setForeground(Color.DARK_GRAY);
    canvas.setTitle(plotTitle);
    canvas.setAxisLabel(0, xTitle);
    canvas.setAxisLabel(1, yTitle);
    return canvas;
  }

  public static void show(String plotTitle, NumericColumn column) {
    JFrame frame = getjFrame(600, 400);
    PlotCanvas canvas = create(plotTitle, column.name(), "proportion", column);
    frame.add(canvas);
    frame.setVisible(true);
  }

  public static void show(NumericColumn column) {
    JFrame frame = getjFrame(600, 400);
    PlotCanvas canvas = create("", column.name(), "proportion", column);
    frame.add(canvas);
    frame.setVisible(true);
  }

  private static JFrame getjFrame(int width, int height) {
    JFrame frame = new JFrame(WINDOW_TITLE);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setSize(width, height);
    return frame;
  }

}

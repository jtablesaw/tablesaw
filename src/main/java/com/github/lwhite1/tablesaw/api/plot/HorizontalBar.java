package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.plotting.fx.FxPlot;
import com.github.lwhite1.tablesaw.plotting.fx.FxHorizontalBar;
import com.github.lwhite1.tablesaw.reducing.NumericSummaryTable;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;

import javax.swing.*;

public class HorizontalBar extends FxPlot {

  private static final String WINDOW_TITLE = "Tablesaw";

  public static void show(String title, CategoryColumn categoryColumn, NumericColumn numericColumn) throws Exception {

    SwingUtilities.invokeLater(() -> {
      try {
        initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public static void show(String title, ShortColumn categoryColumn, NumericColumn numericColumn) throws Exception {

    SwingUtilities.invokeLater(() -> {
      try {
        initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public static void show(String title, IntColumn categoryColumn, NumericColumn numericColumn) throws Exception {

    SwingUtilities.invokeLater(() -> {
      try {
        initAndShowGUI(title, categoryColumn, numericColumn, 640, 480);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public static void show(String title, NumericSummaryTable table) throws Exception {

    SwingUtilities.invokeLater(() -> {
      try {
        if (table.column(0) instanceof CategoryColumn) {
          initAndShowGUI(title, table.categoryColumn(0), table.nCol(1), 640, 480);
        }
        if (table.column(0) instanceof ShortColumn) {
          initAndShowGUI(title, table.shortColumn(0), table.nCol(1), 640, 480);
        }
        if (table.column(0) instanceof IntColumn) {
          initAndShowGUI(title, table.intColumn(0), table.nCol(1), 640, 480);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private static void initAndShowGUI(String title,
                                     CategoryColumn categoryColumn,
                                     NumericColumn numericColumn,
                                     int width,
                                     int height) throws Exception {
    // This method is invoked on the EDT thread
    final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
    BarChart<Number, String> chart = FxHorizontalBar.chart("", categoryColumn, numericColumn);
    Platform.runLater(() -> initFX(fxPanel, chart));
  }

  private static void initAndShowGUI(String title,
                                     ShortColumn categoryColumn,
                                     NumericColumn numericColumn,
                                     int width,
                                     int height) throws Exception {
    // This method is invoked on the EDT thread
    final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
    BarChart<Number, String> chart = FxHorizontalBar.chart("", categoryColumn, numericColumn);
    Platform.runLater(() -> initFX(fxPanel, chart));
  }

  private static void initAndShowGUI(String title,
                                     IntColumn categoryColumn,
                                     NumericColumn numericColumn,
                                     int width,
                                     int height) throws Exception {
    // This method is invoked on the EDT thread
    final JFXPanel fxPanel = getJfxPanel(WINDOW_TITLE, width, height);
    BarChart<Number, String> chart = FxHorizontalBar.chart("", categoryColumn, numericColumn);
    Platform.runLater(() -> initFX(fxPanel, chart));
  }

  private static void initFX(JFXPanel fxPanel, BarChart<Number, String> chart) {
    // This method is invoked on the JavaFX thread
    Scene scene = new Scene(chart, chart.getWidth(), chart.getHeight());
    fxPanel.setScene(scene);
  }
}

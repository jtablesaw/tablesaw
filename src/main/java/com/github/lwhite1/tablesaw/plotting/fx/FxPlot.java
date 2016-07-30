package com.github.lwhite1.tablesaw.plotting.fx;

import javafx.embed.swing.JFXPanel;

import javax.swing.*;

/**
 *
 */
public abstract class FxPlot {

  public static JFXPanel getJfxPanel(String title, int width, int height) {
    JFrame frame = new JFrame(title);
    final JFXPanel fxPanel = new JFXPanel();
    frame.add(fxPanel);
    frame.setSize(width, height);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    return fxPanel;
  }
}

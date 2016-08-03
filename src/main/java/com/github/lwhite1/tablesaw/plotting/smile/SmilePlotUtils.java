package com.github.lwhite1.tablesaw.plotting.smile;

import javax.swing.*;

/**
 *
 */
public class SmilePlotUtils {

  private static final String WINDOW_TITLE = "Tablesaw";

  static JFrame getjFrame(int width, int height) {
    JFrame frame = new JFrame(WINDOW_TITLE);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setSize(width, height);
    return frame;
  }

}

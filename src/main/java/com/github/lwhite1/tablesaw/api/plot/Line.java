package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.plotting.xchart.XchartLine;

/**
 *
 */
public class Line {
  public static void show(String chart, NumericColumn x, NumericColumn y) {
    XchartLine.show(chart, x, y);
  }
}

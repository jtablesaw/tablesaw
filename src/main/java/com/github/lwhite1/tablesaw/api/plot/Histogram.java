package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.plotting.smile.SmileHist;

/**
 *
 */
public class Histogram {


  public static void show(NumericColumn x) {
    SmileHist.show(x);
  }

  public static void show(String title, NumericColumn x) {
    SmileHist.show(title, x);
  }
}

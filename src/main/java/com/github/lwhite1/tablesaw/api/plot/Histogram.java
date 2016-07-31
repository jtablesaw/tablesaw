package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.plotting.smile.SmileHistogram;

/**
 *
 */
public class Histogram {


  public static void show(NumericColumn x) {
    SmileHistogram.show(x);
  }

  public static void show(double[] x) {
    SmileHistogram.show(x);
  }

  public static void show(String title, NumericColumn x) {
    SmileHistogram.show(title, x);
  }
}

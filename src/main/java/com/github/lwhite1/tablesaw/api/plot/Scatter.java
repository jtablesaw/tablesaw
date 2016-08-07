package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ml.regression.LeastSquares;
import com.github.lwhite1.tablesaw.plotting.xchart.XchartScatter;
import com.github.lwhite1.tablesaw.table.ViewGroup;

/**
 *
 */
public class Scatter {

  public static void show(NumericColumn x, NumericColumn y) {

    XchartScatter.show("Scatterplot", x, y);
  }

  public static void show(String title, NumericColumn x, NumericColumn y, ViewGroup groups) {
    XchartScatter.show(title, x, y, groups);
  }

  public static void show(String title, NumericColumn x, NumericColumn y) {
    XchartScatter.show(title, x, y);
  }

  public static void fittedVsResidual(LeastSquares model) {
    XchartScatter.show("Fitted v. Residuals", "Fitted", model.fitted(), "Residuals", model.residuals());
  }

  public static void actualVsFitted(LeastSquares model) {
    XchartScatter.show("Actual v. Fitted", "Actuals", model.actuals(), "Fitted", model.fitted());
  }
}

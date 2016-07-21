package com.github.lwhite1.tablesaw.smile.regression;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import com.google.common.base.Strings;
import smile.regression.OLS;

/**
 *
 */
public class LeastSquares {

  private final OLS model;
  private final NumericColumn[] explanatoryVars;
  // these are the values in the model dataset
  //private final DoubleSet modelValues;

  public static LeastSquares train(NumericColumn responseVar, NumericColumn... explanatoryVars) {

    double[] predicted = responseVar.toDoubleArray();
    OLS model = new OLS(DoubleArrays.to2dArray(explanatoryVars), predicted);
    return new LeastSquares(model, explanatoryVars);
  }

  public LeastSquares(OLS model, NumericColumn ... explanatoryVars) {
    this.model = model;
    this.explanatoryVars = explanatoryVars;
  }

  @Override
  public String toString() {
    String result = model.toString();
    result = result.replace("Intercept", "(Intercept)");
    // TODO(lwhite): This hack needed because Smile doesn't name the vars in it's output; we do, by string replacement.
    int maxNameLength = "(intercept)".length() - 1;
    for (int i = 0; i < explanatoryVars.length; i++) {
      String replacement = explanatoryVars[i].name();
      if (replacement.length() >= maxNameLength) {
        replacement = replacement.substring(0, maxNameLength);
      } else {
        replacement = Strings.padEnd(replacement, maxNameLength, ' ');
      }
      result = result.replaceFirst("Var " + (i + 1) + '\t', replacement);
    }
    return result;
  }

  public double[] residuals() {
    return model.residuals();
  }

  public double[] fitted() {
    return model.residuals();
  }

  public double adjustedRSquared() {
    return model.adjustedRSquared();
  }

  public double df() {
    return model.df();
  }

  public double error() {
    return model.error();
  }

  public double ftest() {
    return model.ftest();
  }

  public double pValue() {
    return model.pvalue();
  }

  public double intercept() {
    return model.intercept();
  }

  public double RSquared() {
    return model.RSquared();
  }

  public double RSS() {
    return model.RSS();
  }

  public double[][] ttest() {
    return model.ttest();
  }

  public double predict(double[] x) {
    return model.predict(x);
  }

  public double[] coefficients() {
    return model.coefficients();
  }
}

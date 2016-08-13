package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.NumericColumn;

/**
 *
 */
public abstract class AbstractClassifier {

  abstract int predictFromModel(double[] data);

  void populateMatrix(int[] labels, ConfusionMatrix confusion, NumericColumn[] predictors) {
    double[] data = new double[predictors.length];
    for (int row = 0; row < predictors[0].size(); row++) {
      for (int col = 0; col < predictors.length; col++) {
        data[col] = predictors[col].getFloat(row);
      }
      int prediction = predictFromModel(data);
      confusion.increment(prediction, labels[row]);
    }
  }
}

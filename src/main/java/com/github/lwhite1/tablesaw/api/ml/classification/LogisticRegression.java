package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.util.DoubleArrays;

/**
 *
 */
public class LogisticRegression {

  public static smile.classification.LogisticRegression train(IntColumn labels, NumericColumn... trainingData) {
    double[][] data = DoubleArrays.to2dArray(trainingData);
    return new smile.classification.LogisticRegression(data, labels.data().toIntArray());
  }

  public static smile.classification.LogisticRegression train(BooleanColumn labels, NumericColumn... trainingData) {
    double[][] data = DoubleArrays.to2dArray(trainingData);
    return new smile.classification.LogisticRegression(data, labels.toIntArray());
  }

}

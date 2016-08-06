package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import com.google.common.base.Preconditions;
import smile.classification.KNN;

/**
 *
 */
public class Knn {

  private final KNN<double[]> classifierModel;

  public static Knn learn(int k, ShortColumn labels, NumericColumn ... predictors) {

    KNN<double[]> classifierModel = KNN.learn(DoubleArrays.to2dArray(predictors), labels.toIntArray(), k);
    return new Knn(classifierModel);
  }

  private Knn(KNN<double[]> classifierModel) {
    this.classifierModel = classifierModel;
  }

  public int predict(double[] data) {
    return classifierModel.predict(data);
  }

  public ConfusionMatrix predictMatrix(ShortColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    ConfusionMatrix confusion = new ConfusionMatrix();

    for (int row = 0; row < predictors[0].size(); row++) {
      double[] data = new double[predictors.length];
      for (int col = 0; col < predictors.length; col++) {
        data[row] = predictors[col].getFloat(row);
      }
      int prediction = classifierModel.predict(data);
      confusion.increment(prediction, (int) labels.get(row));
    }
    return confusion;
  }

  public int[] predict(NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);
    int[] predictedLabels = new int[predictors[0].size()];
    for (int row = 0; row < predictors[0].size(); row++) {
      double[] data = new double[predictors.length];
      for (int col = 0; col < predictors.length; col++) {
        data[row] = predictors[col].getFloat(row);
      }
      predictedLabels[row] = classifierModel.predict(data);
    }
    return predictedLabels;
  }
}

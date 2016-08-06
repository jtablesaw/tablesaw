package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import com.google.common.base.Preconditions;
import smile.classification.KNN;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class Knn {

  private final KNN<double[]> classifierModel;

  public static Knn learn(int k, ShortColumn labels, NumericColumn ... predictors) {
    KNN<double[]> classifierModel = KNN.learn(DoubleArrays.to2dArray(predictors), labels.toIntArray(), k);
    return new Knn(classifierModel);
  }

  public static Knn learn(int k, IntColumn labels, NumericColumn ... predictors) {
    KNN<double[]> classifierModel = KNN.learn(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), k);
    return new Knn(classifierModel);
  }

  public static Knn learn(int k, BooleanColumn labels, NumericColumn ... predictors) {
    KNN<double[]> classifierModel = KNN.learn(DoubleArrays.to2dArray(predictors), labels.toIntArray(), k);
    return new Knn(classifierModel);
  }

  public static Knn learn(int k, CategoryColumn labels, NumericColumn ... predictors) {
    KNN<double[]> classifierModel = KNN.learn(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), k);
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

    SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new ConfusionMatrix(labelSet);

    populateMatrix(labels.toIntArray(), confusion, predictors);
    return confusion;
  }

  public ConfusionMatrix predictMatrix(IntColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new ConfusionMatrix(labelSet);

    populateMatrix(labels.data().toIntArray(), confusion, predictors);
    return confusion;
  }

  public ConfusionMatrix predictMatrix(BooleanColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new ConfusionMatrix(labelSet);

    populateMatrix(labels.toIntArray(), confusion, predictors);
    return confusion;
  }

  public ConfusionMatrix predictMatrix(CategoryColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new ConfusionMatrix(labelSet);

    populateMatrix(labels.data().toIntArray(), confusion, predictors);
    return confusion;
  }

  private void populateMatrix(int[] labels, ConfusionMatrix confusion, NumericColumn[] predictors) {
    for (int row = 0; row < predictors[0].size(); row++) {
      double[] data = new double[predictors.length];
      for (int col = 0; col < predictors.length; col++) {
        data[col] = predictors[col].getFloat(row);
      }
      int prediction = classifierModel.predict(data);
      confusion.increment(prediction, labels[row]);
    }
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

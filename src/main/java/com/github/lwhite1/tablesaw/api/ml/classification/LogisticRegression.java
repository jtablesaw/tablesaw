package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import com.google.common.base.Preconditions;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class LogisticRegression extends AbstractClassifier {

  private final smile.classification.LogisticRegression classifierModel;

  public static LogisticRegression learn(ShortColumn labels, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.toIntArray());
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(IntColumn labels, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.data().toIntArray());
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(BooleanColumn labels, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.toIntArray());
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(CategoryColumn labels, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.data().toIntArray());
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(ShortColumn labels, double lambda, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.toIntArray(), lambda);
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(IntColumn labels, double lambda, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), lambda);
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(BooleanColumn labels, double lambda, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.toIntArray(), lambda);
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(CategoryColumn labels, double lambda, NumericColumn ... predictors) {
    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), lambda);

    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(ShortColumn labels,
                                         double lambda,
                                         double tolerance,
                                         int maxIters,
                                         NumericColumn ... predictors) {

    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(
            DoubleArrays.to2dArray(predictors),
            labels.toIntArray(),
            lambda,
            tolerance,
            maxIters);
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(IntColumn labels,
                                         double lambda,
                                         double tolerance,
                                         int maxIters,
                                         NumericColumn ... predictors) {

    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(
            DoubleArrays.to2dArray(predictors),
            labels.data().toIntArray(),
            lambda,
            tolerance,
            maxIters);
    return new LogisticRegression(classifierModel);
  }

  public static LogisticRegression learn(BooleanColumn labels,
                                         double lambda,
                                         double tolerance,
                                         int maxIters,
                                         NumericColumn ... predictors) {

    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(
            DoubleArrays.to2dArray(predictors),
            labels.toIntArray(),
            lambda,
            tolerance,
            maxIters);
    return new LogisticRegression(classifierModel);
  }


  public static LogisticRegression learn(CategoryColumn labels,
                                         double lambda,
                                         double tolerance,
                                         int maxIters,
                                         NumericColumn ... predictors) {

    smile.classification.LogisticRegression classifierModel =
        new smile.classification.LogisticRegression(
            DoubleArrays.to2dArray(predictors),
            labels.data().toIntArray(),
            lambda,
            tolerance,
            maxIters
        );

    return new LogisticRegression(classifierModel);
  }

  private LogisticRegression(smile.classification.LogisticRegression classifierModel) {
    this.classifierModel = classifierModel;
  }

  public int predict(double[] data) {
    return classifierModel.predict(data);
  }

  public ConfusionMatrix predictMatrix(ShortColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new StandardConfusionMatrix(labelSet);

    populateMatrix(labels.toIntArray(), confusion, predictors);
    return confusion;
  }

  public ConfusionMatrix predictMatrix(IntColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new StandardConfusionMatrix(labelSet);

    populateMatrix(labels.data().toIntArray(), confusion, predictors);
    return confusion;
  }

  public ConfusionMatrix predictMatrix(CategoryColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<String> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new CategoryConfusionMatrix(labels, labelSet);

    populateMatrix(labels.data().toIntArray(), confusion, predictors);
    return confusion;
  }

  public ConfusionMatrix predictMatrix(BooleanColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new StandardConfusionMatrix(labelSet);

    populateMatrix(labels.toIntArray(), confusion, predictors);
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

  @Override
  int predictFromModel(double[] data) {
    return classifierModel.predict(data);
  }

  public double logLikelihood() {
    return classifierModel.loglikelihood();
  }

  public double predictFromModel(double[] x, double[] posteriori) {
    return classifierModel.predict(x, posteriori);
  }

  public double predictFromModel(int row, double[] posteriori, NumericColumn ... predictors) {
    double[] data = new double[predictors.length];
    for (int col = 0; col < predictors.length; col++) {
      data[row] = predictors[col].getFloat(row);
    }
    return classifierModel.predict(data, posteriori);
  }
}

package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import com.google.common.base.Preconditions;
import smile.classification.LDA;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class Lda {

  private final LDA classifierModel;

  public static Lda learn(ShortColumn labels, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray());
    return new Lda(classifierModel);
  }

  public static Lda learn(IntColumn labels, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray());
    return new Lda(classifierModel);
  }

  public static Lda learn(BooleanColumn labels, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray());
    return new Lda(classifierModel);
  }

  public static Lda learn(CategoryColumn labels, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray());
    return new Lda(classifierModel);
  }

  public static Lda learn(ShortColumn labels, double[] priors, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors);
    return new Lda(classifierModel);
  }

  public static Lda learn(IntColumn labels, double[] priors, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors);
    return new Lda(classifierModel);
  }

  public static Lda learn(BooleanColumn labels, double[] priors, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors);
    return new Lda(classifierModel);
  }

  public static Lda learn(CategoryColumn labels, double[] priors, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors);
    return new Lda(classifierModel);
  }

  public static Lda learn(ShortColumn labels, double[] priors, double tolerance, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors, tolerance);
    return new Lda(classifierModel);
  }

  public static Lda learn(IntColumn labels, double[] priors, double tolerance, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors, tolerance);
    return new Lda(classifierModel);
  }

  public static Lda learn(BooleanColumn labels, double[] priors, double tolerance, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors, tolerance);
    return new Lda(classifierModel);
  }

  public static Lda learn(CategoryColumn labels, double[] priors, double tolerance, NumericColumn ... predictors) {
    LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors, tolerance);
    return new Lda(classifierModel);
  }

  private Lda(LDA classifierModel) {
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

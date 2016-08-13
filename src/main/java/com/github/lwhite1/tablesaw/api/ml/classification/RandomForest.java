package com.github.lwhite1.tablesaw.api.ml.classification;

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
public class RandomForest extends AbstractClassifier {

  private final smile.classification.RandomForest classifierModel;


  public static RandomForest learn(int nTrees, IntColumn classes, NumericColumn ... columns) {
    int[] classArray = classes.data().toIntArray();
    return new RandomForest(nTrees, classArray, columns);
  }

  public static RandomForest learn(int nTrees, ShortColumn classes, NumericColumn ... columns) {
    int[] classArray = classes.toIntArray();
    return new RandomForest(nTrees, classArray, columns);
  }

  public static RandomForest learn(int nTrees, CategoryColumn classes, NumericColumn ... columns) {
    int[] classArray = classes.data().toIntArray();
    return new RandomForest(nTrees, classArray, columns);
  }

  private RandomForest(int nTrees, int[] classArray, NumericColumn ... columns) {
    double[][] data = DoubleArrays.to2dArray(columns);
    this.classifierModel = new smile.classification.RandomForest(data, classArray, nTrees);
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

  public ConfusionMatrix predictMatrix(CategoryColumn labels, NumericColumn ... predictors) {
    Preconditions.checkArgument(predictors.length > 0);

    SortedSet<String> labelSet = new TreeSet<>(labels.asSet());
    ConfusionMatrix confusion = new CategoryConfusionMatrix(labels, labelSet);

    populateMatrix(labels.data().toIntArray(), confusion, predictors);
    return confusion;
  }

  @Override
  int predictFromModel(double[] data) {
    return classifierModel.predict(data);
  }
}

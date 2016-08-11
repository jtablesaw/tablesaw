package com.github.lwhite1.tablesaw.api.ml.classification;

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


  public static RandomForest learn(int maxNodes, IntColumn classes, NumericColumn ... columns) {
    int[] classArray = classes.data().toIntArray();
    return new RandomForest(maxNodes, classArray, columns);
  }

  public static RandomForest learn(int maxNodes, ShortColumn classes, NumericColumn ... columns) {
    int[] classArray = classes.toIntArray();
    return new RandomForest(maxNodes, classArray, columns);
  }

  private RandomForest(int maxNodes, int[] classArray, NumericColumn ... columns) {
    double[][] data = DoubleArrays.to2dArray(columns);
    this.classifierModel = new smile.classification.RandomForest(data, classArray, maxNodes);
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

  @Override
  int predictFromModel(double[] data) {
    return classifierModel.predict(data);
  }
}

package com.github.lwhite1.tablesaw.api.ml.classification;

/**
 *
 */
public interface ConfusionMatrix {
  void increment(Integer predicted, Integer actual);

  @Override
  String toString();

  com.github.lwhite1.tablesaw.api.Table toTable();

  double accuracy();
}

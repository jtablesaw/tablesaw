package com.github.lwhite1.tablesaw.api.ml.clustering;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import smile.clustering.KMeans;

/**
 * K-Means clustering
 */
public class Kmeans {

  private final KMeans kMeans;

  public Kmeans(int k, NumericColumn ... columns) {
    double[][] input = DoubleArrays.to2dArray(columns);
    this.kMeans = new KMeans(input, k);
  }

  public Kmeans(int k, int maxIterations, NumericColumn ... columns) {
    double[][] input = DoubleArrays.to2dArray(columns);
    this.kMeans = new KMeans(input, k, maxIterations);
  }

  public int predict(double[] x) {
    return kMeans.predict(x);
  }

  public double[][] centroids() {
    return kMeans.centroids();
  }

  public double distortion() {
    return kMeans.distortion();
  }

  public int getClusterCount() {
    return kMeans.getNumClusters();
  }

  public int[] getClusterLabels() {
    return kMeans.getClusterLabel();
  }

  public int[] getClusterSizes() {
    return kMeans.getClusterSize();
  }

  public Table clustered(Column labels) {
    Table table = Table.create("Clusters");
    CategoryColumn labelColumn = CategoryColumn.create("Label");
    IntColumn clusterColumn = IntColumn.create("Cluster");
    table.addColumn(labelColumn);
    table.addColumn(clusterColumn);
    int[] clusters = kMeans.getClusterLabel();
    for (int i = 0 ; i < clusters.length; i++) {
      labelColumn.addCell(labels.getString(i));
      clusterColumn.add(clusters[i]);
    }
    table = table.sortAscendingOn("Cluster", "Label");
    return table;
  }
}

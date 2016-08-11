package com.github.lwhite1.tablesaw.api.ml.clustering;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import smile.clustering.GMeans;

/**
 *
 */
public class Gmeans {

  private final GMeans model;

  private final NumericColumn[] inputColumns;


  public Gmeans(int maxK, NumericColumn... columns) {

    double[][] data = DoubleArrays.to2dArray(columns);
    this.model = new GMeans(data, maxK);
    this.inputColumns = columns;
  }

  public int predict(double[] x) {
    return model.predict(x);
  }

  public double[][] centroids() {
    return model.centroids();
  }

  public double distortion() {
    return model.distortion();
  }

  public int getClusterCount() {
    return model.getNumClusters();
  }

  public int[] getClusterLabels() {
    return model.getClusterLabel();
  }

  public int[] getClusterSizes() {
    return model.getClusterSize();
  }

  public Table labeledCentroids() {
    Table table = Table.create("Centroids");
    CategoryColumn labelColumn = CategoryColumn.create("Cluster");
    table.addColumn(labelColumn);

    for (int i = 0; i < inputColumns.length; i++) {
      FloatColumn centroid = FloatColumn.create(inputColumns[i].name());
      table.addColumn(centroid);
    }

    double[][] centroids = model.centroids();

    for (int i = 0 ; i < centroids.length; i++) {
      labelColumn.addCell(String.valueOf(i));
      double[] values = centroids[i];
      for (int k = 0; k < values.length; k++) {
        table.floatColumn(k + 1).add((float) values[k]);
      }
    }
    return table;
  }
}

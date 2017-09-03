package tech.tablesaw.api.ml.clustering;

import smile.clustering.XMeans;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.util.DoubleArrays;

public class Xmeans {

    private final XMeans model;
    private final NumericColumn[] inputColumns;

    public Xmeans(int maxK, NumericColumn... columns) {
        double[][] data = DoubleArrays.to2dArray(columns);
        this.model = new XMeans(data, maxK);
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
        CategoryColumn labelColumn = new CategoryColumn("Cluster");
        table.addColumn(labelColumn);

        for (int i = 0; i < inputColumns.length; i++) {
            FloatColumn centroid = new FloatColumn(inputColumns[i].name());
            table.addColumn(centroid);
        }

        double[][] centroids = model.centroids();

        for (int i = 0; i < centroids.length; i++) {
            labelColumn.appendCell(String.valueOf(i));
            double[] values = centroids[i];
            for (int k = 0; k < values.length; k++) {
                table.floatColumn(k + 1).append((float) values[k]);
            }
        }
        return table;
    }
}

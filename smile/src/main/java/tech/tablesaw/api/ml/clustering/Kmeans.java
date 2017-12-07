/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.api.ml.clustering;

import smile.clustering.KMeans;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.util.DoubleArrays;

/**
 * K-Means clustering
 */
public class Kmeans {

    private final KMeans kMeans;
    private final NumericColumn[] inputColumns;

    public Kmeans(int k, NumericColumn... columns) {
        double[][] input = DoubleArrays.to2dArray(columns);
        this.kMeans = new KMeans(input, k);
        this.inputColumns = columns;
    }

    public Kmeans(int k, int maxIterations, NumericColumn... columns) {
        double[][] input = DoubleArrays.to2dArray(columns);
        this.kMeans = new KMeans(input, k, maxIterations);
        this.inputColumns = columns;
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
        CategoryColumn labelColumn = new CategoryColumn("Label");
        IntColumn clusterColumn = new IntColumn("Cluster");
        table.addColumn(labelColumn);
        table.addColumn(clusterColumn);
        int[] clusters = kMeans.getClusterLabel();
        for (int i = 0; i < clusters.length; i++) {
            labelColumn.appendCell(labels.getString(i));
            clusterColumn.append(clusters[i]);
        }
        table = table.sortAscendingOn("Cluster", "Label");
        return table;
    }

    public Table labeledCentroids() {
        Table table = Table.create("Centroids");
        CategoryColumn labelColumn = new CategoryColumn("Cluster");
        table.addColumn(labelColumn);

        for (NumericColumn inputColumn : inputColumns) {
            FloatColumn centroid = new FloatColumn(inputColumn.name());
            table.addColumn(centroid);
        }

        double[][] centroids = kMeans.centroids();

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

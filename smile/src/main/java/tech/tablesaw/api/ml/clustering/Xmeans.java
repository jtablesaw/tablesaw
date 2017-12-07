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

        for (NumericColumn inputColumn : inputColumns) {
            FloatColumn centroid = new FloatColumn(inputColumn.name());
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

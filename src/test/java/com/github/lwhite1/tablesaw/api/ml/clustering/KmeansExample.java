package com.github.lwhite1.tablesaw.api.ml.clustering;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.plot.Scatter;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class KmeansExample {

    public static void main(String[] args) throws IOException {
        Table t = Table.createFromCsv("data/whiskey.csv");

        Kmeans model = new Kmeans(
                5,
                t.nCol(2),
                t.nCol(3),
                t.nCol(4),
                t.nCol(5),
                t.nCol(6),
                t.nCol(7),
                t.nCol(8),
                t.nCol(9),
                t.nCol(10),
                t.nCol(11),
                t.nCol(12),
                t.nCol(13)
        );

        out("Distortion: " + model.distortion());
        out("Cluster count: " + model.getClusterCount());
        out(Arrays.toString(model.getClusterLabels()));
        out(Arrays.toString(model.getClusterSizes()));

        //out(model.clustered(t.column(1)).printHtml());

        out(model.labeledCentroids().print());

        int n = t.rowCount();
        double[] kValues = new double[n - 2];
        double[] distortions = new double[n - 2];

        for (int k = 2; k < n; k++) {
            kValues[k - 2] = k;
            Kmeans kmeans = new Kmeans(k,
                    t.nCol(2),
                    t.nCol(3),
                    t.nCol(4),
                    t.nCol(5),
                    t.nCol(6),
                    t.nCol(7),
                    t.nCol(8),
                    t.nCol(9),
                    t.nCol(10),
                    t.nCol(11),
                    t.nCol(12),
                    t.nCol(13)
            );
            distortions[k - 2] = kmeans.distortion();
        }
        Scatter.show(kValues, "k", distortions, "distortion");
    }

    private static void out(Object object) {
        System.out.println(String.valueOf(object));
    }
}
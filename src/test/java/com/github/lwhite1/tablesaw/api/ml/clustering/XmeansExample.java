package com.github.lwhite1.tablesaw.api.ml.clustering;

import com.github.lwhite1.tablesaw.api.Table;

import java.util.Arrays;

/**
 * An example program illustrating the use of X-means clustering
 */
public class XmeansExample {

    public static void main(String[] args) throws Exception {

        Table t = Table.createFromCsv("data/whiskey.csv");

        Xmeans model = new Xmeans(
                10,
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
        out(model.labeledCentroids().print());
    }

    private static void out(Object object) {
        System.out.println(String.valueOf(object));
    }

}
package com.github.lwhite1.tablesaw.api.ml;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ml.classification.ConfusionMatrix;
import com.github.lwhite1.tablesaw.api.ml.classification.Knn;
import com.github.lwhite1.tablesaw.api.plot.Scatter;

/**
 *
 */
public class KnnExample {

  public static void main(String[] args) throws Exception {

    Table example = Table.createFromCsv("data/KNN_Example_1.csv");
    out(example.structure().printHtml());

    // show all the label values
    out(example.shortColumn("Label").asSet());

    Scatter.show("Example data", example.nCol(0), example.nCol(1), example.splitOn(example.shortColumn(2)));

    // two fold validation
    Table[] splits = example.sampleSplit(.5);
    Table train = splits[0];
    Table test = splits[1];

    Knn knn = Knn.learn(2, train.shortColumn(2), train.nCol("X"), train.nCol("Y"));

    ConfusionMatrix matrix = knn.predictMatrix(test.shortColumn(2), test.nCol("X"), test.nCol("Y"));

    // Prediction
    out(matrix.toTable().printHtml());
    out(String.valueOf(matrix.accuracy()));
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

}

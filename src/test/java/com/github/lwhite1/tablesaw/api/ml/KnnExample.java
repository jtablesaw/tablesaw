package com.github.lwhite1.tablesaw.api.ml;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ml.classification.ConfusionMatrix;
import com.github.lwhite1.tablesaw.api.plot.Scatter;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import smile.classification.KNN;

/**
 *
 */
public class KnnExample {

  public static void main(String[] args) throws Exception {

    Table example = Table.createFromCsv("data/KNN_Example_1.csv");
    out(example.structure().print());

    out(example.shortColumn(2).summary().print());
    Scatter.show("Example data", example.nCol(0), example.nCol(1), example.splitOn(example.shortColumn(2)));

    // two fold validation
    Table[] splits = example.sampleSplit(.5);
    Table train = splits[0];
    Table test = splits[1];

    KNN<double[]> knn = KNN.learn(
          DoubleArrays.to2dArray(train.nCol("X"), train.nCol("Y")),
          train.shortColumn(2).toIntArray(), 2);

    int[] predicted = new int[test.rowCount()];
    ConfusionMatrix confusion = new ConfusionMatrix();
    for (int row : test) {
      double[] data = new double[2];
      data[0] = test.floatColumn(0).getFloat(row);
      data[1] = test.floatColumn(1).getFloat(row);
      predicted[row] = knn.predict(data);

      confusion.increment(predicted[row], (int) test.shortColumn(2).get(row));
    }
    // Prediction
    out(confusion);
    out(String.valueOf(confusion.accuracy()));
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

}

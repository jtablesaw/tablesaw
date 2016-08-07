package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import org.junit.Test;
import smile.classification.KNN;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class ConfusionMatrixTest {

  @Test
  public void testAsTable() throws Exception {

    Table example = Table.createFromCsv("data/KNN_Example_1.csv");
    Table[] splits = example.sampleSplit(.5);
    Table train = splits[0];
    Table test = splits[1];

    KNN<double[]> knn = KNN.learn(
        DoubleArrays.to2dArray(train.nCol("X"), train.nCol("Y")),
        train.shortColumn(2).toIntArray(), 2);

    int[] predicted = new int[test.rowCount()];
    SortedSet<Object> lableSet = new TreeSet<>(train.shortColumn(2).asSet());
    ConfusionMatrix confusion = new ConfusionMatrix(lableSet);
    for (int row : test) {
      double[] data = new double[2];
      data[0] = test.floatColumn(0).getFloat(row);
      data[1] = test.floatColumn(1).getFloat(row);
      predicted[row] = knn.predict(data);
      confusion.increment((int) test.shortColumn(2).get(row), predicted[row]);
    }

    //out(confusion);
    //out(confusion.toTable().toString());
    //out(confusion.toTable().print());
  }

/*
  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }
*/
}
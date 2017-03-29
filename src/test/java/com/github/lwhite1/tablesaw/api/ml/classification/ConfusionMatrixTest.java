package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import com.github.lwhite1.tablesaw.util.Example;
import org.junit.Test;
import smile.classification.KNN;

import java.util.SortedSet;
import java.util.TreeSet;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 *
 */
public class ConfusionMatrixTest extends Example {

    @Test
    public void testAsTable() throws Exception {

        Table example = Table.createFromCsv("data/KNN_Example_1.csv");
        BooleanColumn booleanTarget = example.selectIntoColumn("bt", column("Label").isEqualTo(1));

        Table[] splits = example.sampleSplit(.5);
        Table train = splits[0];
        Table test = splits[1];

        KNN<double[]> knn = KNN.learn(
                DoubleArrays.to2dArray(train.nCol("X"), train.nCol("Y")),
                train.shortColumn(2).toIntArray(), 2);

        int[] predicted = new int[test.rowCount()];
        SortedSet<Object> lableSet = new TreeSet<>(train.shortColumn(2).asSet());
        ConfusionMatrix confusion = new StandardConfusionMatrix(lableSet);
        for (int row : test) {
            double[] data = new double[2];
            data[0] = test.floatColumn(0).getFloat(row);
            data[1] = test.floatColumn(1).getFloat(row);
            predicted[row] = knn.predict(data);
            confusion.increment((int) test.shortColumn(2).get(row), predicted[row]);
        }
    }

    @Test
    public void testWithBooleanColumn() throws Exception {

        Table example = Table.createFromCsv("data/KNN_Example_1.csv");
        BooleanColumn booleanTarget = example.selectIntoColumn("bt", column("Label").isEqualTo(1));
        example.addColumn(booleanTarget);
        Table[] splits = example.sampleSplit(.5);
        Table train = splits[0];
        Table test = splits[1];

        LogisticRegression lr = LogisticRegression.learn(
                train.booleanColumn(3), train.nCol("X"), train.nCol("Y"));

        System.out.println(lr.predictMatrix(test.booleanColumn(3), test.floatColumn(0), test.floatColumn(1)).toString
                ());


        int[] predicted = new int[test.rowCount()];
        SortedSet<Object> lableSet = new TreeSet<>(train.shortColumn(2).asSet());
        ConfusionMatrix confusion = new StandardConfusionMatrix(lableSet);
        for (int row : test) {
            double[] data = new double[2];
            data[0] = test.floatColumn(0).getFloat(row);
            data[1] = test.floatColumn(1).getFloat(row);
            predicted[row] = lr.predict(data);
            confusion.increment((int) test.shortColumn(2).get(row), predicted[row]);
        }

        System.out.println(confusion.toString());
    }
}
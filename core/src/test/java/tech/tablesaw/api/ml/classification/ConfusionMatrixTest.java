package tech.tablesaw.api.ml.classification;

import org.junit.Test;
import smile.classification.KNN;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.ml.classification.ConfusionMatrix;
import tech.tablesaw.api.ml.classification.LogisticRegression;
import tech.tablesaw.api.ml.classification.StandardConfusionMatrix;
import tech.tablesaw.util.DoubleArrays;
import tech.tablesaw.util.Example;

import static tech.tablesaw.api.QueryHelper.column;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class ConfusionMatrixTest extends Example {

    @Test
    public void testAsTable() throws Exception {

        Table example = Table.read().csv("../data/KNN_Example_1.csv");

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

        Table example = Table.read().csv("../data/KNN_Example_1.csv");
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
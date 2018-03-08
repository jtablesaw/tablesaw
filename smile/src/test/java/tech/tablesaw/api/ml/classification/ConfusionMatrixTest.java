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

package tech.tablesaw.api.ml.classification;

import org.junit.Test;
import smile.classification.KNN;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.util.DoubleArrays;

import static org.junit.Assert.assertNotNull;
import static tech.tablesaw.api.QueryHelper.column;

import java.util.SortedSet;
import java.util.TreeSet;

public class ConfusionMatrixTest {

    @Test
    public void testAsTable() throws Exception {

        Table example = Table.read().csv("../data/KNN_Example_1.csv");

        Table[] splits = example.sampleSplit(.5);
        Table train = splits[0];
        Table test = splits[1];

        KNN<double[]> knn = KNN.learn(
                DoubleArrays.to2dArray(train.nCol("X"), train.nCol("Y")),
                train.shortColumn(2).asIntArray(), 2);

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

        //TODO(lwhite): Better tests

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

        //TODO(lwhite): Better tests
        assertNotNull(confusion);
    }
}
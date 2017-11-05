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

import tech.tablesaw.api.Table;
import tech.tablesaw.api.ml.classification.ConfusionMatrix;
import tech.tablesaw.api.ml.classification.Lda;
import tech.tablesaw.api.plot.Scatter;
import tech.tablesaw.util.Example;

public class LdaExample extends Example {

    public static void main(String[] args) throws Exception {

        Table example = Table.read().csv("../data/KNN_Example_1.csv");
        out(example.structure().printHtml());

        // show all the label values
        out(example.shortColumn("Label").asSet());

        Scatter.show("Example data", example.nCol(0), example.nCol(1), example.splitOn(example.shortColumn(2)));

        // two fold validation
        Table[] splits = example.sampleSplit(.5);
        Table train = splits[0];
        Table test = splits[1];

        Lda model = Lda.learn(train.shortColumn(2), train.nCol("X"), train.nCol("Y"));

        ConfusionMatrix matrix = model.predictMatrix(test.shortColumn(2), test.nCol("X"), test.nCol("Y"));

        // Prediction
        out(matrix.toTable().printHtml());
        out(String.valueOf(matrix.accuracy()));
    }
}

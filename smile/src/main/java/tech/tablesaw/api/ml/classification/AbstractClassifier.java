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

import tech.tablesaw.api.NumericColumn;

public abstract class AbstractClassifier {

    abstract int predictFromModel(double[] data);

    void populateMatrix(int[] labels, ConfusionMatrix confusion, NumericColumn[] predictors) {
        double[] data = new double[predictors.length];
        for (int row = 0; row < predictors[0].size(); row++) {
            for (int col = 0; col < predictors.length; col++) {
                data[col] = predictors[col].getFloat(row);
            }
            int prediction = predictFromModel(data);
            confusion.increment(prediction, labels[row]);
        }
    }
}

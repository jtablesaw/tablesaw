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

import com.google.common.base.Preconditions;

import tech.tablesaw.api.IntConvertibleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.util.DoubleArrays;

import java.util.SortedSet;
import java.util.TreeSet;

public class RandomForest extends AbstractClassifier {

    private final smile.classification.RandomForest classifierModel;

    private RandomForest(int nTrees, int[] classArray, NumericColumn... columns) {
        double[][] data = DoubleArrays.to2dArray(columns);
        this.classifierModel = new smile.classification.RandomForest(data, classArray, nTrees);
    }

    public static RandomForest learn(int nTrees, IntConvertibleColumn classes, NumericColumn... columns) {
        int[] classArray = classes.asIntArray();
        return new RandomForest(nTrees, classArray, columns);
    }

    public int predict(double[] data) {
        return classifierModel.predict(data);
    }

    public ConfusionMatrix predictMatrix(IntConvertibleColumn labels, NumericColumn... predictors) {
        Preconditions.checkArgument(predictors.length > 0);

        SortedSet<Object> labelSet = new TreeSet<>(labels.asIntegerSet());
        ConfusionMatrix confusion = new StandardConfusionMatrix(labelSet);

        populateMatrix(labels.asIntArray(), confusion, predictors);
        return confusion;
    }

    @Override
    int predictFromModel(double[] data) {
        return classifierModel.predict(data);
    }
}

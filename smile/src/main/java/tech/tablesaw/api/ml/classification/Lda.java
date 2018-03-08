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
import smile.classification.LDA;
import tech.tablesaw.api.IntConvertibleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.util.DoubleArrays;

import java.util.SortedSet;
import java.util.TreeSet;

public class Lda extends AbstractClassifier {

    private final LDA classifierModel;

    private Lda(LDA classifierModel) {
        this.classifierModel = classifierModel;
    }

    public static Lda learn(IntConvertibleColumn labels, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.asIntArray());
        return new Lda(classifierModel);
    }

    public static Lda learn(IntConvertibleColumn labels, double[] priors, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.asIntArray(), priors);
        return new Lda(classifierModel);
    }

    public static Lda learn(IntConvertibleColumn labels, double[] priors, double tolerance, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.asIntArray(), priors, tolerance);
        return new Lda(classifierModel);
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

    public int[] predict(NumericColumn... predictors) {
        Preconditions.checkArgument(predictors.length > 0);
        int[] predictedLabels = new int[predictors[0].size()];
        for (int row = 0; row < predictors[0].size(); row++) {
            double[] data = new double[predictors.length];
            for (int col = 0; col < predictors.length; col++) {
                data[row] = predictors[col].getFloat(row);
            }
            predictedLabels[row] = classifierModel.predict(data);
        }
        return predictedLabels;
    }

    @Override
    int predictFromModel(double[] data) {
        return classifierModel.predict(data);
    }
}

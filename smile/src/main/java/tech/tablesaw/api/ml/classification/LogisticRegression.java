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

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.IntConvertibleColumn;
import tech.tablesaw.util.DoubleArrays;

import java.util.SortedSet;
import java.util.TreeSet;

public class LogisticRegression extends AbstractClassifier {

    private final smile.classification.LogisticRegression classifierModel;

    private LogisticRegression(smile.classification.LogisticRegression classifierModel) {
        this.classifierModel = classifierModel;
    }

    public static LogisticRegression learn(IntConvertibleColumn labels, NumericColumn... predictors) {
        smile.classification.LogisticRegression classifierModel =
                new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.asIntArray());
        return new LogisticRegression(classifierModel);
    }

    public static LogisticRegression learn(IntConvertibleColumn labels, double lambda, NumericColumn... predictors) {
        smile.classification.LogisticRegression classifierModel =
                new smile.classification.LogisticRegression(DoubleArrays.to2dArray(predictors), labels.asIntArray(),
                        lambda);
        return new LogisticRegression(classifierModel);
    }

    public static LogisticRegression learn(IntConvertibleColumn labels,
                                           double lambda,
                                           double tolerance,
                                           int maxIters,
                                           NumericColumn... predictors) {

        smile.classification.LogisticRegression classifierModel =
                new smile.classification.LogisticRegression(
                        DoubleArrays.to2dArray(predictors),
                        labels.asIntArray(),
                        lambda,
                        tolerance,
                        maxIters);
        return new LogisticRegression(classifierModel);
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
            for (NumericColumn predictor : predictors) {
                data[row] = predictor.getFloat(row);
            }
            predictedLabels[row] = classifierModel.predict(data);
        }
        return predictedLabels;
    }

    @Override
    int predictFromModel(double[] data) {
        return classifierModel.predict(data);
    }

    public double logLikelihood() {
        return classifierModel.loglikelihood();
    }

    public double predictFromModel(double[] x, double[] posteriori) {
        return classifierModel.predict(x, posteriori);
    }

    public double predictFromModel(int row, double[] posteriori, NumericColumn... predictors) {
        double[] data = new double[predictors.length];
        for (NumericColumn predictor : predictors) {
            data[row] = predictor.getFloat(row);
        }
        return classifierModel.predict(data, posteriori);
    }
}

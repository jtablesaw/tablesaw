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
        int[] classArray = classes.toIntArray();
        return new RandomForest(nTrees, classArray, columns);
    }

    public int predict(double[] data) {
        return classifierModel.predict(data);
    }

    public ConfusionMatrix predictMatrix(IntConvertibleColumn labels, NumericColumn... predictors) {
        Preconditions.checkArgument(predictors.length > 0);

        SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
        ConfusionMatrix confusion = new StandardConfusionMatrix(labelSet);

        populateMatrix(labels.toIntArray(), confusion, predictors);
        return confusion;
    }

    @Override
    int predictFromModel(double[] data) {
        return classifierModel.predict(data);
    }
}

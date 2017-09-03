package tech.tablesaw.api.ml.classification;

import com.google.common.base.Preconditions;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntConvertibleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.util.DoubleArrays;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class DecisionTree extends AbstractClassifier {

    private final smile.classification.DecisionTree classifierModel;

    private DecisionTree(int maxNodes, int[] classArray, NumericColumn... columns) {
        double[][] data = DoubleArrays.to2dArray(columns);
        this.classifierModel = new smile.classification.DecisionTree(data, classArray, maxNodes);
    }

    public static DecisionTree learn(int maxNodes, IntConvertibleColumn classes, NumericColumn... columns) {
        int[] classArray = classes.toIntArray();
        return new DecisionTree(maxNodes, classArray, columns);
    }

    public int predict(double[] data) {
        return classifierModel.predict(data);
    }

    public ConfusionMatrix predictMatrix(ShortColumn labels, NumericColumn... predictors) {
        Preconditions.checkArgument(predictors.length > 0);

        SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
        ConfusionMatrix confusion = new StandardConfusionMatrix(labelSet);

        populateMatrix(labels.toIntArray(), confusion, predictors);
        return confusion;
    }

    public ConfusionMatrix predictMatrix(CategoryColumn labels, NumericColumn... predictors) {
        Preconditions.checkArgument(predictors.length > 0);

        SortedSet<String> labelSet = new TreeSet<>(labels.asSet());
        ConfusionMatrix confusion = new CategoryConfusionMatrix(labels, labelSet);

        populateMatrix(labels.data().toIntArray(), confusion, predictors);
        return confusion;
    }

    @Override
    int predictFromModel(double[] data) {
        return classifierModel.predict(data);
    }
}

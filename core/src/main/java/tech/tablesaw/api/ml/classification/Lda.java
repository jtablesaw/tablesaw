package tech.tablesaw.api.ml.classification;

import com.google.common.base.Preconditions;
import smile.classification.LDA;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.util.DoubleArrays;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class Lda extends AbstractClassifier {

    private final LDA classifierModel;

    private Lda(LDA classifierModel) {
        this.classifierModel = classifierModel;
    }

    public static Lda learn(ShortColumn labels, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray());
        return new Lda(classifierModel);
    }

    public static Lda learn(IntColumn labels, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray());
        return new Lda(classifierModel);
    }

    public static Lda learn(BooleanColumn labels, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray());
        return new Lda(classifierModel);
    }

    public static Lda learn(CategoryColumn labels, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray());
        return new Lda(classifierModel);
    }

    public static Lda learn(ShortColumn labels, double[] priors, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors);
        return new Lda(classifierModel);
    }

    public static Lda learn(IntColumn labels, double[] priors, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors);
        return new Lda(classifierModel);
    }

    public static Lda learn(BooleanColumn labels, double[] priors, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors);
        return new Lda(classifierModel);
    }

    public static Lda learn(CategoryColumn labels, double[] priors, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors);
        return new Lda(classifierModel);
    }

    public static Lda learn(ShortColumn labels, double[] priors, double tolerance, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors, tolerance);
        return new Lda(classifierModel);
    }

    public static Lda learn(IntColumn labels, double[] priors, double tolerance, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors,
                tolerance);
        return new Lda(classifierModel);
    }

    public static Lda learn(BooleanColumn labels, double[] priors, double tolerance, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.toIntArray(), priors, tolerance);
        return new Lda(classifierModel);
    }

    public static Lda learn(CategoryColumn labels, double[] priors, double tolerance, NumericColumn... predictors) {
        LDA classifierModel = new LDA(DoubleArrays.to2dArray(predictors), labels.data().toIntArray(), priors,
                tolerance);
        return new Lda(classifierModel);
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

    public ConfusionMatrix predictMatrix(IntColumn labels, NumericColumn... predictors) {
        Preconditions.checkArgument(predictors.length > 0);

        SortedSet<Object> labelSet = new TreeSet<>(labels.asSet());
        ConfusionMatrix confusion = new StandardConfusionMatrix(labelSet);

        populateMatrix(labels.data().toIntArray(), confusion, predictors);
        return confusion;
    }

    public ConfusionMatrix predictMatrix(BooleanColumn labels, NumericColumn... predictors) {
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

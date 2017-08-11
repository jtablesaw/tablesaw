package tech.tablesaw.api.ml.classification;

/**
 *
 */
public interface ConfusionMatrix {
    void increment(Integer predicted, Integer actual);

    @Override
    String toString();

    tech.tablesaw.api.Table toTable();

    double accuracy();
}

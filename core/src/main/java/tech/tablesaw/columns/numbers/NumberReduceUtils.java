package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

public interface NumberReduceUtils extends Column {

    /**
     * Returns the number of unique values in this column, excluding missing values
     */
    @Override
    default int countUnique() {
        DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!NumberColumn.isMissing(get(i))) {
                doubles.add(get(i));
            }
        }
        return doubles.size();
    }

    // Reduce functions applied to the whole column
    default double sum() {
        return sum.agg(this.asDoubleArray());
    }

    default double product() {
        return product.agg(this.asDoubleArray());
    }

    default double mean() {
        return mean.agg(this.asDoubleArray());
    }

    default double median() {
        return median.agg(this.asDoubleArray());
    }

    default double quartile1() {
        return quartile1.agg(this.asDoubleArray());
    }

    default double quartile3() {
        return quartile3.agg(this.asDoubleArray());
    }

    default double percentile(double percentile) {
        return AggregateFunctions.percentile(this.asDoubleArray(), percentile);
    }

    default double range() {
        return range.agg(this.asDoubleArray());
    }

    default double max() {
        return max.agg(this.asDoubleArray());
    }

    default double min() {
        return min.agg(this.asDoubleArray());
    }

    default double variance() {
        return variance.agg(this.asDoubleArray());
    }

    default double populationVariance() {
        return populationVariance.agg(this.asDoubleArray());
    }

    default double standardDeviation() {
        return stdDev.agg(this.asDoubleArray());
    }

    default double sumOfLogs() {
        return sumOfLogs.agg(this.asDoubleArray());
    }

    default double sumOfSquares() {
        return sumOfSquares.agg(this.asDoubleArray());
    }

    default double geometricMean() {
        return geometricMean.agg(this.asDoubleArray());
    }

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    default double quadraticMean() {
        return quadraticMean.agg(this.asDoubleArray());
    }

    default double kurtosis() {
        return kurtosis.agg(this.asDoubleArray());
    }

    default double skewness() {
        return skewness.agg(this.asDoubleArray());
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    default int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (NumberColumn.isMissing(get(i))) {
                count++;
            }
        }
        return count;
    }

    double get(int i);

    double[] asDoubleArray();
}

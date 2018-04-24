package tech.tablesaw.aggregate;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;

public class AggregateFunctions {

    public static BooleanAggregateFunction countTrue = new BooleanAggregateFunction("Number True") {
        @Override
        public double summarize(Column column) {
            return ((BooleanColumn) column).countTrue();
        }
    };

    public static BooleanAggregateFunction countFalse = new BooleanAggregateFunction("Number False") {
        @Override
        public double summarize(Column column) {
            return ((BooleanColumn) column).countFalse();
        }
    };

    public static BooleanAggregateFunction proportionTrue = new BooleanAggregateFunction("Proportion True") {
        @Override
        public double summarize(Column column) {
            return ((BooleanColumn) column).proportionTrue();
        }
    };

    public static BooleanAggregateFunction proportionFalse = new BooleanAggregateFunction("Proportion False") {
        @Override
        public double summarize(Column column) {
            return ((BooleanColumn) column).proportionFalse();
        }
    };

    /**
     * A function that returns the first item
     */
    public static NumericAggregateFunction first = new NumericAggregateFunction("First") {

        @Override
        public double summarize(Column column) {
            return column.isEmpty() ? Float.NaN : column.getDouble(0);
        }
    };

    /**
     * A function that returns the last item
     */
    public static NumericAggregateFunction last = new NumericAggregateFunction("Last") {

        @Override
        public double summarize(Column column) {
            return column.isEmpty() ? Float.NaN : column.getDouble(column.size() - 1);
        }
    };

    /**
     * A function that calculates the count of values in the column excluding missing values
     */
    public static AggregateFunction countNonMissing = new AggregateFunction("Count") {

        @Override
        public double summarize(Column column) {
            return column.size() - column.countMissing();
        }

        @Override
        public boolean isCompatibleWith(ColumnType type) {
            return true;
        }
    };

    /**
     * A function that calculates the count of values in the column excluding missing values. A synonym for countNonMissing
     */
    public static AggregateFunction count = countNonMissing;

    /**
     * A function that calculates the count of values in the column excluding missing values
     */
    public static AggregateFunction countMissing = new AggregateFunction("Missing Values") {

        @Override
        public double summarize(Column column) {
            return column.countMissing();
        }

        @Override
        public boolean isCompatibleWith(ColumnType type) {
            return true;
        }
    };

    /**
     * A function that returns the number of non-missing unique values in the column param
     */
    public static AggregateFunction countUnique = new AggregateFunction("Count Unique") {

        @Override
        public double summarize(Column doubles) {
            return removeMissing(doubles).unique().size();
        }

        @Override
        public boolean isCompatibleWith(ColumnType type) {
            return true;
        }
    };

    /**
     * A function that calculates the mean of the values in the column param
     */
    public static final NumericAggregateFunction mean = new NumericAggregateFunction("Mean") {

        @Override
        public double summarize(Column column) {
            return StatUtils.mean(removeMissing((NumberColumn) column));
        }
    };

    /**
     * A function that calculates the sum of the values in the column param
     */
    public static final NumericAggregateFunction sum = new NumericAggregateFunction("Sum") {

        @Override
        public double summarize(Column column) {
            return StatUtils.sum(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericAggregateFunction median = new NumericAggregateFunction("Median") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 50.0);
        }
    };

    public static final AggregateFunction countWithMissing = new AggregateFunction("Count (incl. missing)") {
        @Override
        public double summarize(Column column) {
            return column.size();
        }

        @Override
        public boolean isCompatibleWith(ColumnType type) {
            return false;
        }
    };

    public static final NumericAggregateFunction quartile1 = new NumericAggregateFunction("First Quartile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 25.0);
        }
    };

    public static final NumericAggregateFunction quartile3 = new NumericAggregateFunction("Third Quartile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 75.0);
        }
    };

    public static final NumericAggregateFunction percentile90 = new NumericAggregateFunction("90th Percentile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 90.0);
        }
    };

    public static final NumericAggregateFunction percentile95 = new NumericAggregateFunction("95th Percentile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 95.0);
        }
    };

    public static final NumericAggregateFunction percentile99 = new NumericAggregateFunction("99th Percentile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 99.0);
        }
    };

    public static final NumericAggregateFunction range = new NumericAggregateFunction("Range") {

        @Override
        public double summarize(Column column) {
            double[] data = removeMissing((NumberColumn) column);
            return StatUtils.max(data) - StatUtils.min(data);
        }
    };

    public static final NumericAggregateFunction min = new NumericAggregateFunction("Min") {

        @Override
        public double summarize(Column column) {
            return StatUtils.min(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericAggregateFunction max = new NumericAggregateFunction("Max") {

        @Override
        public double summarize(Column column) {
            return StatUtils.max(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericAggregateFunction product = new NumericAggregateFunction("Product") {

        @Override
        public double summarize(Column column) {
            return StatUtils.product(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericAggregateFunction geometricMean = new NumericAggregateFunction("Geometric Mean") {

        @Override
        public double summarize(Column column) {
            return StatUtils.geometricMean(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericAggregateFunction populationVariance = new NumericAggregateFunction("Population Variance") {

        @Override
        public double summarize(Column column) {
            return StatUtils.populationVariance(removeMissing((NumberColumn) column));
        }
    };

    /**
     * Returns the quadratic mean, aka, the root-mean-square
     */
    public static final NumericAggregateFunction quadraticMean = new NumericAggregateFunction("Quadratic Mean") {

        @Override
        public double summarize(Column column) {
            return new DescriptiveStatistics(removeMissing((NumberColumn) column)).getQuadraticMean();
        }
    };

    public static final NumericAggregateFunction kurtosis = new NumericAggregateFunction("Kurtosis") {

        @Override
        public double summarize(Column column) {
            double[] data = removeMissing((NumberColumn) column);
            return new Kurtosis().evaluate(data, 0, data.length);
        }
    };

    public static final NumericAggregateFunction skewness = new NumericAggregateFunction("Skewness") {

        @Override
        public double summarize(Column column) {
            double[] data = removeMissing((NumberColumn) column);
            return new Skewness().evaluate(data, 0, data.length);
        }
    };

    public static final NumericAggregateFunction sumOfSquares = new NumericAggregateFunction("Sum of Squares") {

        @Override
        public String functionName() {
            return "Sum of Squares";
        }

        @Override
        public double summarize(Column column) {
            return StatUtils.sumSq(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericAggregateFunction sumOfLogs = new NumericAggregateFunction("Sum of Logs") {

        @Override
        public double summarize(Column column) {
            return StatUtils.sumLog(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericAggregateFunction variance = new NumericAggregateFunction("Variance") {

        @Override
        public double summarize(Column column) {
            double[] values = removeMissing((NumberColumn) column);
            return StatUtils.variance(values);
        }
    };

    public static final NumericAggregateFunction stdDev = new NumericAggregateFunction("Std. Deviation") {

        @Override
        public double summarize(Column column) {
            return Math.sqrt(StatUtils.variance(removeMissing((NumberColumn) column)));
        }
    };

    public static double percentile(NumberColumn data, double percentile) {
        return StatUtils.percentile(removeMissing(data), percentile);
    }

    public static final NumericAggregateFunction standardDeviation = stdDev;

    private static double[] removeMissing(NumberColumn column) {
        return column.removeMissing().asDoubleArray();
    }

    private static Column removeMissing(Column column) {
        return column.removeMissing();
    }

    // TODO(lwhite): These are two column reductions. We need a class for that
    public static double meanDifference(NumberColumn column1, NumberColumn column2) {
        return StatUtils.meanDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }

    public static double sumDifference(NumberColumn column1, NumberColumn column2) {
        return StatUtils.sumDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }
}

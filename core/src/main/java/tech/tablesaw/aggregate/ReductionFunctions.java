package tech.tablesaw.aggregate;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;

public class ReductionFunctions {

    public static BooleanReduction countTrue = new BooleanReduction("Number True") {
        @Override
        public double summarize(Column column) {
            return ((BooleanColumn) column).countTrue();
        }
    };

    public static BooleanReduction countFalse = new BooleanReduction("Number False") {
        @Override
        public double summarize(Column column) {
            return ((BooleanColumn) column).countFalse();
        }
    };

    public static final NumericReduction standardDeviation = new NumericReduction("Std. Deviation") {
        @Override
        public double summarize(Column column) {
            return Math.sqrt(StatUtils.variance(column.asDoubleArray()));
        }
    };

    /**
     * A function that returns the first item
     */
    public static NumericReduction first = new NumericReduction("First") {

        @Override
        public double summarize(Column column) {
            return column.isEmpty() ? Float.NaN : column.getDouble(0);
        }
    };

    /**
     * A function that returns the last item
     */
    public static NumericReduction last = new NumericReduction("Last") {

        @Override
        public double summarize(Column column) {
            return column.isEmpty() ? Float.NaN : column.getDouble(column.size() - 1);
        }
    };

    /**
     * A function that calculates the count of values in the column excluding missing values
     */
    public static Reduction countNonMissing = new Reduction("Count") {

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
     * A function that calculates the count of values in the column excluding missing values
     */
    public static Reduction countMissing = new Reduction("Count Missing Values") {

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
     * A function that calculates the count of the values in the column param
     */
    public static Reduction countUnique = new Reduction("Count") {

        @Override
        public double summarize(Column doubles) {
            return doubles.unique().size();
        }

        @Override
        public boolean isCompatibleWith(ColumnType type) {
            return true;
        }
    };

    /**
     * A function that calculates the mean of the values in the column param
     */
    public static final NumericReduction mean = new NumericReduction("Mean") {

        @Override
        public double summarize(Column column) {
            return StatUtils.mean(removeMissing((NumberColumn) column));
        }
    };

    /**
     * A function that calculates the sum of the values in the column param
     */
    public static final NumericReduction sum = new NumericReduction("Sum") {

        @Override
        public double summarize(Column column) {
            return StatUtils.sum(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction median = new NumericReduction("Median") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 50.0);
        }
    };

    public static final Reduction countWithMissing = new Reduction("Count including missing") {
        @Override
        public double summarize(Column column) {
            return column.size();
        }

        @Override
        public boolean isCompatibleWith(ColumnType type) {
            return false;
        }
    };

    public static final NumericReduction quartile1 = new NumericReduction("First Quartile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 25.0);
        }
    };

    public static final NumericReduction quartile3 = new NumericReduction("Third Quartile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 75.0);
        }
    };

    public static final NumericReduction percentile90 = new NumericReduction("90th Percentile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 90.0);
        }
    };

    public static final NumericReduction percentile95 = new NumericReduction("95th Percentile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 95.0);
        }
    };

    public static final NumericReduction percentile99 = new NumericReduction("99th Percentile") {

        @Override
        public double summarize(Column column) {
            return percentile((NumberColumn) column, 99.0);
        }
    };

    public static final NumericReduction range = new NumericReduction("Range") {

        @Override
        public double summarize(Column column) {
            double[] data = removeMissing((NumberColumn) column);
            return StatUtils.max(data) - StatUtils.min(data);
        }
    };

    public static final NumericReduction min = new NumericReduction("Min") {

        @Override
        public double summarize(Column column) {
            return StatUtils.min(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction max = new NumericReduction("Max") {

        @Override
        public double summarize(Column column) {
            return StatUtils.max(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction product = new NumericReduction("Product") {

        @Override
        public double summarize(Column column) {
            return StatUtils.product(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction geometricMean = new NumericReduction("Geometric Mean") {

        @Override
        public double summarize(Column column) {
            return StatUtils.geometricMean(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction populationVariance = new NumericReduction("Population Variance") {

        @Override
        public double summarize(Column column) {
            return StatUtils.populationVariance(removeMissing((NumberColumn) column));
        }
    };

    /**
     * Returns the quadratic mean, aka, the root-mean-square
     */
    public static final NumericReduction quadraticMean = new NumericReduction("Quadratic Mean") {

        @Override
        public double summarize(Column column) {
            return new DescriptiveStatistics(removeMissing((NumberColumn) column)).getQuadraticMean();
        }
    };

    public static final NumericReduction kurtosis = new NumericReduction("Kurtosis") {

        @Override
        public double summarize(Column column) {
            double[] data = removeMissing((NumberColumn) column);
            return new Kurtosis().evaluate(data, 0, data.length);
        }
    };

    public static final NumericReduction skewness = new NumericReduction("Skewness") {

        @Override
        public double summarize(Column column) {
            double[] data = removeMissing((NumberColumn) column);
            return new Skewness().evaluate(data, 0, data.length);
        }
    };

    public static final NumericReduction sumOfSquares = new NumericReduction("Sum of Squares") {

        @Override
        public String functionName() {
            return "Sum of Squares";
        }

        @Override
        public double summarize(Column column) {
            return StatUtils.sumSq(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction sumOfLogs = new NumericReduction("Sum of Logs") {

        @Override
        public double summarize(Column column) {
            return StatUtils.sumLog(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction variance = new NumericReduction("Variance") {

        @Override
        public double summarize(Column column) {
            return StatUtils.variance(removeMissing((NumberColumn) column));
        }
    };

    public static final NumericReduction stdDev = new NumericReduction("Std. Deviation") {

        @Override
        public double summarize(Column column) {
            return Math.sqrt(StatUtils.variance(removeMissing((NumberColumn) column)));
        }
    };

    public static double percentile(double[] data, double percentile) {
        return StatUtils.percentile(removeMissing(data), percentile);
    }

    public static double percentile(NumberColumn data, double percentile) {
        return StatUtils.percentile(removeMissing(data), percentile);
    }

    private static double[] removeMissing(double[] data) {
        DoubleList doubleArray = new DoubleArrayList();
        for (double d : data) {
            if (isNotMissing(d)) {
                doubleArray.add(d);
            }
        }
        return doubleArray.toDoubleArray();
    }

    private static double[] removeMissing(NumberColumn column) {
        return removeMissing(column.asDoubleArray());
    }

    private static boolean isNotMissing(double value) {
        return !Double.isNaN(value);
    }

    // TODO(lwhite): These are two column reductions. We need a class for that
    public static double meanDifference(NumberColumn column1, NumberColumn column2) {
        return StatUtils.meanDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }

    public static double sumDifference(NumberColumn column1, NumberColumn column2) {
        return StatUtils.sumDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }
}

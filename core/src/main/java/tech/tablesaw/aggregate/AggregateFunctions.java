package tech.tablesaw.aggregate;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AggregateFunctions {


    public static DateTimeAggregateFunction earliestDateTime = new DateTimeAggregateFunction("Earliest Date") {

        @Override
        public LocalDateTime summarize(DateTimeColumn column) {
            return column.min();
        }
    };

    public static DateAggregateFunction earliestDate = new DateAggregateFunction("Earliest Date") {

        @Override
        public LocalDate summarize(DateColumn column) {
            return column.min();
        }
    };

    public static DateTimeAggregateFunction latest = new DateTimeAggregateFunction("Latest Date") {

        @Override
        public LocalDateTime summarize(DateTimeColumn column) {
            return column.max();
        }
    };

    public static BooleanCountFunction countTrue = new BooleanCountFunction("Number True") {

        @Override
        public Integer summarize(BooleanColumn column) {
            return column.countTrue();
        }
    };

    public static BooleanCountFunction countFalse = new BooleanCountFunction("Number False") {
        @Override
        public Integer summarize(BooleanColumn column) {
            return (column).countFalse();
        }
    };

    public static BooleanNumericFunction proportionTrue = new BooleanNumericFunction("Proportion True") {
        @Override
        public Double summarize(BooleanColumn column) {
            return (column).proportionTrue();
        }
    };

    public static BooleanNumericFunction proportionFalse = new BooleanNumericFunction("Proportion False") {
        @Override
        public Double summarize(BooleanColumn column) {
            return (column).proportionFalse();
        }
    };

    /**
     * A function that returns the first item
     */
    public static NumericAggregateFunction first = new NumericAggregateFunction("First") {

        @Override
        public Double summarize(NumberColumn column) {
            return column.isEmpty() ? NumberColumn.MISSING_VALUE : column.getDouble(0);
        }
    };

    /**
     * A function that returns the last item
     */
    public static NumericAggregateFunction last = new NumericAggregateFunction("Last") {

        @Override
        public Double summarize(NumberColumn column) {
            return column.isEmpty() ? NumberColumn.MISSING_VALUE : column.getDouble(column.size() - 1);
        }
    };

    /**
     * A function that returns the difference between the last and first items
     */
    public static NumericAggregateFunction change = new NumericAggregateFunction("Change") {

        @Override
        public Double summarize(NumberColumn column) {
            return column.size() < 2 ? NumberColumn.MISSING_VALUE : column.getDouble(column.size() - 1) - column.getDouble(0);
        }
    };

    /**
     * A function that returns the difference between the last and first items
     */
    public static NumericAggregateFunction pctChange = new NumericAggregateFunction("Percent Change") {

        @Override
        public Double summarize(NumberColumn column) {
            return column.size() < 2 ? NumberColumn.MISSING_VALUE : (column.getDouble(column.size() - 1) - column.getDouble(0)) / column.getDouble(0);
        }
    };    
 
    /**
     * A function that calculates the count of values in the column excluding missing values
     */
    public static CountFunction countNonMissing = new CountFunction("Count") {

        @Override
        public Integer summarize(Column<?> column) {
            return column.size() - column.countMissing();
        }
    };

    /**
     * A function that calculates the count of values in the column excluding missing values. A synonym for countNonMissing
     */
    public static final CountFunction count = countNonMissing;

    /**
     * A function that calculates the count of values in the column excluding missing values
     */
    public static CountFunction countMissing = new CountFunction("Missing Values") {

        @Override
        public Integer summarize(Column<?> column) {
            return column.countMissing();
        }
    };

    /**
     * A function that returns the number of non-missing unique values in the column param
     */
    public static CountFunction countUnique = new CountFunction("Count Unique") {

        @Override
        public Integer summarize(Column<?> doubles) {
            return removeMissing(doubles.unique()).size();
        }
    };

    /**
     * A function that calculates the mean of the values in the column param
     */
    public static final NumericAggregateFunction mean = new NumericAggregateFunction("Mean") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.mean(removeMissing(column));
        }
    };

    /**
     * A function that calculates the sum of the values in the column param
     */
    public static final NumericAggregateFunction sum = new NumericAggregateFunction("Sum") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.sum(removeMissing(column));
        }
    };

    public static final NumericAggregateFunction median = new NumericAggregateFunction("Median") {

        @Override
        public Double summarize(NumberColumn column) {
            return percentile(column, 50.0);
        }
    };

    public static final CountFunction countWithMissing = new CountFunction("Count (incl. missing)") {

        @Override
        public Integer summarize(Column<?> column) {
            return column.size();
        }
    };

    public static final NumericAggregateFunction quartile1 = new NumericAggregateFunction("First Quartile") {

        @Override
        public Double summarize(NumberColumn column) {
            return percentile(column, 25.0);
        }
    };

    public static final NumericAggregateFunction quartile3 = new NumericAggregateFunction("Third Quartile") {

        @Override
        public Double summarize(NumberColumn column) {
            return percentile(column, 75.0);
        }
    };

    public static final NumericAggregateFunction percentile90 = new NumericAggregateFunction("90th Percentile") {

        @Override
        public Double summarize(NumberColumn column) {
            return percentile(column, 90.0);
        }
    };

    public static final NumericAggregateFunction percentile95 = new NumericAggregateFunction("95th Percentile") {

        @Override
        public Double summarize(NumberColumn column) {
            return percentile(column, 95.0);
        }
    };

    public static final NumericAggregateFunction percentile99 = new NumericAggregateFunction("99th Percentile") {

        @Override
        public Double summarize(NumberColumn column) {
            return percentile(column, 99.0);
        }
    };

    public static final NumericAggregateFunction range = new NumericAggregateFunction("Range") {

        @Override
        public Double summarize(NumberColumn column) {
            double[] data = removeMissing(column);
            return StatUtils.max(data) - StatUtils.min(data);
        }
    };

    public static final NumericAggregateFunction min = new NumericAggregateFunction("Min") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.min(removeMissing(column));
        }
    };

    public static final NumericAggregateFunction max = new NumericAggregateFunction("Max") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.max(removeMissing(column));
        }
    };

    public static final NumericAggregateFunction product = new NumericAggregateFunction("Product") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.product(removeMissing(column));
        }
    };

    public static final NumericAggregateFunction geometricMean = new NumericAggregateFunction("Geometric Mean") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.geometricMean(removeMissing(column));
        }
    };

    public static final NumericAggregateFunction populationVariance = new NumericAggregateFunction("Population Variance") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.populationVariance(removeMissing(column));
        }
    };

    /**
     * Returns the quadratic mean, aka, the root-mean-square
     */
    public static final NumericAggregateFunction quadraticMean = new NumericAggregateFunction("Quadratic Mean") {

        @Override
        public Double summarize(NumberColumn column) {
            return new DescriptiveStatistics(removeMissing(column)).getQuadraticMean();
        }
    };

    public static final NumericAggregateFunction kurtosis = new NumericAggregateFunction("Kurtosis") {

        @Override
        public Double summarize(NumberColumn column) {
            double[] data = removeMissing(column);
            return new Kurtosis().evaluate(data, 0, data.length);
        }
    };

    public static final NumericAggregateFunction skewness = new NumericAggregateFunction("Skewness") {

        @Override
        public Double summarize(NumberColumn column) {
            double[] data = removeMissing(column);
            return new Skewness().evaluate(data, 0, data.length);
        }
    };

    public static final NumericAggregateFunction sumOfSquares = new NumericAggregateFunction("Sum of Squares") {

        @Override
        public String functionName() {
            return "Sum of Squares";
        }

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.sumSq(removeMissing(column));
        }
    };

    public static final NumericAggregateFunction sumOfLogs = new NumericAggregateFunction("Sum of Logs") {

        @Override
        public Double summarize(NumberColumn column) {
            return StatUtils.sumLog(removeMissing(column));
        }
    };

    public static final NumericAggregateFunction variance = new NumericAggregateFunction("Variance") {

        @Override
        public Double summarize(NumberColumn column) {
            double[] values = removeMissing(column);
            return StatUtils.variance(values);
        }
    };

    public static final NumericAggregateFunction stdDev = new NumericAggregateFunction("Std. Deviation") {

        @Override
        public Double summarize(NumberColumn column) {
            return Math.sqrt(StatUtils.variance(removeMissing(column)));
        }
    };

    public static Double percentile(NumberColumn data, Double percentile) {
        return StatUtils.percentile(removeMissing(data), percentile);
    }

    public static final NumericAggregateFunction standardDeviation = stdDev;

    private static double[] removeMissing(NumberColumn column) {
        return column.removeMissing().asDoubleArray();
    }

    private static <T> Column<T> removeMissing(Column<T> column) {
        return column.removeMissing();
    }

    // TODO(lwhite): These are two column reductions. We need a class for that
    public static Double meanDifference(NumberColumn column1, NumberColumn column2) {
        return StatUtils.meanDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }

    public static Double sumDifference(NumberColumn column1, NumberColumn column2) {
        return StatUtils.sumDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }
}

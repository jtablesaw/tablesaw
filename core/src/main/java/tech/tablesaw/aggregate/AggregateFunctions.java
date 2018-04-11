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

package tech.tablesaw.aggregate;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import tech.tablesaw.api.NumberColumn;

/**
 * The default set of aggregate functions
 */
public class AggregateFunctions {

    /**
     * A function that returns the first item
     */
    public static AggregateFunction first = new Aggregation("First") {

        @Override
        public double summarize(NumberColumn column) {
            return column.isEmpty() ? Float.NaN : column.getDouble(0);
        }
    };

    /**
     * A function that returns the last item
     */
    public static AggregateFunction last = new Aggregation("Last") {

        @Override
        public double summarize(NumberColumn column) {
            return column.isEmpty() ? Float.NaN : column.getDouble(column.size() - 1);
        }
    };

    /**
     * A function that calculates the count of the values in the column param
     */
    public static AggregateFunction count = new Aggregation("Count") {

        @Override
        public double summarize(NumberColumn column) {
            return column.size();
        }
    };

    /**
     * A function that calculates the count of the values in the column param
     */
    public static AggregateFunction countUnique = new Aggregation("Count") {

        @Override
        public double summarize(NumberColumn doubles) {
            return doubles.unique().size();
        }
    };

    /**
     * A function that calculates the mean of the values in the column param
     */
    public static final AggregateFunction mean = new Aggregation("Mean") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.mean(removeMissing(column));
        }
    };

    /**
     * A function that calculates the sum of the values in the column param
     */
    public static final AggregateFunction sum = new Aggregation("Sum") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.sum(removeMissing(column));
        }
    };

    public static final AggregateFunction median = new Aggregation("Median") {

        @Override
        public double summarize(NumberColumn column) {
            return percentile(column, 50.0);
        }
    };

    public static final AggregateFunction n = new Aggregation("N") {
        //TODO: This is the same as count -> Get rid of one of them
        //TODO: Consider whether we should provide a count without missing values
        @Override
        public double summarize(NumberColumn column) {
            return column.size();
        }
    };

    public static final AggregateFunction quartile1 = new Aggregation("First Quartile") {

        @Override
        public double summarize(NumberColumn column) {
            return percentile(column, 25.0);
        }
    };

    public static final AggregateFunction quartile3 = new Aggregation("Third Quartile") {

        @Override
        public double summarize(NumberColumn column) {
            return percentile(column, 75.0);
        }
    };

    public static final AggregateFunction percentile90 = new Aggregation("90th Percentile") {

        @Override
        public double summarize(NumberColumn column) {
            return percentile(column, 90.0);
        }
    };

    public static final AggregateFunction percentile95 = new Aggregation("95th Percentile") {

        @Override
        public double summarize(NumberColumn column) {
            return percentile(column, 95.0);
        }
    };

    public static final AggregateFunction percentile99 = new Aggregation("99th Percentile") {

        @Override
        public double summarize(NumberColumn column) {
            return percentile(column, 99.0);
        }
    };

    public static final AggregateFunction range = new Aggregation("Range") {

        @Override
        public double summarize(NumberColumn column) {
            double[] data = removeMissing(column);
            return StatUtils.max(data) - StatUtils.min(data);
        }
    };

    public static final AggregateFunction min = new Aggregation("Min") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.min(removeMissing(column));
        }
    };

    public static final AggregateFunction max = new Aggregation("Max") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.max(removeMissing(column));
        }
    };

    public static final AggregateFunction product = new Aggregation("Product") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.product(removeMissing(column));
        }
    };

    public static final AggregateFunction geometricMean = new Aggregation("Geometric Mean") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.geometricMean(removeMissing(column));
        }
    };

    public static final AggregateFunction populationVariance = new Aggregation("Population Variance") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.populationVariance(removeMissing(column));
        }
    };

    /**
     * Returns the quadratic mean, aka, the root-mean-square
     */
    public static final AggregateFunction quadraticMean = new Aggregation("Quadratic Mean") {

        @Override
        public double summarize(NumberColumn column) {
            return new DescriptiveStatistics(removeMissing(column)).getQuadraticMean();
        }
    };

    public static final AggregateFunction kurtosis = new Aggregation("Kurtosis") {

        @Override
        public double summarize(NumberColumn column) {
            double[] data = removeMissing(column);
            return new Kurtosis().evaluate(data, 0, data.length);
        }
    };

    public static final AggregateFunction skewness = new Aggregation("Skewness") {

        @Override
        public double summarize(NumberColumn column) {
            double[] data = removeMissing(column);
            return new Skewness().evaluate(data, 0, data.length);
        }
    };

    public static final AggregateFunction sumOfSquares = new Aggregation("Sum of Squares") {

        @Override
        public String functionName() {
            return "Sum of Squares";
        }

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.sumSq(removeMissing(column));
        }
    };

    public static final AggregateFunction sumOfLogs = new Aggregation("Sum of Logs") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.sumLog(removeMissing(column));
        }
    };

    public static final AggregateFunction variance = new Aggregation("Variance") {

        @Override
        public double summarize(NumberColumn column) {
            return StatUtils.variance(removeMissing(column));
        }
    };

    public static final AggregateFunction stdDev = new Aggregation("Std. Deviation") {

        @Override
        public double summarize(NumberColumn column) {
            return Math.sqrt(StatUtils.variance(removeMissing(column)));
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

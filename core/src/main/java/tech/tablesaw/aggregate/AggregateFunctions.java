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
    public static AggregateFunction first = new Reduction("First") {

        @Override
        public double agg(double[] data) {
            return data.length == 0 ? Float.NaN : data[0];
        }
    };

    /**
     * A function that returns the last item
     */
    public static AggregateFunction last = new Reduction("Last") {

        @Override
        public double agg(double[] data) {
            return data.length == 0 ? Float.NaN : data[data.length - 1];
        }
    };

    /**
     * A function that calculates the count of the values in the column param
     */
    public static AggregateFunction count = new Reduction("Count") {

        @Override
        public double agg(double[] data) {
            return data.length;
        }
    };

    /**
     * A function that calculates the mean of the values in the column param
     */
    public static final AggregateFunction mean = new Reduction("Mean") {

        @Override
        public double agg(double[] data) {
            return StatUtils.mean(removeMissing(data));
        }
    };

    /**
     * A function that calculates the sum of the values in the column param
     */
    public static final AggregateFunction sum = new Reduction("Sum") {

        @Override
        public double agg(double[] data) {
            return StatUtils.sum(removeMissing(data));
        }
    };

    public static final AggregateFunction median = new Reduction("Median") {

        @Override
        public double agg(double[] data) {
            return percentile(data, 50.0);
        }
    };

    public static final AggregateFunction n = new Reduction("N") {
        //TODO: This is the same as count -> Get rid of one of them
        //TODO: Consider whether we should provide a count without missing values
        @Override
        public double agg(double[] data) {
            return data.length;
        }
    };

    public static final AggregateFunction quartile1 = new Reduction("First Quartile") {

        @Override
        public double agg(double[] data) {
            return percentile(data, 25.0);
        }
    };

    public static final AggregateFunction quartile3 = new Reduction("Third Quartile") {

        @Override
        public double agg(double[] data) {
            return percentile(data, 75.0);
        }
    };

    public static final AggregateFunction percentile90 = new Reduction("90th Percentile") {

        @Override
        public double agg(double[] data) {
            return percentile(data, 90.0);
        }
    };

    public static final AggregateFunction percentile95 = new Reduction("95th Percentile") {

        @Override
        public double agg(double[] data) {
            return percentile(data, 95.0);
        }
    };

    public static final AggregateFunction percentile99 = new Reduction("99th Percentile") {

        @Override
        public double agg(double[] data) {
            return percentile(data, 99.0);
        }
    };

    public static final AggregateFunction range = new Reduction("Range") {

        @Override
        public double agg(double[] data) {
            data = removeMissing(data);
            return StatUtils.max(data) - StatUtils.min(data);
        }
    };

    public static final AggregateFunction min = new Reduction("Min") {

        @Override
        public double agg(double[] data) {
            return StatUtils.min(removeMissing(data));
        }
    };

    public static final AggregateFunction max = new Reduction("Max") {

        @Override
        public double agg(double[] data) {
            return StatUtils.max(removeMissing(data));
        }
    };

    public static final AggregateFunction product = new Reduction("Product") {

        @Override
        public double agg(double[] data) {
            return StatUtils.product(removeMissing(data));
        }
    };

    public static final AggregateFunction geometricMean = new Reduction("Geometric Mean") {

        @Override
        public double agg(double[] data) {
            return StatUtils.geometricMean(removeMissing(data));
        }
    };

    public static final AggregateFunction populationVariance = new Reduction("Population Variance") {

        @Override
        public double agg(double[] data) {
            return StatUtils.populationVariance(removeMissing(data));
        }
    };

    /**
     * Returns the quadratic mean, aka, the root-mean-square
     */
    public static final AggregateFunction quadraticMean = new Reduction("Quadratic Mean") {

        @Override
        public double agg(double[] data) {
            return new DescriptiveStatistics(removeMissing(data)).getQuadraticMean();
        }
    };

    public static final AggregateFunction kurtosis = new Reduction("Kurtosis") {

        @Override
        public double agg(double[] data) {
            return new Kurtosis().evaluate(removeMissing(data), 0, data.length);
        }
    };

    public static final AggregateFunction skewness = new Reduction("Skewness") {

        @Override
        public double agg(double[] data) {
            return new Skewness().evaluate(removeMissing(data), 0, data.length);
        }
    };

    public static final AggregateFunction sumOfSquares = new Reduction("Sum of Squares") {

        @Override
        public String functionName() {
            return "Sum of Squares";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.sumSq(removeMissing(data));
        }
    };

    public static final AggregateFunction sumOfLogs = new Reduction("Sum of Logs") {

        @Override
        public double agg(double[] data) {
            return StatUtils.sumLog(removeMissing(data));
        }
    };

    public static final AggregateFunction variance = new Reduction("Variance") {

        @Override
        public double agg(double[] data) {
            return StatUtils.variance(removeMissing(data));
        }
    };

    public static final AggregateFunction stdDev = new Reduction("Std. Deviation") {

        @Override
        public double agg(double[] data) {
            return Math.sqrt(StatUtils.variance(removeMissing(data)));
        }

    };

    public static double percentile(double[] data, double percentile) {
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

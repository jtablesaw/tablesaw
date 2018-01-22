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

import org.apache.commons.math3.util.DoubleArray;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;

/**
 * Contains common utilities for double and long types
 */
public class AggregateFunctions {

    // TODO(lwhite): Re-implement these methods to work natively with float[], instead of converting to double[]

    /**
     * A function that returns the first item
     */
    public static AggregateFunction first = new AggregateFunction() {

        @Override
        public String functionName() {
            return "First";
        }

        @Override
        public double agg(double[] data) {
            return data.length == 0 ? Float.NaN : data[0];
        }
    };  

    /**
     * A function that returns the last item
     */
    public static AggregateFunction last = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Last";
        }

        @Override
        public double agg(double[] data) {
            return data.length == 0 ? Float.NaN : data[data.length-1];
        }
    };

    /**
     * A function that calculates the count of the values in the column param
     */
    public static AggregateFunction count = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Count";
        }

        @Override
        public double agg(double[] data) {
            return data.length;
        }
    };  

    /**
     * A function that calculates the mean of the values in the column param
     */
    public static AggregateFunction mean = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Mean";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.mean(removeMissing(data));
        }
    };

    /**
     * A function that calculates the sum of the values in the column param
     */
    public static AggregateFunction sum = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Sum";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.sum(removeMissing(data));
        }

        @Override
        public double agg(FloatColumn floatColumn) {
            float sum;
            sum = 0.0f;
            for (float value : floatColumn) {
                if (value != Float.NaN) {
                    sum += value;
                }
            }
            return sum;
        }

        @Override
        public double agg(DoubleColumn floatColumn) {
            float sum;
            sum = 0.0f;
            for (double value : floatColumn) {
                if (value != Float.NaN) {
                    sum += value;
                }
            }
            return sum;
        }
    };

    public static AggregateFunction median = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Median";
        }

        @Override
        public double agg(double[] data) {
            return percentile(data, 50.0);
        }
    };

    public static AggregateFunction n = new AggregateFunction() {

        @Override
        public String functionName() {
            return "N";
        }

        //TODO: Consider whether we should provide a count without missing values
        @Override
        public double agg(double[] data) {
            return data.length;
        }
    };

    public static AggregateFunction quartile1 = new AggregateFunction() {

        @Override
        public String functionName() {
            return "First Quartile";
        }

        @Override
        public double agg(double[] data) {
            return percentile(data, 25.0);
        }
    };

    public static AggregateFunction quartile3 = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Third Quartile";
        }

        @Override
        public double agg(double[] data) {
            return percentile(data, 75.0);
        }
    };

    public static AggregateFunction percentile90 = new AggregateFunction() {

        @Override
        public String functionName() {
            return "90th Percentile";
        }

        @Override
        public double agg(double[] data) {
            return percentile(data, 90.0);
        }
    };

    public static AggregateFunction percentile95 = new AggregateFunction() {

        @Override
        public String functionName() {
            return "95th Percentile";
        }

        @Override
        public double agg(double[] data) {
            return percentile(data, 95.0);
        }
    };

    public static AggregateFunction percentile99 = new AggregateFunction() {

        @Override
        public String functionName() {
            return "99th Percentile";
        }

        @Override
        public double agg(double[] data) {
            return percentile(data, 99.0);
        }
    };

    public static AggregateFunction range = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Range";
        }

        @Override
        public double agg(double[] data) {
            data = removeMissing(data);
            return StatUtils.max(data) - StatUtils.min(data);
        }
    };

    public static AggregateFunction min = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Min";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.min(removeMissing(data));
        }

        @Override
        public double agg(FloatColumn data) {
            if (data.size() == 0) {
                return Float.NaN;
            }
            float min = data.firstElement();
            for (float value : data) {
                if (!Float.isNaN(value)) {
                    min = (min < value) ? min : value;
                }
            }
            return min;
        }
    };

    public static AggregateFunction max = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Max";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.max(removeMissing(data));
        }
    };

    public static AggregateFunction product = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Product";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.product(removeMissing(data));
        }

        @Override
        public double agg(FloatColumn data) {
            float product = 1.0f;
            boolean empty = true;
            for (float value : data) {
                if (value != Float.NaN) {
                    empty = false;
                    product *= value;
                }
            }
            if (empty) {
                return Float.NaN;
            }
            return product;
        }
    };

    public static AggregateFunction geometricMean = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Geometric Mean";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.geometricMean(removeMissing(data));
        }
    };

    public static AggregateFunction populationVariance = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Population Variance";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.populationVariance(removeMissing(data));
        }
    };

    /**
     * Returns the quadratic mean, aka, the root-mean-square
     */
    public static AggregateFunction quadraticMean = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Quadratic Mean";
        }

        @Override
        public double agg(double[] data) {
            return new DescriptiveStatistics(removeMissing(data)).getQuadraticMean();
        }
    };

    public static AggregateFunction kurtosis = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Kurtosis";
        }

        @Override
        public double agg(double[] data) {
            return new Kurtosis().evaluate(removeMissing(data), 0, data.length);
        }
    };

    public static AggregateFunction skewness = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Skewness";
        }

        @Override
        public double agg(double[] data) {
            return new Skewness().evaluate(removeMissing(data), 0, data.length);
        }
    };

    public static AggregateFunction sumOfSquares = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Sum of Squares";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.sumSq(removeMissing(data));
        }
    };

    public static AggregateFunction sumOfLogs = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Sum of Logs";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.sumLog(removeMissing(data));
        }
    };

    public static AggregateFunction variance = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Variance";
        }

        @Override
        public double agg(double[] data) {
            return StatUtils.variance(removeMissing(data));
        }

        /**
         * Returns the (sample) variance of the available values.
         * <p>
         * <p>This method returns the bias-corrected sample variance (using {@code n - 1} in
         * the denominator).
         *
         * @return The variance, Double.NaN if no values have been added
         * or 0.0 for a single value set.
         */
        @Override
        public double agg(FloatColumn column) {
            double avg = mean.agg(column);
            double sumSquaredDiffs = 0.0f;
            for (float value : column) {
                double diff = value - avg;
                double sqrdDiff = diff * diff;
                sumSquaredDiffs += sqrdDiff;
            }
            return sumSquaredDiffs / (column.size() - 1);
        }
    };

    public static AggregateFunction stdDev = new AggregateFunction() {

        @Override
        public String functionName() {
            return "Std. Deviation";
        }

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
        return ! Double.isNaN(value);
    }

    // TODO(lwhite): These are two column reductions. We need a class for that
    public static double meanDifference(FloatColumn column1, FloatColumn column2) {
        return StatUtils.meanDifference(column1.toDoubleArray(), column2.toDoubleArray());
    }

    public static double sumDifference(FloatColumn column1, FloatColumn column2) {
        return StatUtils.sumDifference(column1.toDoubleArray(), column2.toDoubleArray());
    }
}

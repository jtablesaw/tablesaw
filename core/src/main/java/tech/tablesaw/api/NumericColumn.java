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

package tech.tablesaw.api;

import tech.tablesaw.columns.Column;

/**
 * Functionality common to all numeric column types
 */
public interface NumericColumn extends Column {

    double[] toDoubleArray();

    float getFloat(int index);

    double max();

    double min();

    double product();

    double mean();

    double median();

    double quartile1();

    double quartile3();

    double percentile(double percentile);

    double range();

    double variance();

    double populationVariance();

    double standardDeviation();

    double sumOfLogs();

    double sumOfSquares();

    double geometricMean();

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    double quadraticMean();

    double kurtosis();

    double skewness();
}

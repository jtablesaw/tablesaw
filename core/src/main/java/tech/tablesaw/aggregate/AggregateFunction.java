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

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;

/**
 * Functions that calculate values over the data of an entire column, such as sum, mean, std. dev, etc.
 * TODO: not sure that this should always return a double
 */
public interface AggregateFunction {

    String functionName();

    double agg(double[] data);

    default double agg(FloatColumn data) {
        return this.agg(data.toDoubleArray());
    }

    default double agg(DoubleColumn doubles) {
        return this.agg(doubles.toDoubleArray());
    }

    default double agg(IntColumn data) {
        return this.agg(data.toDoubleArray());
    }

    default double agg(ShortColumn data) {
        return this.agg(data.toDoubleArray());
    }

    default double agg(LongColumn data) {
        return this.agg(data.toDoubleArray());
    }
}

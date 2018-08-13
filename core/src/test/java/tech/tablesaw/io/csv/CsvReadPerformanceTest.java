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

package tech.tablesaw.io.csv;

import com.google.common.base.Stopwatch;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

import java.util.concurrent.TimeUnit;

import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.STRING;

public class CsvReadPerformanceTest {

    private static final ColumnType[] types = {
            STRING,
            STRING,
            STRING,
            STRING,
            STRING,
            STRING,
            STRING,
            STRING,
            DOUBLE,
            STRING,
            STRING,
            DOUBLE,
            STRING,
            STRING,
            STRING,
            DOUBLE,
            STRING,
            STRING,
            STRING,
            DOUBLE,
            STRING,
            STRING,
            STRING,
            STRING,
            STRING,
            STRING,
            STRING,
            DOUBLE,
            DOUBLE,
            DOUBLE,
            STRING,
            STRING
    };

    /**
     * Usage example using a Tornado data set
     */
    public static void main(String[] args) throws Exception {

        Stopwatch stopwatch = Stopwatch.createStarted();
        Table details = Table.read().csv("../data/SHR76_16.csv");
        stopwatch.stop();
        System.out.println("Large file (752,313 rows) read: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " us");
        System.out.println(details.shape());

        // TODO (white) printColumnTypes is broken or very slow
        //System.out.println(new CsvReader().printColumnTypes("../data/SHR76_16.csv", true, ',', Locale.getDefault()));

        stopwatch.reset();
        stopwatch.start();
        details = Table.read().csv(
                CsvReadOptions.builder("../data/SHR76_16.csv")
                        .columnTypes(types).build());
        stopwatch.stop();
        System.out.println(details.shape());
        System.out.println("Large file (752,313 rows) read: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " us");

    }
}

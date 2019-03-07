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

package tech.tablesaw.filters;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.index.LongIndex;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

/**
 * Tests manipulation of large (but not big) data sets
 */
public class SearchPerformanceTest {

    private static final int CONCEPT_COUNT = 10;

    // pools to get random test data from
    private static List<String> concepts = new ArrayList<>(CONCEPT_COUNT);
    private static LongArrayList dates = new LongArrayList(5_000_000);

    private static int numberOfRecordsInTable = 5_000_000;
    private static LongIndex dateIndex;

    public static void main(String[] args) throws Exception {

        Stopwatch stopwatch = Stopwatch.createStarted();

        Table t = defineSchema();

        generateTestData(t, numberOfRecordsInTable, stopwatch);
        t = t.sortAscendingOn("date");

        dateIndex = new LongIndex(t.dateTimeColumn("date"));
        t.setName("Observations");

        DateTimeColumn dates = t.dateTimeColumn("date");
        DoubleColumn lowValues = t.doubleColumn("lowValue");
        DoubleColumn highValues = t.doubleColumn("highValue");

        System.out.println(dates.summary());
        System.out.println(lowValues.summary());
        System.out.println(highValues.summary());

        LocalDateTime testDateTime = LocalDate.of(2010, 1, 1).atStartOfDay();
        double testLowValue = 500;
        double testHighValue = 999_500;

        stopwatch.reset();
        stopwatch.start();

        int count = 0;
        for (int i = 0; i < 1000; i++) {
            testDateTime = testDateTime.plusDays(2);
            int rowNumber = getRowNumber(t, testDateTime, testLowValue, testHighValue);
            if (rowNumber >= 0) {
                count++;
            }
        }

        stopwatch.stop();
        System.out.println("using rows with an index. found " + count + " in "  + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
    }

    private static int getRowNumber(Table t, LocalDateTime testDate, double testLow, double testHigh) {
        int rowNumber = -1;
        long testPackedDateTime = PackedLocalDateTime.pack(testDate);  // packing saves time
        Row row = new Row(t);
        row.at(dateIndex.get(testPackedDateTime).get(0));
        while (row.hasNext()) {
            row.next();
            if (row.getPackedDateTime("date") >= testPackedDateTime
                    && (row.getDouble("lowValue") <= testLow || row.getDouble("highValue") >= testHigh)) {
                rowNumber = row.getRowNumber();
                break;
            }
        }
        return rowNumber;
    }

    private static Table defineSchema() {
        Table t;
        t = Table.create("Observations");
        StringColumn conceptId = StringColumn.create("concept");
        DateTimeColumn date = DateTimeColumn.create("date");
        DoubleColumn lowValues = DoubleColumn.create("lowValue");
        DoubleColumn highValues = DoubleColumn.create("highValue");
        highValues.setPrintFormatter(NumberColumnFormatter.ints());
        lowValues.setPrintFormatter(NumberColumnFormatter.ints());

        t.addColumns(conceptId);
        t.addColumns(date);
        t.addColumns(lowValues);
        t.addColumns(highValues);
        return t;
    }

    private static void generateTestData(Table t, int numberOfRecordsInTable, Stopwatch stopwatch) throws IOException {
        stopwatch.reset().start();
        out.println("Generating test data");
        LocalDateTime startDateTime = LocalDateTime.of(2008, 1, 1, 0, 0, 0);
        generateData(numberOfRecordsInTable, startDateTime, t);
        out.println("Time to generate "
                + numberOfRecordsInTable + " records: "
                + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }

    private static void generateData(int observationCount, LocalDateTime dateTime, Table table) {
        // createFromCsv pools of random values

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(32, 127).build();

        while (concepts.size() <= CONCEPT_COUNT) {
            concepts.add(generator.generate(30));
        }

        while (dates.size() <= numberOfRecordsInTable) {
            dates.add(PackedLocalDateTime.pack(dateTime.plusMinutes(1)));
        }

        DateTimeColumn dateColumn = table.dateTimeColumn("date");
        StringColumn conceptColumn = table.stringColumn("concept");
        DoubleColumn lowValues = table.doubleColumn("lowValue");
        DoubleColumn highValues = table.doubleColumn("highValue");

        // sample from the pools to write the data
        for (int i = 0; i < observationCount; i++) {
            dateColumn.appendInternal(dates.getLong(i));
            conceptColumn.append(concepts.get(RandomUtils.nextInt(0, concepts.size())));
            lowValues.append(RandomUtils.nextDouble(0, 1_000_000));
            highValues.append(RandomUtils.nextDouble(0, 1_000_000));
        }
    }
}

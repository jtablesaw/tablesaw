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
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

/**
 * Tests manipulation of large (but not big) data sets
 */
public class SearchPerformanceTest {

    private static final int CONCEPT_COUNT = 10;
    private static final int PATIENT_COUNT = 10_000;

    // pools to get random test data from
    private static List<String> concepts = new ArrayList<>(CONCEPT_COUNT);
    private static IntArrayList patientIds = new IntArrayList(PATIENT_COUNT);
    private static int size = 60 * 365;
    private static LongArrayList dates = new LongArrayList(size);

    private static int startIndex = 0;
    private static int numberOfRecordsInTable = 5_000_000;

    public static void main(String[] args) throws Exception {
        
        Stopwatch stopwatch = Stopwatch.createStarted();

        Table t = defineSchema();
        generateTestData(t, numberOfRecordsInTable, stopwatch);
        t = t.sortAscendingOn("date");

        t.setName("Observations");

        DateTimeColumn dates = t.dateTimeColumn("date");
        NumberColumn values = t.numberColumn("value");
        NumberColumn patients = t.numberColumn("patient");

        System.out.println(t.structure());
        System.out.println(dates.summary());
        System.out.println(values.summary());
        System.out.println(patients.summary());

        LocalDateTime testDateTime = LocalDate.of(2010, 1, 1).atStartOfDay();
        double testValue = 90_000;
        double testPatient = 1_900_000_000;

        stopwatch.reset();
        stopwatch.start();

        for (int i = 0; i < 100; i++) {
            testDateTime = testDateTime.plusWeeks(1);
            int rowNumber = getRowNumber(t, testDateTime, testValue, testPatient);
            if (rowNumber >= 0) {
                System.out.println(t.rows(rowNumber));
            }
        }

        stopwatch.stop();
        System.out.println("using rows " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        System.out.println("Done");
    }

    private static int getRowNumber(Table t, LocalDateTime testDate, double testLow, double testHigh) {
        int rowNumber = -1;
        boolean updatedStartIndex = false;
        long testPackedDateTime = PackedLocalDateTime.pack(testDate);  // packing saves time
        Row row = new Row(t);
        row.at(startIndex);
        while (row.hasNext()) {
            row.next();
            if (row.getPackedDateTime("date").isOnOrAfter(testPackedDateTime)) {
                if (!updatedStartIndex) {
                    startIndex = row.getRowNumber();
                    updatedStartIndex = true;
                }
                if (row.getDouble("value") > testLow
                        || row.getDouble("patient") > testHigh) {
                    System.out.println(row.getRowNumber());
                    rowNumber = row.getRowNumber();
                    break;
                }
            }
        }
        return rowNumber;
    }

    private static int getRowNumberSimple(Table t, LocalDateTime testDate, double testLow, double testHigh) {
        int rowNumber = -1;
        long testPackedDateTime = PackedLocalDateTime.pack(testDate);  // packing saves time
        for (Row row : t) {
            if (row.getPackedDateTime("date").isOnOrAfter(testPackedDateTime)
                    && (row.getDouble("value") > testLow
                    || row.getDouble("patient") > testHigh)) {
                return row.getRowNumber();
            }
        }
        return rowNumber;
    }

    private static Table defineSchema() {
        Table t;
        t = Table.create("Observations");
        StringColumn conceptId = StringColumn.create("concept");
        DateTimeColumn date = DateTimeColumn.create("date");
        NumberColumn value = DoubleColumn.create("value");
        NumberColumn patientId = DoubleColumn.create("patient");
        patientId.setPrintFormatter(NumberColumnFormatter.ints());

        t.addColumns(conceptId);
        t.addColumns(date);
        t.addColumns(value);
        t.addColumns(patientId);
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

        while (patientIds.size() <= PATIENT_COUNT) {
            patientIds.add(RandomUtils.nextInt(0, 2_000_000_000));
        }

        while (dates.size() <= numberOfRecordsInTable) {
            dateTime = dateTime.plusMinutes(1);
            dates.add(PackedLocalDateTime.pack(dateTime));
        }

        DateTimeColumn dateColumn = table.dateTimeColumn("date");
        StringColumn conceptColumn = table.stringColumn("concept");
        NumberColumn valueColumn = table.numberColumn("value");
        NumberColumn patientColumn = table.numberColumn("patient");

        // sample from the pools to write the data
        for (int i = 0; i < observationCount; i++) {
            dateColumn.appendInternal(dates.getLong(i));
            conceptColumn.append(concepts.get(RandomUtils.nextInt(0, concepts.size())));
            valueColumn.append(RandomUtils.nextDouble(0f, 100_000f));
            patientColumn.append(patientIds.getInt(RandomUtils.nextInt(0, patientIds.size())));
        }
    }

    // TODO(lwhite): Put this in a Test utils class
    private static LocalDate randomDate() {
        Random random = new Random();
        int minDay = (int) LocalDate.of(2000, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2016, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt(maxDay - minDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    private enum DependencyFilter {
        FIRST,
        LAST,
        ANY
    }

    private static class IndependentResult {
        RangeSet<LocalDate> dateRanges = TreeRangeSet.create();

        void addRange(Range<LocalDate> dateRange) {
            dateRanges.add(dateRange);
        }
    }
}

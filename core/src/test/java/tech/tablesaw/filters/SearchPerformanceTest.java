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
import tech.tablesaw.api.*;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.strings.StringColumnReference;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSlice;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.table.TableSliceGroup;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;
import static tech.tablesaw.api.QueryHelper.*;

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
    private static IntArrayList dates = new IntArrayList(size);

    public static void main(String[] args) throws Exception {

        int numberOfRecordsInTable = 5_000_000;
        Stopwatch stopwatch = Stopwatch.createStarted();

        Table t = defineSchema();
        generateTestData(t, numberOfRecordsInTable, stopwatch);

        t.setName("Observations");

        DateColumn dates = t.dateColumn("date");
        NumberColumn values = t.numberColumn("value");
        NumberColumn patients = t.numberColumn("patient");

        System.out.println(t.structure());
        System.out.println(dates.summary());
        System.out.println(values.summary());
        System.out.println(patients.summary());

        stopwatch.reset();
        stopwatch.start();
        Selection d = dates.isAfter(LocalDate.of(2010, 01,01));

        Selection v = values.isGreaterThan(60_000);
        Selection p = patients.isGreaterThan(1_500_000_000);
        Selection and = d.and(v).or(p);
        stopwatch.stop();
        System.out.println("Search time " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        System.out.println("d size " + d.size());
        System.out.println("v size " + v.size());
        System.out.println("p size " + p.size());
        System.out.println("and size " + and.size());

        stopwatch.reset();
        stopwatch.start();
        int i = and.get(0);
        System.out.println(t.rows(i));
        stopwatch.stop();
        System.out.println("get first row time " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        System.out.println("Done");
    }

    private static Table defineSchema() {
        Table t;
        t = Table.create("Observations");
        StringColumn conceptId = StringColumn.create("concept");
        DateColumn date = DateColumn.create("date");
        NumberColumn value =  DoubleColumn.create("value");
        NumberColumn patientId =  DoubleColumn.create("patient");
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
        generateData(numberOfRecordsInTable, t);
        out.println("Time to generate "
                + numberOfRecordsInTable + " records: "
                + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }

    private static void generateData(int observationCount, Table table) {
        // createFromCsv pools of random values

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(32, 127).build();
        while (concepts.size() <= CONCEPT_COUNT) {
            concepts.add(generator.generate(30));
        }

        while (patientIds.size() <= PATIENT_COUNT) {
            patientIds.add(RandomUtils.nextInt(0, 2_000_000_000));
        }

        while (dates.size() <= size) {
            dates.add(PackedLocalDate.pack(randomDate()));
        }

        DateColumn dateColumn = table.dateColumn("date");
        StringColumn conceptColumn = table.stringColumn("concept");
        NumberColumn valueColumn = table.numberColumn("value");
        NumberColumn patientColumn = table.numberColumn("patient");

        // sample from the pools to write the data
        for (int i = 0; i < observationCount; i++) {
            dateColumn.appendInternal(dates.getInt(RandomUtils.nextInt(0, dates.size())));
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

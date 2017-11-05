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
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.columns.packeddata.PackedLocalDate;
import tech.tablesaw.table.TemporaryView;
import tech.tablesaw.table.ViewGroup;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;

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
public class TimeDependentFilteringTest {

    private static final int CONCEPT_COUNT = 10;
    private static final int PATIENT_COUNT = 10_000;

    // pools to get random test data from
    private static List<String> concepts = new ArrayList<>(CONCEPT_COUNT);
    private static IntArrayList patientIds = new IntArrayList(PATIENT_COUNT);
    private static int size = 60 * 365;
    private static IntArrayList dates = new IntArrayList(size);

    public static void main(String[] args) throws Exception {

        int numberOfRecordsInTable = 100_000;
        Stopwatch stopwatch = Stopwatch.createStarted();

        Table t = defineSchema();
        generateTestData(t, numberOfRecordsInTable, stopwatch);

        t.setName("Observations");

        // non temporal constraints
        String conceptA = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
        String conceptB = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));

        // independent temporal constraints
        String conceptZ = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
        String conceptD = t.categoryColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
        DependencyFilter independentConstraintFilter = DependencyFilter.FIRST;

        // temporal dependency range constraint
        Range<Integer> daysConstraint = Range.closed(0, 0);

        ColumnReference concept = column("concept");

        //Non-temporal clause
        Table nt = t.selectWhere(
                both(concept.isEqualTo(conceptA),
                        (concept.isNotEqualTo(conceptB))));

        IntColumn ntPatients = nt.intColumn("patient");

        // Group the original table by patient id
        ViewGroup patients = ViewGroup.create(t, "patient");

        // Create a list of patient sub-tables to work with TODO(lwhite): Build the copy-on-write to ViewGroups to avoid
        CopyOnWriteArrayList<TemporaryView> patientTables = new CopyOnWriteArrayList<>(patients.getSubTables());

        // Apply the independent temporal event filtering to the patient subtables and remove any that don't pass
        for (TemporaryView patientTable : patients) {
            CategoryColumn concepts = patientTable.categoryColumn("concept");
            int patientId = Integer.parseInt(patientTable.name());
            if (!concepts.contains(conceptZ)
                    || concepts.contains(conceptD)) {
                patientTables.remove(patientTable);
            } else if (!ntPatients.contains(patientId)) {      // filtering out the non-temporal now constraints for
                // efficiency
                patientTables.remove(patientTable);
            }
        }

        List<IndependentResult> independentResults = new ArrayList<>();

        // Working with the filtered patient tables, calculate the event dates for the independent events
        for (TemporaryView patientTable : patientTables) {
            IndependentResult result = new IndependentResult();
            List<LocalDate> eventDates = new ArrayList<>();

            // iterate an individual table and find the rows where concept matches the target concept
            for (int row : patientTable) {
                CategoryColumn concepts = patientTable.categoryColumn("concept");
                DateColumn dates = patientTable.dateColumn("date");
                if (concepts.get(row).equals(conceptZ)) {
                    eventDates.add(dates.get(row));
                }
            }


            if (independentConstraintFilter == DependencyFilter.FIRST) {
                if (eventDates.isEmpty()) {
                    // this is an error
                    System.out.println(patientTable.name());
                } else {  //Get the first event for the current patient and createFromCsv a date range around it
                    LocalDate date = eventDates.get(0);
                    result.addRange(Range.closed(date.minusDays(daysConstraint.lowerEndpoint()),
                            date.plusDays(daysConstraint.upperEndpoint())));
                } //TODO handle last and any cases
            }
            independentResults.add(result);
        }


        System.out.println("Done");
    }

    private static Table defineSchema() {
        Table t;
        t = Table.create("Observations");
        CategoryColumn conceptId = new CategoryColumn("concept");
        DateColumn date = new DateColumn("date");
        FloatColumn value = new FloatColumn("value");
        IntColumn patientId = new IntColumn("patient");

        t.addColumn(conceptId);
        t.addColumn(date);
        t.addColumn(value);
        t.addColumn(patientId);
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

    private static void generateData(int observationCount, Table table) throws IOException {
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
        CategoryColumn conceptColumn = table.categoryColumn("concept");
        FloatColumn valueColumn = table.floatColumn("value");
        IntColumn patientColumn = table.intColumn("patient");

        // sample from the pools to write the data
        for (int i = 0; i < observationCount; i++) {
            dateColumn.appendInternal(dates.getInt(RandomUtils.nextInt(0, dates.size())));
            conceptColumn.add(concepts.get(RandomUtils.nextInt(0, concepts.size())));
            valueColumn.append(RandomUtils.nextFloat(0f, 100_000f));
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

    private static enum DependencyFilter {
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

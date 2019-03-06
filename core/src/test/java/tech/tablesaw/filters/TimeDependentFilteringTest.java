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
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSlice;
import tech.tablesaw.table.TableSliceGroup;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.fail;

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

        int numberOfRecordsInTable = 100_000_000;
        Stopwatch stopwatch = Stopwatch.createStarted();

        Table t = defineSchema();
        generateTestData(t, numberOfRecordsInTable, stopwatch);

        t.setName("Observations");

        // non temporal constraints
        String conceptA = t.stringColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
        String conceptB = t.stringColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));

        // independent temporal constraints
        String conceptZ = t.stringColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
        String conceptD = t.stringColumn("concept").get(RandomUtils.nextInt(0, t.rowCount()));
        DependencyFilter independentConstraintFilter = DependencyFilter.FIRST;

        // temporal dependency range constraint
        Range<Integer> daysConstraint = Range.closed(0, 0);

        StringColumn concept = t.stringColumn("concept");

        //Non-temporal clause
        Table nt = t.where(concept.isEqualTo(conceptA).and(concept.isNotEqualTo(conceptB)));

        DoubleColumn ntPatients = nt.doubleColumn("patient");

        // Group the original table by patient id
        TableSliceGroup patients = StandardTableSliceGroup.create(t, "patient");

        // Create a list of patient sub-tables to work with TODO(lwhite): Build the copy-on-write to ViewGroups to avoid
        CopyOnWriteArrayList<TableSlice> patientTables = new CopyOnWriteArrayList<>(patients.getSlices());

        // Apply the independent temporal event filtering to the patient subtables and remove any that don't pass
        for (TableSlice patientTable : patients) {
            StringColumn concepts = patientTable.stringColumn("concept");
            double patientId = Double.parseDouble(patientTable.name());
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
        for (TableSlice patientTable : patientTables) {
            IndependentResult result = new IndependentResult();
            List<LocalDate> eventDates = new ArrayList<>();

            // iterate an individual table and find the rows where concept matches the target concept
            for (int row : patientTable) {
                StringColumn concepts = patientTable.stringColumn("concept");
                DateColumn dates = patientTable.dateColumn("date");
                if (concepts.get(row).equals(conceptZ)) {
                    eventDates.add(dates.get(row));
                }
            }


            if (independentConstraintFilter == DependencyFilter.FIRST) {
                if (eventDates.isEmpty()) {
                    // this is an error
                    fail("There are no event dates");
                } else {  //Get the first event for the current patient and createFromCsv a date range around it
                    LocalDate date = eventDates.get(0);
                    result.addRange(Range.closed(date.minusDays(daysConstraint.lowerEndpoint()),
                            date.plusDays(daysConstraint.upperEndpoint())));
                } //TODO handle last and any cases
            }
            independentResults.add(result);
        }
    }

    private static Table defineSchema() {
        Table t;
        t = Table.create("Observations");
        StringColumn conceptId = StringColumn.create("concept");
        DateColumn date = DateColumn.create("date");
        DoubleColumn value =  DoubleColumn.create("value");
        DoubleColumn patientId =  DoubleColumn.create("patient");
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
        DoubleColumn valueColumn = table.doubleColumn("value");
        DoubleColumn patientColumn = table.doubleColumn("patient");

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
        private RangeSet<LocalDate> dateRanges = TreeRangeSet.create();

        private void addRange(Range<LocalDate> dateRange) {
            dateRanges.add(dateRange);
        }
    }
}

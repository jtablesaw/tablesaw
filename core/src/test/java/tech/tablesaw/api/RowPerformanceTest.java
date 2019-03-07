package tech.tablesaw.api;

import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.text.RandomStringGenerator;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class RowPerformanceTest {

    private static final int CONCEPT_COUNT = 1000;

    // pools to get random test data from
    private static List<String> concepts = new ArrayList<>(CONCEPT_COUNT);
    private static LongArrayList dates = new LongArrayList(5_000_000);

    private static int numberOfRecordsInTable = 5_000_000;

    public static void main(String[] args) {


        Table t = defineSchema();
        generateTestData(t, numberOfRecordsInTable);
        System.out.println();

        System.out.println("Test table info: ");
        System.out.println(t.structure());
        System.out.println();
        System.out.println(t.shape());
        System.out.println();
        System.out.println();

        Row row = new Row(t);
        // run with no operations
        Stopwatch stopwatch = Stopwatch.createStarted();
        while(row.hasNext()) {
            row.next();
        }
        stopwatch.stop();
        System.out.println("No op test (iteration only): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        System.out.println();
        System.out.println("Tests getting columns by name");

        // run with one getDouble()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getDouble("lowValue");
        }
        stopwatch.stop();
        System.out.println("one getDouble(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getInt() (casting from double)
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getInt("lowValue");
        }
        stopwatch.stop();
        System.out.println("one getInt(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getPackedDateTime()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getPackedDateTime("date");
        }
        stopwatch.stop();
        System.out.println("one getPackedDateTime(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getDateTime()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getDateTime("date");
        }
        stopwatch.stop();
        System.out.println("one getDateTime(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getString()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getString("concept");
        }
        stopwatch.stop();
        System.out.println("one getString(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with a packedDateTime, aString, and two doubles
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getString("concept");
            row.getPackedDateTime("date");
            row.getDouble("lowValue");
            row.getDouble("highValue");
        }
        stopwatch.stop();
        System.out.println("Getting four values: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");


        System.out.println();
        System.out.println("Tests getting columns by index");

        // run with one getDouble()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getDouble(2);
        }
        stopwatch.stop();
        System.out.println("one getDouble(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getInt() (casting from double)
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getInt(2);
        }
        stopwatch.stop();
        System.out.println("one getInt(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getPackedDateTime()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getPackedDateTime(1);
        }
        stopwatch.stop();
        System.out.println("one getPackedDateTime(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getDateTime()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getDateTime(1);
        }
        stopwatch.stop();
        System.out.println("one getDateTime(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with one getString()
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getString(0);
        }
        stopwatch.stop();
        System.out.println("one getString(): " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        // run with a packedDateTime, aString, and two doubles
        row = new Row(t);
        stopwatch.reset();
        stopwatch.start();
        while(row.hasNext()) {
            row.next();
            row.getString(0);
            row.getPackedDateTime(1);
            row.getDouble(2);
            row.getDouble(3);
        }
        stopwatch.stop();
        System.out.println("Getting four values: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        System.out.println("Done");
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

    private static void generateTestData(Table t, int numberOfRecordsInTable) {
        out.println("Generating test data");
        LocalDateTime startDateTime = LocalDateTime.of(2008, 1, 1, 0, 0, 0);
        generateData(numberOfRecordsInTable, startDateTime, t);
        out.println("Done ");
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

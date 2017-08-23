package tech.tablesaw.examples;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.csv.CsvWriter;
import tech.tablesaw.store.StorageManager;

/**
 * Tests manipulation of large (but not big) data sets
 */
public class LargeDataTest {

    private static String CSV_FILE = "bigdata/people1.csv";

    public static void main(String[] args) throws Exception {

        Stopwatch stopwatch = Stopwatch.createStarted();

        ColumnType[] columnTypes = {ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType
                .CATEGORY, ColumnType.CATEGORY, ColumnType.CATEGORY, ColumnType.LOCAL_DATE, ColumnType.SHORT_INT,
                ColumnType.SHORT_INT, ColumnType.BOOLEAN};
        Table t = CsvReader.read(columnTypes, CSV_FILE);
        System.out.println("Time to read from CSV File " + stopwatch.elapsed(TimeUnit.SECONDS));

        stopwatch = stopwatch.reset().start();
        storeInDb(t);
        System.out.println("Time to store in columnStore " + stopwatch.elapsed(TimeUnit.SECONDS));

        stopwatch.reset().start();
        System.out.println(t.categoryColumn("first name").first(5).print());
        System.out.println("Time to print first 5 from first name column " + stopwatch.elapsed(TimeUnit.MILLISECONDS)
                + " ms");
        System.out.println();

        stopwatch.reset().start();
        System.out.println(t.shortColumn("weight").summary());
        System.out.println("Time to summarize weight column " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        System.out.println();

        stopwatch.reset().start();
        System.out.println(t.shortColumn("height").summary());
        System.out.println("Time to summarize height column " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        System.out.println();

        stopwatch.reset().start();
        System.out.println(t.first(5));
        System.out.println("Time to print first(5) " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        System.out.println();

        stopwatch.reset().start();
        System.out.println(t.structure());
        System.out.println("Time to print structure " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        System.out.println();

        stopwatch.reset().start();
        CsvWriter.write(t, "bigdata/shortpeople2.csv");
        System.out.println("Time to write csv file " + stopwatch.elapsed(TimeUnit.SECONDS));
        System.out.println();
    }

    private static void storeInDb(Table t) throws Exception {
        StorageManager.saveTable("bigdata/peopleShort2", t);
    }
}

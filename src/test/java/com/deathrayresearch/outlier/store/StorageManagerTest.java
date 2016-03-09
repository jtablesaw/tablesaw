package com.deathrayresearch.outlier.store;

import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.columns.TextColumn;
import com.deathrayresearch.outlier.io.CsvReader;
import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static com.deathrayresearch.outlier.columns.ColumnType.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class StorageManagerTest {

  private static final int COUNT = 100_000;

  Relation table = new Table("t");
  FloatColumn floatColumn = FloatColumn.create("float");
  TextColumn textColumn = TextColumn.create("text");
  CategoryColumn categoryColumn = CategoryColumn.create("cat");
  LocalDateColumn localDateColumn = LocalDateColumn.create("date");

  @Before
  public void setUp() throws Exception {

    for (int i = 0; i < COUNT; i++) {
      floatColumn.add((float) i);
      localDateColumn.add(LocalDate.now());
      textColumn.add("Testing");
      categoryColumn.add("Category");
    }
    table.addColumn(floatColumn);
    table.addColumn(localDateColumn);
    table.addColumn(textColumn);
    table.addColumn(categoryColumn);
  }

  @Test
  public void testWriteTable() throws IOException {
    System.out.println(table.head(5).print());
    Stopwatch stopwatch = Stopwatch.createStarted();
    StorageManager.saveTable("databases", table);
    System.out.println("Write time (ms) " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    Table t = StorageManager.readTable("databases/" + table.id());
    t.sortOn("cat");
    System.out.print(t.head(5).print());
  }

  @Ignore
  @Test
  public void testWriteReadFloatColumn() throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    StorageManager.writeColumn("testfolder/test_col", floatColumn);
    System.out.println("Milliseconds to write float col:"  + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    stopwatch = Stopwatch.createStarted();
    FloatColumn floatColumn1 = StorageManager.readFloatColumn("testfolder/test_col", "test");
    assertEquals(COUNT, floatColumn1.size());
    System.out.println("Milliseconds to read float col:"  + stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }

  public static void main(String[] args) throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");
    Table flights2015 = CsvReader.read("bigdata/2015.csv", heading);
    System.out.println(String.format("loaded %d records in %d seconds",
        flights2015.rowCount(),
        (int) stopwatch.elapsed(TimeUnit.SECONDS)));
    out(flights2015.shape());
    out(flights2015.columnNames().toString());
    out(flights2015.head(10).print());
    stopwatch.reset().start();
    StorageManager.saveTable("databases", flights2015);
    out("Write time in column store: " + stopwatch.elapsed(TimeUnit.SECONDS));
    stopwatch.reset().start();
    flights2015 = StorageManager.readTable("databases/" + flights2015.id());
    out("Read time from column store: " + stopwatch.elapsed(TimeUnit.SECONDS));
    out(flights2015.head(5).print());
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  // The full set of available columns in the dataset
  static ColumnType[] heading = {
      LOCAL_DATE, // flight date
      CAT,  // unique carrier
      CAT,  // airline id
      CAT,  // carrier
      CAT,  // TailNum
      CAT,  // FlightNum
      CAT,  // Origin airport id
      CAT,  // Origin
      CAT,  // Dest airport id
      CAT,  // Dest
      LOCAL_TIME, // CRSDepTime
      LOCAL_TIME, // DepTime
      FLOAT, // DepDelay
      FLOAT, // TaxiOut
      FLOAT, // TaxiIn
      LOCAL_TIME, // CRSArrTime
      LOCAL_TIME, // ArrTime
      FLOAT,   // ArrDelay
      BOOLEAN, // Cancelled
      CAT,     // CancellationCode
      BOOLEAN, // Diverted
      FLOAT, // CRSElapsedTime
      FLOAT, // ActualElapsedTime
      FLOAT, // AirTime
      FLOAT, // Distance
      FLOAT, // CarrierDelay
      FLOAT, // WeatherDelay
      FLOAT, // NASDelay
      FLOAT, // SecurityDelay
      FLOAT  // LateAircraftDelay
  };
}
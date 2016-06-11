package com.github.lwhite1.outlier.store;

import com.github.lwhite1.outlier.columns.CategoryColumn;
import com.github.lwhite1.outlier.columns.FloatColumn;
import com.github.lwhite1.outlier.columns.LocalDateColumn;
import com.github.lwhite1.outlier.columns.LongColumn;
import com.github.lwhite1.outlier.Relation;
import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.api.ColumnType;
import com.github.lwhite1.outlier.io.CsvReader;
import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static com.github.lwhite1.outlier.api.ColumnType.*;

/**
 * Tests for StorageManager
 */
public class StorageManagerTest {

  private static final int COUNT = 100_000;

  private Relation table = new Table("t");
  private FloatColumn floatColumn = FloatColumn.create("float");
  private CategoryColumn textColumn = CategoryColumn.create("text");
  private CategoryColumn categoryColumn = CategoryColumn.create("cat");
  private LocalDateColumn localDateColumn = LocalDateColumn.create("date");
  private LongColumn longColumn = LongColumn.create("long");

  @Before
  public void setUp() throws Exception {

    for (int i = 0; i < COUNT; i++) {
      floatColumn.add((float) i);
      localDateColumn.add(LocalDate.now());
      textColumn.add("Testing");
      categoryColumn.add("Category");
      longColumn.add(i);
    }
    table.addColumn(floatColumn);
    table.addColumn(localDateColumn);
    table.addColumn(textColumn);
    table.addColumn(categoryColumn);
    table.addColumn(longColumn);
  }

  @Test
  public void testWriteTable() throws IOException {
    System.out.println(table.head(5).print());
    StorageManager.saveTable("databases/mytable.db", table);

    Table t = StorageManager.readTable("databases/mytable.db");
    t.sortOn("cat");
    System.out.print(t.head(5).print());
  }

  @Ignore
  @Test
  public void testWriteTableTwice() throws IOException {

    StorageManager.saveTable("databases/mytable.db", table);
    Table t = StorageManager.readTable("databases/mytable.db");

    t.floatColumn(0).setName("a float column");
    System.out.println(t.head(3).print());
  }

  public static void main(String[] args) throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");
    Table flights2015 = CsvReader.read(heading, "bigdata/2015.csv");
    System.out.println(String.format("loaded %d records in %d seconds",
        flights2015.rowCount(),
        stopwatch.elapsed(TimeUnit.SECONDS)));
    out(flights2015.shape());
    out(flights2015.columnNames().toString());
    out(flights2015.head(10).print());
    stopwatch.reset().start();
    StorageManager.saveTable("databases", flights2015);
    stopwatch.reset().start();
    flights2015 = StorageManager.readTable("databases/" + flights2015.id());
    out(flights2015.head(5).print());
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  // The full set of available columns in the dataset
  static ColumnType[] heading = {
      LOCAL_DATE, // flight date
      CATEGORY,  // unique carrier
      CATEGORY,  // airline id
      CATEGORY,  // carrier
      CATEGORY,  // TailNum
      CATEGORY,  // FlightNum
      CATEGORY,  // Origin airport id
      CATEGORY,  // Origin
      CATEGORY,  // Dest airport id
      CATEGORY,  // Dest
      LOCAL_TIME, // CRSDepTime
      LOCAL_TIME, // DepTime
      FLOAT, // DepDelay
      FLOAT, // TaxiOut
      FLOAT, // TaxiIn
      LOCAL_TIME, // CRSArrTime
      LOCAL_TIME, // ArrTime
      FLOAT,   // ArrDelay
      BOOLEAN, // Cancelled
      CATEGORY,     // CancellationCode
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
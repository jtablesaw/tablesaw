package com.github.lwhite1.tablesaw.store;

import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.columns.FloatColumn;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import com.github.lwhite1.tablesaw.columns.LongColumn;
import com.github.lwhite1.tablesaw.table.Relation;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.io.CsvReader;
import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static java.lang.System.out;

/**
 * Tests for StorageManager
 */
public class StorageManagerTest {

  private static final int COUNT = 5;

  private Relation table = new Table("t");
  private FloatColumn floatColumn = FloatColumn.create("float");
  private CategoryColumn categoryColumn = CategoryColumn.create("cat");
  private LocalDateColumn localDateColumn = LocalDateColumn.create("date");
  private LongColumn longColumn = LongColumn.create("long");

  @Before
  public void setUp() throws Exception {

    for (int i = 0; i < COUNT; i++) {
      floatColumn.add((float) i);
      localDateColumn.add(LocalDate.now());
      categoryColumn.add("Category " + i);
      longColumn.add(i);
    }
    table.addColumn(floatColumn);
    table.addColumn(localDateColumn);
    table.addColumn(categoryColumn);
    table.addColumn(longColumn);
  }

  @Test
  public void testCatStorage() throws Exception {
    out(categoryColumn.first(5).print());
    StorageManager.writeColumn("cat_dogs", categoryColumn);
    CategoryColumn readCat = StorageManager.readCategoryColumn("cat_dogs", categoryColumn.columnMetadata());
    out(readCat.first(5).print());
  }

  @Test
  public void testWriteTable() throws IOException {
    out.println(table.first(5).print());
    StorageManager.saveTable("/tmp/mytables", table);

    Table t = StorageManager.readTable("/tmp/mytables/t.saw");
    t.sortOn("cat");
    System.out.print(t.first(5).print());
  }

  @Ignore
  @Test
  public void testWriteTableTwice() throws IOException {

    StorageManager.saveTable("/tmp/mytables", table);
    Table t = StorageManager.readTable("/tmp/mytables/t.saw");
    t.floatColumn("float").setName("a float column");

    StorageManager.saveTable("/tmp/mytables", table);
    t = StorageManager.readTable("/tmp/mytables/t.saw");

    System.out.println(t.first(3).print());
  }

  public static void main(String[] args) throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");
    Table tornados = CsvReader.read(COLUMN_TYPES, "data/1950-2014_torn.csv");
    tornados.setName("tornados");
    System.out.println(String.format("loaded %d records in %d seconds",
        tornados.rowCount(),
        stopwatch.elapsed(TimeUnit.SECONDS)));
    out(tornados.shape());
    out(tornados.columnNames().toString());
    out(tornados.first(10).print());
    stopwatch.reset().start();
    StorageManager.saveTable("/tmp/tablesaw/testdata", tornados);
    stopwatch.reset().start();
    tornados = StorageManager.readTable("/tmp/tablesaw/testdata/tornados.saw");
    out(tornados.first(5).print());
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  // column types for the tornado table
  private static final ColumnType[] COLUMN_TYPES = {
      FLOAT,   // number by year
      FLOAT,   // year
      FLOAT,   // month
      FLOAT,   // day
      LOCAL_DATE,  // date
      LOCAL_TIME,  // time
      CATEGORY, // tz
      CATEGORY, // st
      CATEGORY, // state fips
      FLOAT,    // state torn number
      FLOAT,    // scale
      FLOAT,    // injuries
      FLOAT,    // fatalities
      CATEGORY, // loss
      FLOAT,   // crop loss
      FLOAT,   // St. Lat
      FLOAT,   // St. Lon
      FLOAT,   // End Lat
      FLOAT,   // End Lon
      FLOAT,   // length
      FLOAT,   // width
      FLOAT,   // NS
      FLOAT,   // SN
      FLOAT,   // SG
      CATEGORY,  // Count FIPS 1-4
      CATEGORY,
      CATEGORY,
      CATEGORY};
}
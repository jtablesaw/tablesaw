package com.deathrayresearch.outlier.integration;

import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.io.CsvWriter;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.deathrayresearch.outlier.columns.ColumnType.*;

/**
 *
 */
public class BigDataTest {

  static final ColumnType types[] = {
      LOCAL_DATE_TIME,
      LOCAL_DATE_TIME,
      CAT,
      CAT,
      CAT,
      CAT,
      CAT
  };

  static int[] wanted = {1, 2, 3, 5, 6, 16, 19};
  static String file = "bigdata/311_Service_Requests_from_2010_to_Present.csv";

  public static void main(String[] args) throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = CsvReader.read(file, types, wanted, ',', true);
    out(String.format("Loaded %d rows in %d seconds", table.rowCount(), stopwatch.elapsed(TimeUnit.SECONDS)));

    table.head(3).print();
    stopwatch.reset();

    stopwatch.start();
    CsvWriter.write("testfolder/BigData.csv", table);
    out(String.format("Table written as csv file in %d seconds", stopwatch.elapsed(TimeUnit.SECONDS)));
  }

  //@Test
  public void readCsvTest() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = CsvReader.read("data/BigData.csv", types, true);
    table.head(3).print();
    out(table.rowCount());
    out("Table read from csv file");
    out(stopwatch.elapsed(TimeUnit.SECONDS));
  }

  private static void out(Object str) {
    System.out.println(String.valueOf(str));
  }
}

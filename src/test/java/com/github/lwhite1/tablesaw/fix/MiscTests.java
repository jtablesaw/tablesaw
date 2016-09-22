package com.github.lwhite1.tablesaw.fix;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.store.CSVParserConfig;
import com.github.lwhite1.tablesaw.store.WideCsvIndexer;
import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MiscTests {
  @Test
  public void checkColumnType() throws IOException {
    // TODO : push this file to repo

    String src = "/Users/apple/Downloads/scienaptic.datasets/cisco/Feb_01_Apr_30Cleaned.csv";
    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = Table.createFromCsv(src, true, ',');
    stopwatch.stop();
    System.out.println("Time taken to read file ==> " + stopwatch.toString());

    assertNotNull(table);
    assertEquals("row count", 60670, table.rowCount());
    assertEquals("column count", 80, table.columnCount());

    String dir = Paths.get(src).getParent().toFile().getAbsolutePath();
    String indexDir = Paths.get(dir, "index").toFile().getAbsolutePath();
    stopwatch.reset().start();
    String savedDir = table.save(indexDir);
    stopwatch.stop();
    System.out.println("Saved Dir ==> " + savedDir);
    System.out.println("Time taken to index ==> " + stopwatch.toString());

    stopwatch.reset().start();
    Table readTable = Table.readTable(savedDir);
    stopwatch.stop();
    assertNotNull(readTable);
    System.out.println("Time taken to read from index ==> " + stopwatch.toString());
  }

  @Test
  public void testTimeParsing1() throws IOException {
    // TODO : push this file to repo

    Table table = Table.createFromCsv("/Users/apple/Downloads/scienaptic.datasets/Delay.csv");
    assertNotNull(table);
    System.out.println(table.shape());
    System.out.println(table.columns());
  }

  @Test
  public void testLargeData() throws IOException {
    // Note: these are very large files [3, 6+, 9+ gb]
    // not pushing them to repo
    String src = "/home/mishtu/depot/dataset/part1";
    int expectedRowCount = 8_000_000 - 1;

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = Table.createFromCsv(src, true, '|');
    stopwatch.stop();
    assertNotNull(table);
    assertEquals("row count", expectedRowCount, table.rowCount());
    assertEquals("column count", 76, table.columnCount());

    System.out.println("Time taken to read dataset: " + stopwatch.toString());

    Path dir = Paths.get(src).getParent();
    stopwatch.reset().start();
    String savedDir = table.save(dir.toFile().getAbsolutePath());
    stopwatch.stop();
    System.out.println("Saved directory ==> " + savedDir);
    System.out.println("Time taken to index data: " + stopwatch.toString());

    stopwatch.reset().start();
    table = Table.readTable(savedDir);
    stopwatch.stop();
    assertNotNull(table);
    assertEquals("row count", expectedRowCount, table.rowCount());
    assertEquals("column count", 76, table.columnCount());

    System.out.println("Time taken to read dataset: " + stopwatch.toString());
  }

  @Test
  public void checkBatch() throws IOException {
    String csvFile = "data/cities-states-zipcode.csv";
    CSVParserConfig params = new CSVParserConfig.Builder(csvFile).build();
    testBatchIndexing(params, 81831, 20);
  }

  @Test
  public void checkBatch2() throws IOException {
    // Note: these are very large files [3, 6+, 9+ gb]
    // not pushing them to repo
    String csvFile = "/home/mishtu/depot/dataset/part1";
    CSVParserConfig params = new CSVParserConfig.Builder(csvFile).fieldDelimiter('|').build();
    testBatchIndexing(params, 8_000_000 - 1, 76);
  }

  @Test
  public void checkBatch3() throws IOException {
    // Note: these are very large files [3, 6+, 9+ gb]
    // not pushing them to repo
    String csvFile = "/home/mishtu/depot/dataset/part2";
    CSVParserConfig params = new CSVParserConfig.Builder(csvFile)
        .fieldDelimiter('|')
        .columnBatchSize(13)
        .build();
    testBatchIndexing(params, 18_000_000 - 1, 76);
  }

  private void testBatchIndexing(CSVParserConfig params, int expectedRows, int expectedColumns) throws IOException {
    String csvFile = params.csvFile();
    String fileName = Paths.get(csvFile).getFileName().toString();
    String dir = Paths.get(csvFile).getParent().resolve(fileName + ".saw").toString();
    WideCsvIndexer writer = new WideCsvIndexer(params, dir);

    Stopwatch stopwatch = Stopwatch.createStarted();
    writer.saveInSawFormat();
    stopwatch.stop();
    System.out.println("Time taken to save as saw: " + stopwatch.toString());

    stopwatch.reset().start();
    Table table = Table.readTable(dir);
    stopwatch.stop();
    System.out.println("Time taken to read table: " + stopwatch.toString());
    assertNotNull(table);
    assertEquals("row count", expectedRows, table.rowCount());
    assertEquals("Column count", expectedColumns, table.columnCount());
  }
}

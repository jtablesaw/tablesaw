package com.github.lwhite1.tablesaw.kogu;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import com.github.lwhite1.tablesaw.store.CSVParserConfig;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.store.TableMetadata;
import com.github.lwhite1.tablesaw.store.WideCsvIndexer;
import com.github.lwhite1.tablesaw.store.debug.StoreViewer;
import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
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
    CSVParserConfig params = CSVParserConfig.defaultParser(csvFile);
    testBatchIndexing(params, 81831, 20);
  }

  @Test
  public void checkBatch2() throws IOException {
    // Note: these are very large files [3, 6+, 9+ gb]
    // not pushing them to repo
    String csvFile = "/home/mishtu/depot/dataset/part1";
    CSVParserConfig params = CSVParserConfig.newBuilder(csvFile).fieldDelimiter('|').build();
    testBatchIndexing(params, 8_000_000 - 1, 76);
  }

  @Test
  public void checkBatch3() throws IOException {
    // Note: these are very large files [3, 6+, 9+ gb]
    // not pushing them to repo
    String csvFile = "/home/mishtu/depot/dataset/part2";
    CSVParserConfig params = CSVParserConfig.newBuilder(csvFile)
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

  @Test
  public void testReader() throws IOException {
    String dir = "data/cities-states-zipcode.csv.saw";
    Table table = Table.readTable(dir);
    assertNotNull(table);
    StoreViewer.display(dir);
  }

  @Test
  public void testCisco() throws IOException {
    String s = "/Users/apple/Downloads/scienaptic.datasets/cisco/Feb_01_Apr_30Cleaned.csv";
    String fileName = fileName(s);
    Path parentDir = Paths.get(s).getParent();
    String sawDir = parentDir.toAbsolutePath().toString();
    System.out.println(sawDir);

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = Table.createFromCsv(s, true);
    stopwatch.stop();
    System.out.println("Time taken to read csv: " + stopwatch.toString());

    table.setName(fileName);

    stopwatch.reset().start();
    String storageDir = table.save(sawDir);
    stopwatch.stop();
    System.out.println("Time taken to index table: " + stopwatch.toString());

    StoreViewer.display(storageDir);
  }

  @Test
  public void testBFL() throws IOException {
    String s = "/Users/apple/Downloads/scienaptic.datasets/bfl.data/BFL_Data_Part1.csv";
    String fileName = fileName(s);
    Path parentDir = Paths.get(s).getParent();
    String sawDir = parentDir.toAbsolutePath().toString();
    System.out.println(sawDir);

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = Table.createFromCsv(s, true, '|');
    stopwatch.stop();
    System.out.println("Time taken to read csv: " + stopwatch.toString());

    table.setName(fileName);

    stopwatch.reset().start();
    String storageDir = table.save(sawDir);
    stopwatch.stop();
    System.out.println("Time taken to index table: " + stopwatch.toString());

    StoreViewer.display(storageDir);
  }

  @Test
  public void testBigCsv() throws IOException {
//    String s = "/Users/apple/Downloads/scienaptic.datasets/bfl.data/BFL_Data_Part1.csv";
    String s = "/Users/apple/Downloads/scienaptic.datasets/bfl.data/BFL_FULL.csv";

    Path parentDir = Paths.get(s).getParent();
    String sawDir = parentDir.toAbsolutePath().toString();
    System.out.println(sawDir);

    ColumnType[] columnTypes = new ColumnType[]{INTEGER, CATEGORY, SHORT_INT, CATEGORY, INTEGER, SHORT_INT, CATEGORY, CATEGORY, SHORT_INT, SHORT_INT, SHORT_INT, FLOAT, FLOAT, CATEGORY, CATEGORY, LOCAL_DATE, FLOAT, FLOAT, LOCAL_DATE, LOCAL_DATE, LOCAL_DATE, FLOAT, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, FLOAT, FLOAT, SHORT_INT, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, FLOAT, FLOAT, FLOAT, FLOAT, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, LOCAL_DATE_TIME, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, LOCAL_DATE, LOCAL_DATE, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY, CATEGORY};

    String[] headers = {"CustomerID", "PRDSEG", "BureauKey", "Source", "BureauCustomerID", "BureauProdKey", "AccountNumber", "Institution", "AccountFlag", "AccountType", "OwnershipType", "Balance", "PastDueAmount", "DatePastDue", "Open", "DateClosed", "HighCredit", "SanctionAmount", "LastPaymentDate", "DateReported", "DateOpened", "LastPayment", "Nominee", "WriteOffAmount", "WrittenOffPrincipalAmount", "DateWrittenOff", "SettlementAmount", "PaymentFrequency", "ActualPaymentAmount", "InterestRate", "RepaymentTenure", "DateSanctioned", "DateApplied", "AppliedAmount", "NoOfInstallments", "DisbursedAmount", "InstallmentAmount", "CreditLimit", "CashLimit", "CollateralValue", "RelationInfoType", "ClientName", "LoanCategory", "LoanPurpose", "LoanCycleID", "KeyPerson", "Reason", "DisputeCode", "CollateralType", "DateForErrorCode", "ErrorCode", "DateForErrorDisputeRemark", "ErrorDisputeRemarksCode1", "ErrorDisputeRemarksCode2", "ReportingDateTime", "CreatedDate", "UpdatedDate", "CurrentIndicator", "OriginalPaymentHistoryString", "PaymentHistorystring", "OriginalAssetClassificationString", "AssetclassificationHistoryString", "OriginalSuitFiledString", "SuitfiledString", "PaymentStartDate", "PaymentEndDate", "SuitFiledWilfulDefaultStatus", "WrittenoffandSettledStatus", "LatestAssetClassification", "LatestReportedMonthforAssetClassification", "LatestAccountStatus", "LatestReportedMonthforAccountStatus", "Sector", "CIBILRemarksCode", "DisputeRemarksCode", "ACCOUNTTYPE_DESC"};

//    int numRows = 10_000_000 - 1;
    int numRows = 25238390;
    List<ColumnMetadata> cMetas = new ArrayList<>();

    for (int i = 0; i < headers.length; i++) {
      String colName = headers[i];
      ColumnType columnType = columnTypes[i];
      ColumnMetadata cMeta = new ColumnMetadata(UUID.randomUUID().toString(), colName, columnType, numRows);
      cMetas.add(cMeta);
    }

//    TableMetadata tMeta = new TableMetadata("BFL_Part1", numRows, cMetas);
    TableMetadata tMeta = new TableMetadata("BFL_FULL", numRows, cMetas);

    CSVParserConfig config = CSVParserConfig.newBuilder(s)
        .hasHeader(true)
        .fieldDelimiter('|')
        .build();

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = CsvReader.createFromCSV(config, tMeta);
    stopwatch.stop();
    System.out.println("Time taken to read csv: " + stopwatch.toString());

    stopwatch.reset().start();
    String storageDir = table.save(sawDir);
    stopwatch.stop();
    System.out.println("Time taken to index table: " + stopwatch.toString());

    StoreViewer.display(storageDir);
  }

  @Test
  public void testDistribution() throws InterruptedException, IOException {
    String s = "/Users/apple/Downloads/scienaptic.datasets/bfl.data/BFL_Part1.saw";
    StoreViewer.display(s);

    System.out.println("==================================");

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table table = Table.readTable(s);
    stopwatch.stop();
    System.out.println("Time taken to read saw index: " + stopwatch.toString());
    table.columns().forEach(column ->
        System.out.println(column.name() + " => " + column.countUnique()));
  }

  private String fileName(String s) {
    Path path = Paths.get(s);
    String fileName = path.getFileName().toString();
    int lastIndex = fileName.lastIndexOf(".");
    return lastIndex == -1 ? fileName : fileName.substring(0, lastIndex);
  }
}

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

package tech.tablesaw.io.csv;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.api.ColumnType.*;

import com.google.common.collect.ImmutableMap;
import com.univocity.parsers.common.TextParsingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.datetimes.DateTimeParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.io.AddCellToColumnException;

/** Tests for CSV Reading */
public class CsvReaderTest {

  private static final String LINE_END = System.lineSeparator();
  private static final String COMMA = ",";

  private final ColumnType[] bus_types = {SHORT, STRING, STRING, FLOAT, FLOAT};
  private final ColumnType[] bus_types_with_SKIP = {SHORT, STRING, SKIP, DOUBLE, DOUBLE};

  @Test
  public void testMaxCharsPerColumnPass() throws IOException {
    final Reader reader =
        new StringReader("Text" + LINE_END + "\"short\"" + LINE_END + "1234567890" + LINE_END);

    final int maxCharsPerColumn = 12;

    Table result =
        Table.read().csv(CsvReadOptions.builder(reader).maxCharsPerColumn(maxCharsPerColumn));
    assertEquals(2, result.rowCount());
  }

  @Test
  public void testMaxCharsPerColumnException() {
    final Reader reader =
        new StringReader("Text" + LINE_END + "\"short\"" + LINE_END + "1234567890" + LINE_END);

    final int maxCharsPerColumn = 8;

    assertThrows(
        TextParsingException.class,
        () -> {
          Table.read().csv(CsvReadOptions.builder(reader).maxCharsPerColumn(maxCharsPerColumn));
        });
  }

  @Test
  public void testWithBusData() throws IOException {
    // Read the CSV file
    Table table =
        Table.read()
            .csv(CsvReadOptions.builder("../data/bus_stop_test.csv").columnTypes(bus_types));

    // Look at the column names
    assertEquals(
        "[stop_id, stop_name, stop_desc, stop_lat, stop_lon]", table.columnNames().toString());

    table = table.sortDescendingOn("stop_id");
    table.removeColumns("stop_desc");
  }

  @Test
  @Disabled
  /** TODO: Remove. Text columns should be used as backing column types */
  void textColumnShutoff() throws IOException {

    Table table = Table.read().csv(CsvReadOptions.builder("../data/cities-states-zipcode.csv"));
    ColumnType[] types = {INTEGER, STRING, DOUBLE, BOOLEAN};
    List<ColumnType> typesToDetect = asList(types);
    Table table2 =
        Table.read()
            .csv(
                CsvReadOptions.builder("../data/cities-states-zipcode.csv")
                    .columnTypesToDetect(typesToDetect)
                    .build());

    assertEquals(STRING, table.column("WorldRegion").type());
    assertEquals(STRING, table2.column("WorldRegion").type());
  }

  @Test
  public void testWithColumnSKIP() throws IOException {
    // Read the CSV file
    Table table =
        Table.read()
            .csv(
                CsvReadOptions.builder("../data/bus_stop_test.csv")
                    .columnTypes(bus_types_with_SKIP));

    assertEquals(4, table.columnCount());
    // Look at the column names
    assertEquals("[stop_id, stop_name, stop_lat, stop_lon]", table.columnNames().toString());
  }

  @Test
  void allowDuplicateColumnNames() throws IOException {
    final Reader reader1 =
        new StringReader(
            "Col1" + COMMA + "Col2" + LINE_END + "\"first\"" + COMMA + "second" + LINE_END);
    Table noDupes = Table.read().csv(reader1);
    assertEquals("Col1", noDupes.columnNames().get(0));
    assertEquals("Col2", noDupes.columnNames().get(1));

    final Reader reader2 =
        new StringReader(
            "Col1" + COMMA + "Col1" + LINE_END + "\"first\"" + COMMA + "second" + LINE_END);
    Table dupes =
        Table.read().csv(CsvReadOptions.builder(reader2).allowDuplicateColumnNames(true).build());
    assertEquals("Col1", dupes.columnNames().get(0));
    assertEquals("Col1-2", dupes.columnNames().get(1));
  }

  @Test
  public void testWithColumnSKIPWithoutHeader() throws IOException {
    // Read the CSV file
    Table table =
        Table.read()
            .csv(
                CsvReadOptions.builder("../data/bus_stop_noheader_test.csv")
                    .header(false)
                    .columnTypes(bus_types_with_SKIP));

    assertEquals(4, table.columnCount());
    // Look at the column names
    assertEquals("[C0, C1, C3, C4]", table.columnNames().toString());
  }

  @Test
  public void testWithBushData() throws IOException {
    // Read the CSV file
    ColumnType[] types = {LOCAL_DATE, DOUBLE, STRING};
    Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").columnTypes(types));

    assertEquals(323, table.rowCount());

    // Look at the column names
    assertEquals("[date, approval, who]", table.columnNames().toString());
  }

  @Test
  public void testBushDataWithoutSamplingForTypeDetection() throws IOException {
    // Read the CSV file
    Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").sample(false));

    assertEquals(323, table.rowCount());

    // Look at the column names
    assertEquals("[date, approval, who]", table.columnNames().toString());
  }

  @Test
  public void testDataTypeDetection() throws IOException {
    Reader reader = new FileReader("../data/bus_stop_test.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(true)
            .minimizeColumnSizes()
            .separator(',')
            .sample(false)
            .locale(Locale.getDefault())
            .build();

    ColumnType[] columnTypes = new CsvReader().detectColumnTypes(reader, options);
    assertArrayEquals(bus_types, columnTypes);
  }

  @Test
  public void testNumberTypeDetectionIgnoreZeroDecimal() throws IOException {
    Reader reader = new FileReader("../data/immunization.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader).header(true).sample(false).ignoreZeroDecimal(true).build();

    // Column index 3 and 7 contain values with none to 3 zero values as suffix
    // Should map to type INTEGER when ignoreZeroDecimal = true
    ColumnType[] columnTypes = new CsvReader().detectColumnTypes(reader, options);
    assertEquals(INTEGER, columnTypes[3]);
    assertEquals(INTEGER, columnTypes[7]);
  }

  @Test
  public void testNumberTypeDetectionRetainZeroDecimal() throws IOException {
    Reader reader = new FileReader("../data/immunization.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader).header(true).sample(false).ignoreZeroDecimal(false).build();

    // Column index 3 and 7 contain values with none to 3 zero values as suffix
    // Should map to type DOUBLE when ignoreZeroDecimal = false
    ColumnType[] columnTypes = new CsvReader().detectColumnTypes(reader, options);
    assertEquals(DOUBLE, columnTypes[3]);
    assertEquals(DOUBLE, columnTypes[7]);
  }

  @Test
  public void testMillis() {
    long[] times = {1530486314124L, 1530488214124L};
    LongColumn d = LongColumn.create("times", times);
    DateTimeColumn column = d.asDateTimes(ZoneOffset.UTC);
    assertEquals(1530486314124L, column.get(0).toInstant(ZoneOffset.UTC).toEpochMilli());
  }

  @Test
  public void testLocalDateDetectionEnglish() {

    final Reader reader =
        new StringReader(
            "Date"
                + LINE_END
                + "\"Nov 1, 2017\""
                + LINE_END
                + "\"Oct 1, 2017\""
                + LINE_END
                + "\"Sep 1, 2017\""
                + LINE_END
                + "\"Aug 1, 2017\""
                + LINE_END
                + "\"Jul 1, 2017\""
                + LINE_END
                + "\"Jun 1, 2017\""
                + LINE_END);

    final boolean header = true;
    final char delimiter = ',';
    final boolean useSampling = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .separator(delimiter)
            .sample(useSampling)
            .locale(Locale.ENGLISH)
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));

    assertEquals(Collections.singletonList(LOCAL_DATE), actual);
  }

  @Test
  public void testDateTimeDetection() {

    final Reader reader =
        new StringReader(
            "Date"
                + LINE_END
                + "09-Nov-2014 13:03:04"
                + LINE_END
                + "09-Oct-2014 13:03:56"
                + LINE_END);

    final boolean header = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .dateTimeFormat(DateTimeParser.caseInsensitiveFormatter("dd-MMM-yyyy HH:mm:ss"))
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));

    assertEquals(Collections.singletonList(LOCAL_DATE_TIME), actual);
  }

  @Test
  public void testDateTimeDetection2() {

    final Reader reader =
        new StringReader(
            "Date"
                + LINE_END
                + "09-Nov-2014 13:03:04"
                + LINE_END
                + "09-Oct-2014 13:03:56"
                + LINE_END);

    final boolean header = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .dateTimeFormat(DateTimeParser.caseInsensitiveFormatter("dd-MMM-yyyy HH:mm:ss"))
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));

    assertEquals(Collections.singletonList(LOCAL_DATE_TIME), actual);
  }

  @Test
  public void testDateTimeDetection3() {

    final Reader reader =
        new StringReader(
            "Date"
                + LINE_END
                + "09-NOV-2014 13:03:04"
                + LINE_END
                + "09-OCT-2014 13:03:56"
                + LINE_END);

    final boolean header = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .dateTimeFormat(
                new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-MMM-yyyy HH:mm:ss")
                    .toFormatter())
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));

    assertEquals(Collections.singletonList(LOCAL_DATE_TIME), actual);
  }

  @Test
  public void testDateDetection1() {

    final Reader reader =
        new StringReader("Time" + LINE_END + "13.03.04" + LINE_END + "13.03.04" + LINE_END);

    final boolean header = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .timeFormat(
                new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("HH.mm.ss")
                    .toFormatter())
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));
    assertEquals(Collections.singletonList(LOCAL_TIME), actual);
  }

  @Test
  public void testTimeDetection1() {

    final Reader reader =
        new StringReader("Date" + LINE_END + "09-NOV-2014" + LINE_END + "09-OCT-2014" + LINE_END);

    final boolean header = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .dateFormat(
                new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-MMM-yyyy")
                    .toFormatter())
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));
    assertEquals(Collections.singletonList(LOCAL_DATE), actual);
  }

  @Test
  public void testLocalDateDetectionFrench() {

    final Reader reader =
        new StringReader(
            "Date"
                + LINE_END
                + "\"nov. 1, 2017\""
                + LINE_END
                + "\"oct. 1, 2017\""
                + LINE_END
                + "\"sept. 1, 2017\""
                + LINE_END
                + "\"août 1, 2017\""
                + LINE_END
                + "\"juil. 1, 2017\""
                + LINE_END
                + "\"juin 1, 2017\""
                + LINE_END);

    final boolean header = true;
    final char delimiter = ',';
    final boolean useSampling = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .separator(delimiter)
            .sample(useSampling)
            .locale(Locale.FRENCH)
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));

    assertEquals(Collections.singletonList(LOCAL_DATE), actual);
  }

  @Test
  public void testLocalDateTimeDetectionFrench() {

    final Reader reader =
        new StringReader(
            "Date"
                + LINE_END
                + "09-nov.-2014 13:03"
                + LINE_END
                + "09-oct.-2014 13:03"
                + LINE_END
                + "09-sept.-2014 13:03"
                + LINE_END
                + "09-août-2014 13:03"
                + LINE_END
                + "09-juil.-2014 13:03"
                + LINE_END
                + "09-juin-2014 13:03"
                + LINE_END);

    final boolean header = true;
    final char delimiter = ',';
    final boolean useSampling = true;

    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(header)
            .separator(delimiter)
            .sample(useSampling)
            .locale(Locale.FRENCH)
            .build();

    final List<ColumnType> actual = asList(new CsvReader().detectColumnTypes(reader, options));

    assertEquals(Collections.singletonList(LOCAL_DATE_TIME), actual);
  }

  @Test
  void testWithMissingValue() throws IOException {

    CsvReadOptions options =
        CsvReadOptions.builder("../data/missing_values.csv").missingValueIndicator("-").build();

    Table t = Table.read().csv(options);
    assertEquals(1, t.stringColumn(0).countMissing());
    assertEquals(1, t.numberColumn(1).countMissing());
    assertEquals(1, t.numberColumn(2).countMissing());
  }

  /**
   * Tests using multiple, non-standard missing value indicators, some columns also contains NA or
   * N/A, which are treated as regular string values rather than missing values. This has the
   * side-effect of treating those otherwise numeric columns as StringColumns
   */
  @Test
  @Disabled
  void testWithMissingValues() throws IOException {

    Reader reader =
        new StringReader(
            "Products,Sales,Market_Share\n"
                + "a,?,-\n"
                + "b,12200,NA\n"
                + "c,60000,33\n"
                + "d,N/A,10\n"
                + ",32000,42");
    CsvReadOptions options =
        CsvReadOptions.builder(reader).sample(false).missingValueIndicator("-", "?", "").build();

    Table t = Table.read().csv(options);
    assertEquals(1, t.stringColumn(0).countMissing());
    assertEquals(1, t.stringColumn(1).countMissing());
    assertEquals(1, t.stringColumn(2).countMissing());
  }

  /** Tests the auto-detection of missing values, using multiple missing value indicators */
  @Test
  @Disabled
  void testWithMissingValue2() throws IOException {

    Table t = Table.read().csv("../data/missing_values2.csv");
    assertEquals(1, t.stringColumn(0).countMissing());
    assertEquals(0, t.numberColumn(1).countMissing());
    assertEquals(0, t.numberColumn(2).countMissing());
  }

  @Test
  public void testWindowsAndLinuxLineEndings() throws IOException {
    Reader reader =
        new StringReader(
            "TestCol\n"
                + "foobar1\n"
                + "foobar2\n"
                + "foobar3\n"
                + "foobar4\r\n"
                + "foobar5\r\n"
                + "foobar6\r\n");

    Table t = Table.read().csv(reader);
    assertEquals(1, t.columnCount());
    assertEquals(6, t.rowCount());
  }

  @Test
  public void testCustomLineEndings() throws IOException {
    CsvReadOptions options =
        CsvReadOptions.builder("../data/alt_line_endings.csv").lineEnding("~").header(true).build();

    Table t = Table.read().csv(options);
    assertEquals(2, t.columnCount());
    assertEquals(2, t.rowCount());
  }

  @Test
  public void testDateWithFormatter1() throws IOException {

    final boolean header = false;
    final char delimiter = ',';
    final boolean useSampling = true;

    CsvReadOptions options =
        CsvReadOptions.builder("../data/date_format_test.txt")
            .header(header)
            .separator(delimiter)
            .sample(useSampling)
            .dateFormat(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            .build();

    final Table table = Table.read().csv(options);
    DateColumn date = table.dateColumn(0);
    assertFalse(date.isEmpty());
  }

  @Test
  public void testDateWithFormatter2() throws IOException {

    final boolean header = false;
    final char delimiter = ',';
    final boolean useSampling = true;

    CsvReadOptions options =
        CsvReadOptions.builder("../data/date_format_test.txt")
            .header(header)
            .separator(delimiter)
            .sample(useSampling)
            .dateFormat(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            .build();

    final Table table = Table.read().csv(options);
    DateColumn date = table.dateColumn(0);
    assertFalse(date.isEmpty());
  }

  @Test
  public void testPrintStructure() throws IOException {
    String output =
        "ColumnType[] columnTypes = {"
            + LINE_END
            + "LOCAL_DATE, // 0     date        "
            + LINE_END
            + "INTEGER,    // 1     approval    "
            + LINE_END
            + "STRING,     // 2     who         "
            + LINE_END
            + "}"
            + LINE_END;
    assertEquals(
        output,
        new CsvReader()
            .printColumnTypes(
                CsvReadOptions.builder("../data/bush.csv")
                    .header(true)
                    .separator(',')
                    .locale(Locale.getDefault())
                    .sample(true)
                    .build()));
  }

  @Test
  public void testDataTypeDetection2() throws IOException {
    Reader reader = new FileReader("../data/bush.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(true)
            .separator(',')
            .sample(false)
            .locale(Locale.getDefault())
            .build();

    ColumnType[] columnTypes = new CsvReader().detectColumnTypes(reader, options);
    assertEquals(LOCAL_DATE, columnTypes[0]);
    assertEquals(INTEGER, columnTypes[1]);
    assertEquals(STRING, columnTypes[2]);
  }

  @Test
  public void testLoadFromUrlWithtypeArray() throws IOException {
    ColumnType[] types = {LOCAL_DATE, DOUBLE, STRING};
    Table table;
    try (InputStream input = new File("../data/bush.csv").toURI().toURL().openStream()) {
      table =
          Table.read()
              .csv(
                  CsvReadOptions.builder(input)
                      .tableName("Bush approval ratings")
                      .columnTypes(types));
    }
    assertNotNull(table);
    assertEquals(3, table.columnCount());
  }

  /** Read from a url while performing column type inference */
  @Test
  public void testLoadFromUrl() throws IOException {
    Table table;
    try (InputStream input = new File("../data/bush.csv").toURI().toURL().openStream()) {
      table = Table.read().csv(CsvReadOptions.builder(input).tableName("Bush approval ratings"));
    }
    assertNotNull(table);
    assertEquals(3, table.columnCount());
  }

  /** Read from a file input stream while performing column type inference */
  @Test
  public void testLoadFromFileStream() throws IOException {
    String location = "../data/bush.csv";
    Table table;
    File file = Paths.get(location).toFile();
    try (InputStream input = new FileInputStream(file)) {
      table = Table.read().csv(CsvReadOptions.builder(input).tableName("Bush approval ratings"));
    }
    assertNotNull(table);
    assertEquals(3, table.columnCount());
  }

  /** Read from a file input stream while performing column type inference */
  @Test
  public void testLoadFromFileStreamReader() throws IOException {
    String location = "../data/bush.csv";
    Table table;
    File file = Paths.get(location).toFile();
    try (Reader reader = new FileReader(file)) {
      table = Table.read().csv(CsvReadOptions.builder(reader).tableName("Bush approval ratings"));
    }
    assertNotNull(table);
    assertEquals(3, table.columnCount());
  }

  @Test
  public void testEmptyRow() throws IOException {
    Table table = Table.read().csv("../data/empty_row.csv");
    // Note: tried capturing std err output and asserting on it, but it failed when running as mvn
    // target
    assertEquals(5, table.rowCount());
  }

  @Test
  public void testShortRow() {
    assertThrows(
        AddCellToColumnException.class,
        () -> {
          Table.read().csv("../data/short_row.csv");
        });
  }

  @Test
  public void testLongRow() {
    assertThrows(
        RuntimeException.class,
        () -> {
          Table.read().csv("../data/long_row.csv");
        });
  }

  @Test
  public void testBoundary1() throws IOException {
    Table table = Table.read().csv("../data/boundaryTest1.csv");
    assertEquals(2, table.rowCount());
  }

  @Test
  public void testBoundary2() throws IOException {
    Table table = Table.read().csv("../data/boundaryTest2.csv");
    assertEquals(2, table.rowCount());
  }

  @Test
  public void testReadFailure() {
    // TODO (lwhite): These tests don't fail. What was their intent?
    Table table1 =
        Table.read()
            .csv(CsvReadOptions.builder("../data/read_failure_test.csv").minimizeColumnSizes());
    table1.structure(); // just make sure the import completed
    ShortColumn test = table1.shortColumn("Test");
    // TODO(lwhite): Better tests
    assertNotNull(test.summary());
  }

  @Test
  public void testReadFailure2() throws IOException {
    Table table1 =
        Table.read()
            .csv(CsvReadOptions.builder("../data/read_failure_test2.csv").minimizeColumnSizes());
    table1.structure(); // just make sure the import completed
    ShortColumn test = table1.shortColumn("Test");

    // TODO(lwhite): Better tests
    assertNotNull(test.summary());
  }

  @Test
  public void testEmptyFileHeaderEnabled() throws IOException {
    Table table1 = Table.read().csv(CsvReadOptions.builder("../data/empty_file.csv").header(false));
    assertEquals("empty_file.csv: 0 rows X 0 cols", table1.shape());
  }

  @Test
  public void testEmptyFileHeaderDisabled() throws IOException {
    Table table1 = Table.read().csv(CsvReadOptions.builder("../data/empty_file.csv").header(false));
    assertEquals("empty_file.csv: 0 rows X 0 cols", table1.shape());
  }

  @Test
  public void testReadMaxColumnsExceeded() {
    assertThrows(
        TextParsingException.class,
        () -> {
          Table.read().csv(CsvReadOptions.builder("../data/10001_columns.csv").header(false));
        });
  }

  @Test
  public void testReadWithMaxColumnsSetting() throws IOException {
    Table table1 =
        Table.read()
            .csv(
                CsvReadOptions.builder("../data/10001_columns.csv")
                    .maxNumberOfColumns(10001)
                    .header(false));
    assertEquals("10001_columns.csv: 1 rows X 10001 cols", table1.shape());
  }

  @Test
  public void testSkipLinesWithComments() throws IOException {
    Table table1 =
        Table.read()
            .csv(
                CsvReadOptions.builder("../data/with_comments.csv")
                    .maxNumberOfColumns(3)
                    .commentPrefix('#')
                    .header(true));
    assertEquals("with_comments.csv: 3 rows X 3 cols", table1.shape());
  }

  @Test
  public void carriageReturnLineEnding() throws IOException {
    Table table =
        Table.read().csv(CsvReadOptions.builder("../data/sacramento_real_estate_transactions.csv"));
    assertEquals(985, table.rowCount());
  }

  @Test
  public void testReadCsvWithRowSampling() throws IOException {
    int expectedSampleSize = 10;
    Table table =
        Table.read().csv(CsvReadOptions.builder("../data/cake.csv").sampleSize(expectedSampleSize));

    assertEquals(expectedSampleSize, table.rowCount());
    assertEquals("[recipe, mix, temp, y]", table.columnNames().toString());
  }

  @Test
  public void testReadCsvWithRowSamplingParsingNumbers() throws IOException {

    StringBuilder csv = new StringBuilder();
    csv.append("RandomNumbers\n");
    Set<Integer> values =
        new TreeSet<>(
            asList(
                24323, 542345, 64323, 73640, 38453, 12735, 93456, 23457, 483075, 469364, 473936));
    values.forEach(v -> csv.append(v + "\n"));

    Reader reader = new StringReader(csv.toString());

    int expectedSampleSize = 5;
    Table t = Table.read().csv(CsvReadOptions.builder(reader).sampleSize(expectedSampleSize));

    assertEquals(1, t.columnCount());
    assertEquals(expectedSampleSize, t.rowCount());
    assertEquals(INTEGER, t.column(0).type());

    List<Integer> intValues = t.intColumn(0).asList();
    assertEquals(true, values.containsAll(intValues));
  }

  @Test
  public void preserveQuote() throws IOException {
    Table table = Table.create("test", StringColumn.create("colName", Arrays.asList("\"")));

    // test CSV writes quote properly
    Writer writer = new StringWriter();
    table.write().csv(writer);
    String string = writer.toString();

    // test CSV reads quote back again
    Table out = Table.read().csv(new StringReader(string));
    assertEquals(table.get(0, 0), out.get(0, 0));
  }

  @Test
  public void testReadCsvWithPercentage1() throws IOException {
    Table table = Table.read().csv(CsvReadOptions.builder("../data/currency_percent.csv"));
    assertEquals(DoubleColumnType.instance(), table.typeArray()[1]);
    assertEquals(DoubleColumnType.instance(), table.typeArray()[2]);
  }

  @Test
  public void testReadCsvWithPercentage2() throws IOException {
    Table table = Table.read().csv(CsvReadOptions.builder("../data/currency_percent.csv"));
    DoubleColumn column = (DoubleColumn) table.column(1);
    assertEquals("0.0132", column.getString(0));
    assertEquals("0.32768", column.getString(1));
    assertEquals("1", column.getString(2));
    column.setPrintFormatter(NumberColumnFormatter.percent(2));
    assertEquals("1.32%", column.getString(0));
    assertEquals("32.77%", column.getString(1));
    assertEquals("100.00%", column.getString(2));
  }

  @Test
  public void testSkipRowsWithInvalidColumnCount() throws IOException {
    Table table =
        Table.read()
            .csv(
                CsvReadOptions.builder("../data/short_row.csv")
                    .skipRowsWithInvalidColumnCount(true)
                    .build());
    assertEquals(2, table.rowCount());
  }

  @Test
  public void skipRowsWithInvalidColumnCountWithoutHeader() throws IOException {
    assertThrows(
        AddCellToColumnException.class,
        () -> {
          Table.read()
              .csv(
                  CsvReadOptions.builder("../data/short_row.csv")
                      .header(false)
                      .skipRowsWithInvalidColumnCount(true)
                      .build());
        });
  }

  @Test
  public void testCustomizedColumnTypesMixedWithDetection() throws IOException {
    Reader reader = new FileReader("../data/bus_stop_test.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(true)
            .separator(',')
            .locale(Locale.getDefault())
            .minimizeColumnSizes()
            .columnTypesPartial(
                columnName ->
                    Optional.ofNullable(
                        ImmutableMap.of("stop_id", STRING, "stop_name", STRING, "stop_lon", DOUBLE)
                            .get(columnName)))
            .build();

    ColumnType[] columnTypes = new CsvReader().read(options).typeArray();

    ColumnType[] expectedTypes = Arrays.copyOf(bus_types, bus_types.length);
    expectedTypes[0] = STRING; // stop_id
    expectedTypes[1] = STRING; // stop_name
    expectedTypes[4] = DOUBLE; // stop_lon
    assertArrayEquals(expectedTypes, columnTypes);
  }

  @Test
  public void testCustomizedColumnTypeAllCustomized() throws IOException {
    Reader reader = new FileReader("../data/bus_stop_test.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(true)
            .separator(',')
            .locale(Locale.getDefault())
            .minimizeColumnSizes()
            .columnTypes(columnName -> STRING)
            .build();

    ColumnType[] columnTypes = new CsvReader().read(options).typeArray();

    assertTrue(Arrays.stream(columnTypes).allMatch(columnType -> columnType.equals(STRING)));
  }

  @Test
  public void testColumnsArePreservedWithNoDataIfCustomizedTypesAreProvided() throws IOException {
    Reader reader = new FileReader("../data/bus_stop_test_no_data.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(true)
            .separator(',')
            .locale(Locale.getDefault())
            .minimizeColumnSizes()
            .columnTypesPartial(
                ImmutableMap.of(
                    "stop_id",
                    SHORT,
                    "stop_name",
                    STRING,
                    "stop_desc",
                    STRING,
                    "stop_lat",
                    FLOAT,
                    "stop_lon",
                    FLOAT))
            .build();

    ColumnType[] columnTypes = new CsvReader().read(options).typeArray();

    assertArrayEquals(bus_types, columnTypes);
  }

  @Test
  public void testColumnsArePreservedWithNoDataIfCustomizedTypesAreProvided2() throws IOException {
    Reader reader = new FileReader("../data/bus_stop_test_no_data.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(true)
            .separator(',')
            .locale(Locale.getDefault())
            .minimizeColumnSizes()
            .columnTypes(new ColumnType[] {SHORT, STRING, STRING, FLOAT, FLOAT})
            .build();

    ColumnType[] columnTypes = new CsvReader().read(options).typeArray();

    assertArrayEquals(bus_types, columnTypes);
  }

  @Test
  public void testColumnsArePreservedWithNoDataIfCustomizedTypesAreProvidedPartially()
      throws IOException {
    Reader reader = new FileReader("../data/bus_stop_test_no_data.csv");
    CsvReadOptions options =
        CsvReadOptions.builder(reader)
            .header(true)
            .separator(',')
            .locale(Locale.getDefault())
            .minimizeColumnSizes()
            .columnTypesPartial(ImmutableMap.of("stop_id", SHORT, "stop_name", STRING))
            .build();

    ColumnType[] columnTypes = new CsvReader().read(options).typeArray();

    assertArrayEquals(new ColumnType[] {SHORT, STRING}, columnTypes);
  }
}

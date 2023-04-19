package tech.tablesaw.io.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.booleans.BooleanFormatter;
import tech.tablesaw.columns.datetimes.DateTimeParser;
import tech.tablesaw.columns.instant.InstantColumnFormatter;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.strings.StringColumnFormatter;

public class CsvWriterTest {

  @Test
  void toWriterWithExtension() throws IOException {
    StringColumn colA = StringColumn.create("colA", ImmutableList.of("a", "b"));
    StringColumn colB = StringColumn.create("colB", ImmutableList.of("1", "2"));
    Table table = Table.create("testTable", colA, colB);
    StringWriter writer = new StringWriter();
    table.write().toWriter(writer, "csv");
    assertEquals("colA,colB\na,1\nb,2\n", writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void quoteAll() throws IOException {
    StringColumn colA = StringColumn.create("colA", ImmutableList.of("a", "b"));
    StringColumn colB = StringColumn.create("colB", ImmutableList.of("1", "2"));
    Table table = Table.create("testTable", colA, colB);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).quoteAllFields(true).build());
    assertEquals(
        "\"colA\",\"colB\"\n\"a\",\"1\"\n\"b\",\"2\"\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void dateFormatter() throws IOException {
    Table table = Table.read().csv("../data/bush.csv").rows(1);
    table.dateColumn("date").setPrintFormatter(DateTimeParser.caseInsensitiveFormatter("MMM dd, yyyy"));
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "date,approval,who\n" + "\"Jan 21, 2004\",53,fox\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_double() throws IOException {
    Table table = Table.create("", DoubleColumn.create("percents"));
    table.doubleColumn("percents").setPrintFormatter(NumberColumnFormatter.percent(2));
    table.doubleColumn("percents").append(0.323).append(0.1192).append(1.0);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "percents\n" + "32.30%\n" + "11.92%\n" + "100.00%\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_double2() throws IOException {
    Table table = Table.create("", DoubleColumn.create("percents"));
    table
        .doubleColumn("percents")
        .setPrintFormatter(
            new NumberColumnFormatter(NumberColumnFormatter.percent(2).getFormat(), "NA"));
    table.doubleColumn("percents").append(Double.NaN).append(0.323).append(0.1192).append(1.0);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "percents\n" + "NA\n" + "32.30%\n" + "11.92%\n" + "100.00%\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_date() throws IOException {
    Table table = Table.create("", DateColumn.create("dates"));
    DateTimeFormatter formatter = DateTimeParser.caseInsensitiveFormatter("yyyy-dd-MMM");
    table.dateColumn("dates").setPrintFormatter(formatter, "WHAT?");
    table
        .dateColumn("dates")
        .append(LocalDate.of(2021, 11, 3))
        .appendObj(null)
        .append(LocalDate.of(2021, 3, 11));
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "dates\n" + "2021-03-Nov\n" + "WHAT?\n" + "2021-11-Mar\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_int() throws IOException {
    Table table = Table.create("", IntColumn.create("ints"));
    table.intColumn("ints").setPrintFormatter(NumberColumnFormatter.intsWithGrouping());
    table.intColumn("ints").append(102_123).append(2).append(-1_232_132);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "ints\n" + "\"102,123\"\n" + "2\n" + "\"-1,232,132\"\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_int2() throws IOException {
    Table table = Table.create("", IntColumn.create("ints"));
    table.intColumn("ints").setPrintFormatter(new NumberColumnFormatter("NA"));
    Integer missing = null;
    table.intColumn("ints").append(102_123).append(2).append(missing).append(-1_232_132);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "ints\n" + "102123\n" + "2\n" + "NA\n" + "-1232132\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_float() throws IOException {
    Table table = Table.create("", FloatColumn.create("floats"));
    table.floatColumn("floats").setPrintFormatter(NumberColumnFormatter.fixedWithGrouping(2));
    table.floatColumn("floats").append(032.3f).append(0.1192f).appendObj(null).append(1001.0f);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "floats\n" + "32.30\n" + "0.12\n" + "\n" + "\"1,001.00\"\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_float2() throws IOException {
    Table table = Table.create("", FloatColumn.create("floats"));
    table
        .floatColumn("floats")
        .setPrintFormatter(
            new NumberColumnFormatter(
                NumberColumnFormatter.fixedWithGrouping(2).getFormat(), "NA"));
    table.floatColumn("floats").append(032.3f).append(0.1192f).appendObj(null).append(1001.0f);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "floats\n" + "32.30\n" + "0.12\n" + "NA\n" + "\"1,001.00\"\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_datetime() throws IOException {
    Table table = Table.create("", DateTimeColumn.create("dates"));
    DateTimeFormatter formatter = DateTimeParser.caseInsensitiveFormatter("MMM d, yyyy - hh:mm");
    table.dateTimeColumn("dates").setPrintFormatter(formatter, "WHAT?");
    table.dateTimeColumn("dates").append(LocalDateTime.of(2011, 1, 1, 4, 30));
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "dates\n" + "\"Jan 1, 2011 - 04:30\"\n", writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_boolean() throws IOException {
    Table table = Table.create("", BooleanColumn.create("bools"));
    BooleanFormatter formatter = new BooleanFormatter("Yes", "No", "IDK");
    table.booleanColumn("bools").setPrintFormatter(formatter);
    table.booleanColumn("bools").append(true).append(false).appendMissing();
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "bools\n" + "Yes\n" + "No\n" + "IDK\n", writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_string() throws IOException {
    Table table = Table.create("", StringColumn.create("strings"));
    StringColumnFormatter formatter = new StringColumnFormatter(s -> "[" + s + "]", "N/A");
    table.stringColumn("strings").setPrintFormatter(formatter);
    table.stringColumn("strings").append("hey").append("you").appendMissing();
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "strings\n" + "[hey]\n" + "[you]\n" + "N/A\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_text() throws IOException {
    Table table = Table.create("", StringColumn.create("strings"));
    StringColumnFormatter formatter = new StringColumnFormatter(s -> "[" + s + "]", "N/A");
    table.stringColumn("strings").setPrintFormatter(formatter);
    table.stringColumn("strings").append("hey").append("you").appendMissing();
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "strings\n" + "[hey]\n" + "[you]\n" + "N/A\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_short() throws IOException {
    Table table = Table.create("", ShortColumn.create("ints"));
    table.shortColumn("ints").setPrintFormatter(NumberColumnFormatter.intsWithGrouping());
    table.shortColumn("ints").append((short) 102).append((short) 12_132).append((short) -1_234);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "ints\n" + "102\n" + "\"12,132\"\n" + "\"-1,234\"\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void printFormatter_instant() throws IOException {
    Table table = Table.create("", InstantColumn.create("dates"));
    DateTimeFormatter formatter = DateTimeParser.caseInsensitiveFormatter("MMM d, yyyy - hh:mm");
    table.instantColumn("dates").setPrintFormatter(new InstantColumnFormatter(formatter, "WHAT?"));
    table.instantColumn("dates").append(Instant.parse("2007-12-03T10:15:30.00Z")).appendMissing();
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "dates\n" + "\"Dec 3, 2007 - 10:15\"\n" + "WHAT?\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  /** Test preventing scientific notation */
  @Test
  void printFormatter_scientific_notation() throws IOException {
    Table table = Table.create("", DoubleColumn.create("doubles"));
    DecimalFormat df = new DecimalFormat("0.#");
    df.setMaximumFractionDigits(11);
    NumberColumnFormatter formatter = new NumberColumnFormatter(df);
    table.doubleColumn("doubles").setPrintFormatter(formatter);
    table
        .doubleColumn("doubles")
        .append(32.32342489123)
        .append(0.1192342224)
        .appendObj(null)
        .append(1001.0);
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "doubles\n" + "32.32342489123\n" + "0.1192342224\n" + "\n" + "1001\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void dateTimeFormatter() throws IOException {
    Table table = Table.create("test", DateTimeColumn.create("dt"));
    table.dateTimeColumn(0).append(LocalDateTime.of(2011, 1, 1, 4, 30));
    table
        .dateTimeColumn("dt")
        .setPrintFormatter(DateTimeParser.caseInsensitiveFormatter("MMM d, yyyy - hh:mm"));
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "dt\n" + "\"Jan 1, 2011 - 04:30\"\n", writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  void transformColumnNames() throws IOException {
    Table table = Table.read().csv("../data/bush.csv").rows(1);
    Map<String, String> nameMap = ImmutableMap.of("approval", "popularity", "who", "pollster");
    StringWriter writer = new StringWriter();
    table
        .write()
        .usingOptions(CsvWriteOptions.builder(writer).transformColumnNames(nameMap).build());
    assertEquals(
        "date,popularity,pollster\n" + "2004-01-21,53,fox\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }
}

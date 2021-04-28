package tech.tablesaw.io.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.booleans.BooleanFormatter;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.strings.StringColumnFormatter;

public class CsvWriterTest {

  @Test
  public void toWriterWithExtension() throws IOException {
    StringColumn colA = StringColumn.create("colA", ImmutableList.of("a", "b"));
    StringColumn colB = StringColumn.create("colB", ImmutableList.of("1", "2"));
    Table table = Table.create("testTable", colA, colB);
    StringWriter writer = new StringWriter();
    table.write().toWriter(writer, "csv");
    assertEquals("colA,colB\na,1\nb,2\n", writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  public void quoteAll() throws IOException {
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
  public void dateFormatter() throws IOException {
    Table table = Table.read().csv("../data/bush.csv").rows(1);
    StringWriter writer = new StringWriter();
    table
        .write()
        .usingOptions(
            CsvWriteOptions.builder(writer)
                .dateFormatter(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                .build());
    assertEquals(
        "date,approval,who\n" + "\"Jan 21, 2004\",53,fox\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  public void printFormatter1() throws IOException {
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
  public void printFormatter2() throws IOException {
    Table table = Table.create("", DateColumn.create("dates"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MMM");
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
  public void printFormatter3() throws IOException {
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
  public void printFormatter4() throws IOException {
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
  public void printFormatter5() throws IOException {
    Table table = Table.create("", DateTimeColumn.create("dates"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - hh:mm");
    table.dateTimeColumn("dates").setPrintFormatter(formatter, "WHAT?");
    table.dateTimeColumn("dates").append(LocalDateTime.of(2011, 1, 1, 4, 30));
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "dates\n" + "\"Jan 1, 2011 - 04:30\"\n", writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  public void printFormatter6() throws IOException {
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
  public void printFormatter7() throws IOException {
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
  public void printFormatter8() throws IOException {
    Table table = Table.create("", TextColumn.create("strings"));
    StringColumnFormatter formatter = new StringColumnFormatter(s -> "[" + s + "]", "N/A");
    table.textColumn("strings").setPrintFormatter(formatter);
    table.textColumn("strings").append("hey").append("you").appendMissing();
    StringWriter writer = new StringWriter();
    table.write().usingOptions(CsvWriteOptions.builder(writer).usePrintFormatters(true).build());
    assertEquals(
        "strings\n" + "[hey]\n" + "[you]\n" + "N/A\n",
        writer.toString().replaceAll("\\r\\n", "\n"));
  }

  @Test
  public void printFormatter9() throws IOException {
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
  public void dateTimeFormatter() throws IOException {
    Table table = Table.create("test", DateTimeColumn.create("dt"));
    table.dateTimeColumn(0).append(LocalDateTime.of(2011, 1, 1, 4, 30));
    StringWriter writer = new StringWriter();
    table
        .write()
        .usingOptions(
            CsvWriteOptions.builder(writer)
                .dateTimeFormatter(DateTimeFormatter.ofPattern("MMM d, yyyy - hh:mm"))
                .build());
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

package tech.tablesaw.io.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

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

    @Test
    void numberFormatter() throws IOException {
        StringColumn srtCol = StringColumn.create("strCol", ImmutableList.of("", "b"));
        srtCol.appendMissing();
        IntColumn intCol = IntColumn.create("intCol", 0, 111111);
        intCol.appendMissing();
        ShortColumn shortCol = ShortColumn.create("shortCol", (short) 0, (short) 222);
        shortCol.appendMissing();
        LongColumn longCol = LongColumn.create("longCol", 0L, 333333L);
        longCol.appendMissing();
        DoubleColumn doubleCol = DoubleColumn.create("doubleCol", 0d, 123.123d);
        doubleCol.appendMissing();
        FloatColumn floatCol = FloatColumn.create("floatCol", 0f, 456.456f);
        floatCol.appendMissing();
        final Table testTable = Table.create(srtCol, intCol, shortCol, longCol, doubleCol, floatCol);

        //NumberFormatters
        final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat integerFormat = NumberFormat.getIntegerInstance(Locale.GERMAN);
        NumberFormat decimalFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        decimalFormat.setMinimumFractionDigits(2);
        Map<String, NumberFormat> formatMap = Map.of("doubleCol", currencyFormat);
        
        //TESTS
        //All formatters set
        StringWriter writer = new StringWriter();

        testTable.write().usingOptions(CsvWriteOptions.builder(writer)
                .numberFormatters(integerFormat, decimalFormat, formatMap)
                .separator(';')
                .build());
        
        assertEquals("strCol;intCol;shortCol;longCol;doubleCol;floatCol\n"
                + ";0,00;0,00;0,00;$0.00;0\n"
                + "b;111.111,00;222,00;333.333,00;$123.12;456\n"
                + ";;;;;\n",
                writer.toString().replaceAll("\\r\\n", "\n"));
        
        //Map and whole number formatter set
  
        writer = new StringWriter();
        testTable.write().usingOptions(CsvWriteOptions.builder(writer)
                .numberFormatters(null, decimalFormat, formatMap)
                .separator(';')
                .build());
        assertEquals("strCol;intCol;shortCol;longCol;doubleCol;floatCol\n"
                + ";0,00;0,00;0,00;$0.00;0.0\n"
                + "b;111.111,00;222,00;333.333,00;$123.12;456.456\n"
                + ";;;;;\n",
                writer.toString().replaceAll("\\r\\n", "\n"));
        
        //Map and decimalFormatter set
        
        writer = new StringWriter();
        testTable.write().usingOptions(CsvWriteOptions.builder(writer)
                .numberFormatters(integerFormat, null, formatMap)
                .separator(';')
                .build());
        assertEquals("strCol;intCol;shortCol;longCol;doubleCol;floatCol\n"
                + ";0;0;0;$0.00;0\n"
                + "b;111111;222;333333;$123.12;456\n"
                + ";;;;;\n", 
                writer.toString().replaceAll("\\r\\n", "\n"));
        
        //Map only
       
        writer=new StringWriter();
        testTable.write().usingOptions(CsvWriteOptions.builder(writer)
                .numberFormatters(null, null, formatMap)
                .separator(';')
                .build());
        assertEquals("strCol;intCol;shortCol;longCol;doubleCol;floatCol\n"
                + ";0;0;0;$0.00;0.0\n"
                + "b;111111;222;333333;$123.12;456.456\n"
                + ";;;;;\n",
                writer.toString().replaceAll("\\r\\n", "\n"));
        
        //whole number formatter only
        writer = new StringWriter();
        testTable.write().usingOptions(CsvWriteOptions.builder(writer)
                .numberFormatters(null, decimalFormat, null)
                .separator(';')
                .build());
        assertEquals("strCol;intCol;shortCol;longCol;doubleCol;floatCol\n"
                + ";0,00;0,00;0,00;0.0;0.0\n"
                + "b;111.111,00;222,00;333.333,00;123.123;456.456\n"
                + ";;;;;\n",
                writer.toString().replaceAll("\\r\\n", "\n"));
        
        //All null
        writer = new StringWriter();
        testTable.write().usingOptions(CsvWriteOptions.builder(writer)
                .numberFormatters(null, null, null)
                .separator(';')
                .build());
        assertEquals("strCol;intCol;shortCol;longCol;doubleCol;floatCol\n"
                + ";0;0;0;0.0;0.0\n"
                + "b;111111;222;333333;123.123;456.456\n"
                + ";;;;;\n",
                writer.toString().replaceAll("\\r\\n", "\n"));
        
        //Illegal application of numberformatter
        assertThrows(IllegalArgumentException.class, new Executable() {
             
            @Override
            public void execute() throws Throwable {
                StringWriter writer=new StringWriter();
                Map<String, NumberFormat> formatMap = Map.of("strCol", currencyFormat);
                testTable.write().usingOptions(CsvWriteOptions.builder(writer)
                .numberFormatters(null,null, formatMap)
                .separator(';')
                .build());
            }
        });
        
        

    }
}

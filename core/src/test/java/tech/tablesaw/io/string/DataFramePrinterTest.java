package tech.tablesaw.io.string;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

public class DataFramePrinterTest {

  @Test
  public void printNull() {
    DoubleColumn col = DoubleColumn.create("testCol");
    col.append(5.0);
    col.appendCell(null);
    col.append(3.0);
    Table table = Table.create("nullCellTable", col);
    String out = table.print();
    assertTrue(out.contains("          "));
  }

  @Test
  public void printOneRow() throws IOException {
    Table table = Table.read().csv("../data/bush.csv");
    String out = table.print(1);
    assertTrue(out.contains("2004-02-04"));
    assertTrue(out.contains("53"));
    assertTrue(out.contains("fox"));
  }

  @Test
  public void printWithSmallDoubleNumber() {
    DoubleColumn col = DoubleColumn.create("testCol");
    col.append(0.000003);
    Table table = Table.create("small decimal table", col);
    String out = table.print();
    assertTrue(out.contains("0.000003"));
  }

  @Test
  public void printWithSmallFloatNumber() {
    FloatColumn col = FloatColumn.create("testCol");
    col.append(0.000003f);
    Table table = Table.create("small float table", col);
    String out = table.print();
    assertTrue(out.contains("0.000003"));
  }

  @Test
  public void printWithExponent() {
    DoubleColumn col = DoubleColumn.create("testCol");
    col.append(0.000003);
    col.append(123.000003);
    col.setPrintFormatter(NumberColumnFormatter.standard());
    Table table = Table.create("small float table", col);
    String out = table.print();
    assertTrue(out.contains("3.0E-6"));
    assertTrue(out.contains("123.000003"));
  }

  @Test
  public void printWithLargeDoubleNumber() {
    DoubleColumn col = DoubleColumn.create("testCol");
    col.append(33.333333333333328);
    col.append(9007199254740992d);
    col.append(900719925474099.1d);
    col.append(90071992547409.11d);
    Table table = Table.create("large float table", col);
    String out = table.print();
    assertTrue(out.contains("33.3333333333333"));
    assertTrue(out.contains("9007199254740992"));
    assertTrue(out.contains("900719925474099.1"));
    assertTrue(out.contains("90071992547409.11"));
  }

  @Test
  public void printWithLargeFloatNumber() {
    FloatColumn col = FloatColumn.create("testCol");
    col.append(33.333333333333328f);
    col.append(900719925474f);
    col.append(9007199254.1f);
    col.append(90071992.11f);
    col.append(90071.11f);
    Table table = Table.create("large float table", col);
    String out = table.print();
    assertTrue(out.contains("33.33333206176758"));
    assertTrue(out.contains("900719902720"));
    assertTrue(out.contains("9007199232"));
    assertTrue(out.contains("90071992"));
    assertTrue(out.contains("90071.109375"));
  }
}

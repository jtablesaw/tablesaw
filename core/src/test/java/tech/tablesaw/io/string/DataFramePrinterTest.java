package tech.tablesaw.io.string;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public class DataFramePrinterTest {

  @Test
  public void printNull() {
    DoubleColumn col = new DoubleColumn("testCol");
    col.append(5.0);    
    col.appendCell(null);
    col.append(3.0);
    Table table = Table.create("nullCellTable", col);
    String out = table.print();
    assertThat(out, containsString("          "));
  }

}

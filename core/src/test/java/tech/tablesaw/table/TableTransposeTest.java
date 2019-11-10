package tech.tablesaw.table;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableTransposeTest {

  @Test
  void transpose() {
    StringColumn label = StringColumn.create("label").append("row1").append("row2").append("row3");
    DoubleColumn value = DoubleColumn.create("value1").append(1.0).append(1.1).append(1.2);
    DoubleColumn value2 = DoubleColumn.create("value2").append(2.0).append(2.1).append(2.2);

    Table testTable = Table.create("Data");
    testTable.addColumns(label);
    testTable.addColumns(value);
    testTable.addColumns(value2);
    Table result = testTable.transpose();

    assertEquals("                Data                 \n" +
        " label   |  row1  |  row2  |  row3  |\n" +
        "-------------------------------------\n" +
        " value1  |     1  |   1.1  |   1.2  |\n" +
        " value2  |     2  |   2.1  |   2.2  |", result.print());
    assertEquals(testTable.print(), result.transpose().print());
  }

  @Test
  void transposeWithMissingData() {
    StringColumn label = StringColumn.create("label").append("row1").append("row2").append("row3");
    DoubleColumn value = DoubleColumn.create("value1").append(1.0).appendMissing().append(1.2);
    DoubleColumn value2 = DoubleColumn.create("value2").append(2.0).append(2.1).append(2.2);

    Table testTable = Table.create("Data");
    testTable.addColumns(label);
    testTable.addColumns(value);
    testTable.addColumns(value2);
    Table result = testTable.transpose();

    assertEquals("                Data                 \n" +
        " label   |  row1  |  row2  |  row3  |\n" +
        "-------------------------------------\n" +
        " value1  |     1  |        |   1.2  |\n" +
        " value2  |     2  |   2.1  |   2.2  |", result.print());
    assertEquals(testTable.print(), result.transpose().print());
  }

}

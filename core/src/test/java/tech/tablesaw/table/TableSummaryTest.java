package tech.tablesaw.table;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class TableSummaryTest {
  @Test
  public void summaryDoublesOnly() {
    DoubleColumn value = DoubleColumn.create("value1").append(1.0).append(1.1).append(1.2);
    DoubleColumn value2 = DoubleColumn.create("value2").append(2.0).append(2.1).append(2.2);

    Table testTable = Table.create("Data");
    testTable.addColumns(value);
    testTable.addColumns(value2);
    Table result = testTable.summary();
    assertEquals(
        "                            Data                             \n"
            + " Summary   |         value1         |        value2         |\n"
            + "-------------------------------------------------------------\n"
            + "    Count  |                     3  |                    3  |\n"
            + "      sum  |                   3.3  |                  6.3  |\n"
            + "     Mean  |                   1.1  |                  2.1  |\n"
            + "      Min  |                     1  |                    2  |\n"
            + "      Max  |                   1.2  |                  2.2  |\n"
            + "    Range  |   0.19999999999999996  |  0.20000000000000018  |\n"
            + " Variance  |  0.009999999999999995  |  0.01000000000000004  |\n"
            + " Std. Dev  |   0.09999999999999998  |   0.1000000000000002  |",
        result.print());
  }

  @Test
  public void summaryMixedTypes() {
    StringColumn label = StringColumn.create("label").append("row1").append("row2").append("row3");
    DoubleColumn value = DoubleColumn.create("value1").append(1.0).append(1.1).append(1.2);
    StringColumn label2 =
        StringColumn.create("label2").append("row1").append("row5").append("row6");
    DoubleColumn value2 = DoubleColumn.create("value2").append(2.0).append(2.1).append(2.2);

    Table testTable = Table.create("Data");
    testTable.addColumns(label);
    testTable.addColumns(value);
    testTable.addColumns(label2);
    testTable.addColumns(value2);
    Table result = testTable.summary();
    assertEquals(
        "                                       Data                                        \n"
            + "  Summary   |  label  |         value1         |  label2  |        value2         |\n"
            + "-----------------------------------------------------------------------------------\n"
            + "     Count  |      3  |                     3  |       3  |                    3  |\n"
            + "    Unique  |      3  |                        |       3  |                       |\n"
            + "       Top  |   row1  |                        |    row1  |                       |\n"
            + " Top Freq.  |      1  |                        |       1  |                       |\n"
            + "       sum  |         |                   3.3  |          |                  6.3  |\n"
            + "      Mean  |         |                   1.1  |          |                  2.1  |\n"
            + "       Min  |         |                     1  |          |                    2  |\n"
            + "       Max  |         |                   1.2  |          |                  2.2  |\n"
            + "     Range  |         |   0.19999999999999996  |          |  0.20000000000000018  |\n"
            + "  Variance  |         |  0.009999999999999995  |          |  0.01000000000000004  |\n"
            + "  Std. Dev  |         |   0.09999999999999998  |          |   0.1000000000000002  |",
        result.print());
  }
}

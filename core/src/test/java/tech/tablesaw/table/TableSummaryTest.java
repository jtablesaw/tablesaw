package tech.tablesaw.table;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import tech.tablesaw.TableAssertions;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class TableSummaryTest {

  @Test
  public void emptyTable() {
    Table testTable = Table.create("Data");
    Table summary = testTable.summary();
    TableAssertions.assertTableEquals(testTable, summary);
  }

  @Test
  public void summaryTestTwoDoubleColumnsStatistics() {
    Table testTable =
        Table.create(
            "Data",
            DoubleColumn.create("value1", 1.0, 1.1, 1.2),
            DoubleColumn.create("value2", 2.0, 2.1, 2.2));
    Table result = testTable.summary();
    assertEquals(
        "                            Data                             "
            + System.lineSeparator()
            + " Summary   |         value1         |        value2         |"
            + System.lineSeparator()
            + "-------------------------------------------------------------"
            + System.lineSeparator()
            + "    Count  |                     3  |                    3  |"
            + System.lineSeparator()
            + "      sum  |                   3.3  |                  6.3  |"
            + System.lineSeparator()
            + "     Mean  |                   1.1  |                  2.1  |"
            + System.lineSeparator()
            + "      Min  |                     1  |                    2  |"
            + System.lineSeparator()
            + "      Max  |                   1.2  |                  2.2  |"
            + System.lineSeparator()
            + "    Range  |   0.19999999999999996  |  0.20000000000000018  |"
            + System.lineSeparator()
            + " Variance  |  0.009999999999999995  |  0.01000000000000004  |"
            + System.lineSeparator()
            + " Std. Dev  |   0.09999999999999998  |   0.1000000000000002  |",
        result.print());
  }

  @Test
  public void summaryMixedTypes() {
    Table testTable =
        Table.create(
            "Data",
            StringColumn.create("label", "yellow", "yellow", "green"),
            DoubleColumn.create("value1", 1.0, 1.1, 1.2),
            BooleanColumn.create("truthy", true, false, true),
            DateColumn.create(
                "dates",
                new LocalDate[] {
                  LocalDate.of(2001, 1, 1), LocalDate.of(2002, 1, 1), LocalDate.of(2001, 1, 1)
                }));
    Table result = testTable.summary();
    assertEquals(
        "                                   Data                                    "
            + System.lineSeparator()
            + "  Summary   |  label   |         value1         |  truthy  |    dates     |"
            + System.lineSeparator()
            + "---------------------------------------------------------------------------"
            + System.lineSeparator()
            + "     Count  |       3  |                     3  |          |           3  |"
            + System.lineSeparator()
            + "    Unique  |       2  |                        |          |              |"
            + System.lineSeparator()
            + "       Top  |  yellow  |                        |          |              |"
            + System.lineSeparator()
            + " Top Freq.  |       2  |                        |          |              |"
            + System.lineSeparator()
            + "       sum  |          |                   3.3  |          |              |"
            + System.lineSeparator()
            + "      Mean  |          |                   1.1  |          |              |"
            + System.lineSeparator()
            + "       Min  |          |                     1  |          |              |"
            + System.lineSeparator()
            + "       Max  |          |                   1.2  |          |              |"
            + System.lineSeparator()
            + "     Range  |          |   0.19999999999999996  |          |              |"
            + System.lineSeparator()
            + "  Variance  |          |  0.009999999999999995  |          |              |"
            + System.lineSeparator()
            + "  Std. Dev  |          |   0.09999999999999998  |          |              |"
            + System.lineSeparator()
            + "     false  |          |                        |       1  |              |"
            + System.lineSeparator()
            + "      true  |          |                        |       2  |              |"
            + System.lineSeparator()
            + "   Missing  |          |                        |          |           0  |"
            + System.lineSeparator()
            + "  Earliest  |          |                        |          |  2001-01-01  |"
            + System.lineSeparator()
            + "    Latest  |          |                        |          |  2002-01-01  |",
        result.print());
  }
}

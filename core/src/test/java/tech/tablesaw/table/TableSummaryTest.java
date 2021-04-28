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

  private static final String LINE_END = System.lineSeparator();

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
            + LINE_END
            + " Summary   |         value1         |        value2         |"
            + LINE_END
            + "-------------------------------------------------------------"
            + LINE_END
            + "    Count  |                     3  |                    3  |"
            + LINE_END
            + "      sum  |                   3.3  |                  6.3  |"
            + LINE_END
            + "     Mean  |                   1.1  |                  2.1  |"
            + LINE_END
            + "      Min  |                     1  |                    2  |"
            + LINE_END
            + "      Max  |                   1.2  |                  2.2  |"
            + LINE_END
            + "    Range  |   0.19999999999999996  |  0.20000000000000018  |"
            + LINE_END
            + " Variance  |  0.009999999999999995  |  0.01000000000000004  |"
            + LINE_END
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
            + LINE_END
            + "  Summary   |  label   |         value1         |  truthy  |    dates     |"
            + LINE_END
            + "---------------------------------------------------------------------------"
            + LINE_END
            + "     Count  |       3  |                     3  |          |           3  |"
            + LINE_END
            + "    Unique  |       2  |                        |          |              |"
            + LINE_END
            + "       Top  |  yellow  |                        |          |              |"
            + LINE_END
            + " Top Freq.  |       2  |                        |          |              |"
            + LINE_END
            + "       sum  |          |                   3.3  |          |              |"
            + LINE_END
            + "      Mean  |          |                   1.1  |          |              |"
            + LINE_END
            + "       Min  |          |                     1  |          |              |"
            + LINE_END
            + "       Max  |          |                   1.2  |          |              |"
            + LINE_END
            + "     Range  |          |   0.19999999999999996  |          |              |"
            + LINE_END
            + "  Variance  |          |  0.009999999999999995  |          |              |"
            + LINE_END
            + "  Std. Dev  |          |   0.09999999999999998  |          |              |"
            + LINE_END
            + "     false  |          |                        |       1  |              |"
            + LINE_END
            + "      true  |          |                        |       2  |              |"
            + LINE_END
            + "   Missing  |          |                        |          |           0  |"
            + LINE_END
            + "  Earliest  |          |                        |          |  2001-01-01  |"
            + LINE_END
            + "    Latest  |          |                        |          |  2002-01-01  |",
        result.print());
  }
}

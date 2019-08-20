package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

class AnalyticQueryTest {

  private static final Consumer<Iterable<Row>> consumer1 = iterable -> {};

  @Test
  public void testToSqlString() {
    Table table = Table.create("table1", IntColumn.create("sales"));

    AnalyticQuery query =
        AnalyticQuery.query()
            .from(table)
            .partitionBy("product", "region")
            .orderBy("sales")
            .rowsBetween()
            .unboundedPreceding()
            .andUnBoundedFollowing()
            .sum("sales")
            .as("sumSales")
            .build();

    String expected =
        "SELECT"
            + System.lineSeparator()
            + "SUM(sales) OVER w1 AS sumSales"
            + System.lineSeparator()
            + "FROM table1"
            + System.lineSeparator()
            + "Window w1 AS ("
            + System.lineSeparator()
            + "PARTITION BY product, region"
            + System.lineSeparator()
            + "ORDER BY sales ASC"
            + System.lineSeparator()
            + "ROWS BETWEEN UNBOUNDED_PRECEDING AND UNBOUNDED_FOLLOWING);";

    assertEquals(expected, query.toSqlString());
  }

  @Test
  public void toSqlStringQuick() {
    AnalyticQuery query =
        AnalyticQuery.quickQuery()
            .from(Table.create("sales"))
            .rowsBetween()
            .currentRow()
            .andFollowing(1)
            .max("sales")
            .as("salesSum")
            .build();

    String expectd =
        "SELECT\n"
            + "MAX(sales) OVER w1 AS salesSum\n"
            + "FROM sales\n"
            + "Window w1 AS (\n"
            + "ROWS BETWEEN CURRENT_ROW AND 1 FOLLOWING);";

    assertEquals(expectd, query.toSqlString());
  }

  @Test
  public void toSqlStringNumbering() {
    AnalyticQuery query =
        AnalyticQuery.numberingQuery()
            .from(Table.create("myTable", IntColumn.create("date")))
            .partitionBy()
            .orderBy("date")
            .rank()
            .as("myRank")
            .build();

    String expectd =
        "SELECT\n"
            + "RANK() OVER w1 AS myRank\n"
            + "FROM myTable\n"
            + "Window w1 AS (\n"
            + "ORDER BY date ASC);";

    assertEquals(expectd, query.toSqlString());
  }
}

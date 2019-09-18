package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

class AnalyticQueryTest {

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

    assertEquals(expected, query.toSqlLikeString());
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

    assertEquals(expectd, query.toSqlLikeString());
  }

  @Test
  public void toSqlStringNumbering() {
    AnalyticQuery query =
        AnalyticQuery.numberingQuery()
            .from(Table.create("myTable", IntColumn.create("date"), IntColumn.create("region")))
            .partitionBy()
            .orderBy("date", "region")
            .rank()
            .as("myRank")
            .build();

    String expectd =
        "SELECT\n"
            + "RANK() OVER w1 AS myRank\n"
            + "FROM myTable\n"
            + "Window w1 AS (\n"
            + "ORDER BY date ASC, region ASC);";

    assertEquals(expectd, query.toSqlLikeString());
  }

  @Test
  public void executeInPlaceNumbering() {
    Table table = Table.create("table", StringColumn.create("col1", new String[] {}));

    AnalyticQuery.numberingQuery()
        .from(table)
        .partitionBy()
        .orderBy("col1")
        .rowNumber()
        .as("rowNumber")
        .rank()
        .as("rank")
        .denseRank()
        .as("denseRank")
        .executeInPlace();

    assertEquals(ImmutableList.of("col1", "rowNumber", "rank", "denseRank"), table.columnNames());
  }

  @Test
  public void executeInPlaceAnalytic() {
    Table table = Table.create("table", DoubleColumn.create("col1", new Double[] {}));

    AnalyticQuery.query()
        .from(table)
        .partitionBy()
        .orderBy("col1")
        .rowsBetween()
        .unboundedPreceding()
        .andUnBoundedFollowing()
        .sum("col1")
        .as("sum")
        .max("col1")
        .as("max")
        .executeInPlace();

    assertEquals(ImmutableList.of("col1", "sum", "max"), table.columnNames());
  }

  @Test
  public void executeInPlaceWithDuplicateColumnsThrows() {
    Table table = Table.create("myTable", DoubleColumn.create("col1", new Double[] {}));

    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                AnalyticQuery.query()
                    .from(table)
                    .partitionBy()
                    .orderBy("col1")
                    .rowsBetween()
                    .unboundedPreceding()
                    .andUnBoundedFollowing()
                    .sum("col1")
                    .as("col1")
                    .executeInPlace());

    assertTrue(thrown.getMessage().contains("Cannot add column with duplicate name"));
  }
}

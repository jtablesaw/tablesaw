package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.analytic.AnalyticQuery.Order.ASC;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

class AnalyticQueryTest {

  private static final Consumer<Iterable<Row>> consumer1 = iterable -> {
  };

  @Test
  public void testToSqlString() {
    Table table = Table.create("table1");

    AnalyticQuery query = AnalyticQuery.from(table)
      .partitionBy("product", "region")
      .orderBy("sales", ASC)
      .rowsBetween().unboundedPreceding().andUnBoundedFollowing()
      .rank("sales").as("salesRank")
      .build();

    String expected = "SELECT"
      + System.lineSeparator()
      + "RANK(sales) OVER w1 AS salesRank"
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
  public void testToSqlStringEmptyWindowSpecification() {
    AnalyticQuery query = AnalyticQuery.from(Table.create("house"))
      .rowsBetween().currentRow().andFollowing(1)
      .max("col1").as("house")
      .build();

    String expectd = "SELECT\n"
      + "MAX(col1) OVER w1 AS house\n"
      + "FROM house\n"
      + "Window w1 AS (\n"
      + "ROWS BETWEEN CURRENT_ROW AND 1 FOLLOWING);";

    assertEquals(expectd, query.toSqlString());
  }

  @Test
  public void addConsumers() {
    AnalyticQuery analyticQuery = AnalyticQuery.from(Table.create("t1"))
      .rowsBetween().unboundedPreceding().andUnBoundedFollowing()
      .apply(consumer1)
      .apply(consumer1)
      .build();

    assertEquals(2, analyticQuery.getConsumers().size());
  }

}
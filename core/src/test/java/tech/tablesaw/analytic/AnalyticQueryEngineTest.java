package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;

class AnalyticQueryEngineTest {

  private Table source;

  @BeforeEach
  // TODO Consider changing to BeforeAll if tests are slow.
  public void setUp() throws Exception {
    // Contains columns calculated by a SQL engine that our implementation should match exactly.
    source = Table.read().csv("../data/bush_sql_partitionby_who_orderby_date_sum_approval.csv");
  }

  private double[] sourceColumnAsDouble(String columnName) {
    return source.intColumn(columnName).asDoubleArray();
  }

  @Test
  public void testSliding() {
    String destinationColumnName = "dest";
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().preceding(5).andPreceding(3)
      .sum("approval").as(destinationColumnName)
      .build();


    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("5preceding_and_3preceding");
    double[] actual = result.doubleColumn(destinationColumnName).asDoubleArray();

    System.out.println(Arrays.toString(expected));
    System.out.println(Arrays.toString(actual));

    /*
     * This does not work because TableSliceGroup sorts makes a copy of the underlying table and sorts it.
     * This makes it so the indexes no longer match. Solutions.
     *
     * 1) Stop TableSliceGroup from sorting.
     *
     */


    assertArrayEquals(expected, actual);
  }

}
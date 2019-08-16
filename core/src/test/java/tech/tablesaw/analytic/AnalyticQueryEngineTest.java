package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

class AnalyticQueryEngineTest {

  private static Table source;

  //Before all is a few seconds faster.
  @BeforeAll
  public static void setUp() throws Exception {
    // Reference implementation generated from BigQuery.
    source = Table.read().csv("../data/bush_analytic_reference_implementation.csv");
  }

  private double[] sourceColumnAsDouble(String columnName) {
    return source.intColumn(columnName).asDoubleArray();
  }

  @Test
  public void testInvalidSourceColumn() {
    String destinationColumnName = "dest";
    Table table = Table.create("table",
      StringColumn.create("col1", new String[]{"bad"}));

    AnalyticQuery query = AnalyticQuery.from(table)
      .rowsBetween().preceding(1).andCurrentRow()
      .sum("col1").as(destinationColumnName)
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Throwable thrown = assertThrows(IllegalArgumentException.class, queryEngine::execute);
    assertTrue(thrown.getMessage().contains("Function: SUM Is not compatible with column type: STRING"));
  }

  @Test
  public void testBasic() {
    Table table = Table.create("table",
      DoubleColumn.create("col1", new double[]{2, 1, 1, 1, 1, 1, 1}));

    AnalyticQuery query = AnalyticQuery.from(table)
      .rowsBetween().preceding(3).andUnBoundedFollowing()
      .sum("col1").as("sum")
      .max("col1").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = new double[]{8, 8, 8, 8, 6, 5, 4};
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = new double[]{2, 2, 2, 2, 1, 1, 1};
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testMissingValues() {
    String destinationColumnName = "dest";
    Table table = Table.create("table",
      DoubleColumn.create("col1", new double[]{1, 1, 1, Double.NaN, Double.NaN, Double.NaN, 1, 1, 1}));

    AnalyticQuery query = AnalyticQuery.from(table)
      .rowsBetween().preceding(1).andCurrentRow()
      .sum("col1").as(destinationColumnName)
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = new double[]{1, 2, 2, 1, Double.NaN, Double.NaN, 1, 2, 2};
    double[] actual = result.doubleColumn(destinationColumnName).asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unoundedPrecedingAnd5Preceding() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().unboundedPreceding().andPreceding(5)
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_unboundedpreceding_and_5preceding");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_unboundedpreceding_and_5preceding");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unboundedPrecedingAndCurrentRow() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().unboundedPreceding().andCurrentRow()
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_unboundedpreceding_and_currentrow");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_unboundedpreceding_and_currentrow");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unboundedPrecedingAnd5Following() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().unboundedPreceding().andFollowing(5)
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_unboundedpreceding_and_5following");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_unboundedpreceding_and_5following");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unboundedPrecedingAndUnboundedFollowing() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().unboundedPreceding().andUnBoundedFollowing()
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_unboundedpreceding_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_unboundedpreceding_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAnd3Preceding() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().preceding(5).andPreceding(3)
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_5preceding_and_3preceding");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_5preceding_and_3preceding");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAndCurrentRow() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().preceding(5).andCurrentRow()
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_5preceding_and_currentrow");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_5preceding_and_currentrow");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAnd5Following() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().preceding(5).andFollowing(5)
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_5preceding_and_5following");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_5preceding_and_5following");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAndUnboundedFollowing() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().preceding(5).andUnBoundedFollowing()
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_5preceding_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_5preceding_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void currentRowAndUnboundedFollowing() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().currentRow().andUnBoundedFollowing()
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_currentrow_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_currentrow_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fiveFollowingAnd8Following() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().following(5).andFollowing(8)
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_5following_and_8following");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_5following_and_8following");
    actual = result.doubleColumn("max").asDoubleArray();

    assertArrayEquals(expected, actual);
  }

  @Test
  public void fiveFollowingAndUnboundedFollowing() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().following(5).andUnBoundedFollowing()
      .sum("approval").as("sum")
      .max("approval").as("max")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = sourceColumnAsDouble("sum_5following_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = sourceColumnAsDouble("max_5following_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void numberingFunctionReferenceImplementation() {
    AnalyticQuery query = AnalyticQuery.from(source)
      .partitionBy("who")
      .orderBy("date")
      .rowsBetween().unboundedPreceding().andUnBoundedFollowing()
      .rowNumber().as("rowNumber")
      .rank().as("rank")
      .denseRank().as("denseRank")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    assertArrayEquals(sourceColumnAsDouble("row_number"),
      result.intColumn("rowNumber").asDoubleArray());
    assertArrayEquals(sourceColumnAsDouble("rank"),
      result.intColumn("rank").asDoubleArray());
    assertArrayEquals(sourceColumnAsDouble("dense_rank"),
      result.intColumn("denseRank").asDoubleArray());
  }

  @Test
  public void numberingFunctionsWithStrings() {
    Table table = Table.create("table",
      StringColumn.create("col1", new String[]{"A", "B", "B", "C", "C", "C", "D"}));

    AnalyticQuery query = AnalyticQuery.from(table)
      .partitionBy().orderBy("col1").rowsBetween().unboundedPreceding().andUnBoundedFollowing()
      .rowNumber().as("rowNumber")
      .rank().as("rank")
      .denseRank().as("denseRank")
      .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    assertArrayEquals(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0},
      result.intColumn("rowNumber").asDoubleArray());
    assertArrayEquals(new double[]{1.0, 2.0, 2.0, 4.0, 4.0, 4.0, 7.0},
      result.intColumn("rank").asDoubleArray());
    assertArrayEquals(new double[]{1.0, 2.0, 2.0, 3.0, 3.0, 3.0, 4.0},
      result.intColumn("denseRank").asDoubleArray());
  }
}

package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

class AnalyticQueryEngineTest {

  private static Table referenceImplementation;

  // Before runs tests a few seconds faster.
  @BeforeAll
  public static void setUp() throws Exception {
    // Reference implementation generated from BigQuery.
    referenceImplementation =
        Table.read().csv("../data/bush_analytic_reference_implementation.csv");
  }

  private double[] intSourceColumnAsDoubleArray(String columnName) {
    return referenceImplementation.intColumn(columnName).asDoubleArray();
  }

  private double[] doubleSourceColumnAsDoubleArray(String columnName) {
    return referenceImplementation.doubleColumn(columnName).asDoubleArray();
  }

  @Test
  public void testInvalidSourceColumn() {
    String destinationColumnName = "dest";
    Table table = Table.create("table", StringColumn.create("col1", new String[] {"bad"}));

    AnalyticQuery query =
        AnalyticQuery.quickQuery()
            .from(table)
            .rowsBetween()
            .preceding(1)
            .andCurrentRow()
            .sum("col1")
            .as(destinationColumnName)
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Throwable thrown = assertThrows(IllegalArgumentException.class, queryEngine::execute);
    assertTrue(
        thrown.getMessage().contains("Function: SUM Is not compatible with column type: STRING"));
  }

  @Test
  public void testBasic() {
    Table table =
        Table.create("table", DoubleColumn.create("col1", new double[] {2, 1, 1, 1, 1, 1, 1}));

    AnalyticQuery query =
        AnalyticQuery.quickQuery()
            .from(table)
            .rowsBetween()
            .preceding(3)
            .andUnBoundedFollowing()
            .sum("col1")
            .as("sum")
            .max("col1")
            .as("max")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = new double[] {8, 8, 8, 8, 6, 5, 4};
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = new double[] {2, 2, 2, 2, 1, 1, 1};
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testMissingValues() {
    String destinationColumnName = "dest";
    Table table =
        Table.create(
            "table",
            DoubleColumn.create(
                "col1", new double[] {1, 1, 1, Double.NaN, Double.NaN, Double.NaN, 1, 1, 1}));

    AnalyticQuery query =
        AnalyticQuery.quickQuery()
            .from(table)
            .rowsBetween()
            .preceding(1)
            .andCurrentRow()
            .sum("col1")
            .as(destinationColumnName)
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = new double[] {1, 2, 2, 1, Double.NaN, Double.NaN, 1, 2, 2};
    double[] actual = result.doubleColumn(destinationColumnName).asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unoundedPrecedingAnd5Preceding() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .unboundedPreceding()
            .andPreceding(5)
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_unboundedpreceding_and_5preceding");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_unboundedpreceding_and_5preceding");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_unboundedpreceding_and_5preceding");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_unboundedpreceding_and_5preceding");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_unboundedpreceding_and_5preceding");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unboundedPrecedingAndCurrentRow() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .unboundedPreceding()
            .andCurrentRow()
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_unboundedpreceding_and_currentrow");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_unboundedpreceding_and_currentrow");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_unboundedpreceding_and_currentrow");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_unboundedpreceding_and_currentrow");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_unboundedpreceding_and_currentrow");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unboundedPrecedingAnd5Following() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .unboundedPreceding()
            .andFollowing(5)
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_unboundedpreceding_and_5following");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_unboundedpreceding_and_5following");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_unboundedpreceding_and_5following");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_unboundedpreceding_and_5following");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_unboundedpreceding_and_5following");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void unboundedPrecedingAndUnboundedFollowing() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .unboundedPreceding()
            .andUnBoundedFollowing()
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected =
        intSourceColumnAsDoubleArray("sum_unboundedpreceding_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_unboundedpreceding_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_unboundedpreceding_and_unboundedfollowing");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_unboundedpreceding_and_unboundedfollowing");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_unboundedpreceding_and_unboundedfollowing");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAnd3Preceding() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .preceding(5)
            .andPreceding(3)
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_5preceding_and_3preceding");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_5preceding_and_3preceding");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_5preceding_and_3preceding");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_5preceding_and_3preceding");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_5preceding_and_3preceding");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAndCurrentRow() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .preceding(5)
            .andCurrentRow()
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_5preceding_and_currentrow");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_5preceding_and_currentrow");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_5preceding_and_currentrow");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_5preceding_and_currentrow");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_5preceding_and_currentrow");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAnd5Following() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .preceding(5)
            .andFollowing(5)
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_5preceding_and_5following");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_5preceding_and_5following");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_5preceding_and_5following");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_5preceding_and_5following");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_5preceding_and_5following");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fivePrecedingAndUnboundedFollowing() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .preceding(5)
            .andUnBoundedFollowing()
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_5preceding_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_5preceding_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_5preceding_and_unboundedfollowing");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_5preceding_and_unboundedfollowing");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_5preceding_and_unboundedfollowing");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void currentRowAndUnboundedFollowing() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .currentRow()
            .andUnBoundedFollowing()
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_currentrow_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_currentrow_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_currentrow_and_unboundedfollowing");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_currentrow_and_unboundedfollowing");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_currentrow_and_unboundedfollowing");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fiveFollowingAnd8Following() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .following(5)
            .andFollowing(8)
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_5following_and_8following");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_5following_and_8following");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_5following_and_8following");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_5following_and_8following");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_5following_and_8following");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void fiveFollowingAndUnboundedFollowing() {
    AnalyticQuery query =
        AnalyticQuery.query()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowsBetween()
            .following(5)
            .andUnBoundedFollowing()
            .sum("approval")
            .as("sum")
            .max("approval")
            .as("max")
            .min("approval")
            .as("min")
            .mean("approval")
            .as("mean")
            .count("approval")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    double[] expected = intSourceColumnAsDoubleArray("sum_5following_and_unboundedfollowing");
    double[] actual = result.doubleColumn("sum").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("max_5following_and_unboundedfollowing");
    actual = result.doubleColumn("max").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("min_5following_and_unboundedfollowing");
    actual = result.doubleColumn("min").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = doubleSourceColumnAsDoubleArray("mean_5following_and_unboundedfollowing");
    actual = result.doubleColumn("mean").asDoubleArray();
    assertArrayEquals(expected, actual);

    expected = intSourceColumnAsDoubleArray("count_5following_and_unboundedfollowing");
    actual = result.intColumn("count").asDoubleArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void countWithStrings() {
    Table table =
        Table.create(
            "table",
            StringColumn.create("col1", new String[] {"A", "B", null, "C", "C", "C", "D"}));

    AnalyticQuery query =
        AnalyticQuery.quickQuery()
            .from(table)
            .rowsBetween()
            .unboundedPreceding()
            .andCurrentRow()
            .count("col1")
            .as("count")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    assertEquals(ImmutableList.of(1, 2, 2, 3, 4, 5, 6), result.intColumn("count").asList());
  }

  @Test
  public void numberingFunctionReferenceImplementation() {
    AnalyticQuery query =
        AnalyticQuery.numberingQuery()
            .from(referenceImplementation)
            .partitionBy("who")
            .orderBy("date")
            .rowNumber()
            .as("rowNumber")
            .rank()
            .as("rank")
            .denseRank()
            .as("denseRank")
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    assertArrayEquals(
        intSourceColumnAsDoubleArray("row_number"), result.intColumn("rowNumber").asDoubleArray());
    assertArrayEquals(
        intSourceColumnAsDoubleArray("rank"), result.intColumn("rank").asDoubleArray());
    assertArrayEquals(
        intSourceColumnAsDoubleArray("dense_rank"), result.intColumn("denseRank").asDoubleArray());
  }

  @Test
  public void numberingFunctionsWithStrings() {
    Table table =
        Table.create(
            "table", StringColumn.create("col1", new String[] {"A", "B", "B", "C", "C", "C", "D"}));

    AnalyticQuery query =
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
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    assertArrayEquals(
        new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0},
        result.intColumn("rowNumber").asDoubleArray());
    assertArrayEquals(
        new double[] {1.0, 2.0, 2.0, 4.0, 4.0, 4.0, 7.0}, result.intColumn("rank").asDoubleArray());
    assertArrayEquals(
        new double[] {1.0, 2.0, 2.0, 3.0, 3.0, 3.0, 4.0},
        result.intColumn("denseRank").asDoubleArray());
  }

  @Test
  public void resultColumnOrderSameAsSpecifiedInQuery() {
    Table table = Table.create("table", StringColumn.create("col1", new String[] {}));

    AnalyticQuery query =
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
            .build();

    AnalyticQueryEngine queryEngine = AnalyticQueryEngine.create(query);
    Table result = queryEngine.execute();

    assertEquals(ImmutableList.of("rowNumber", "rank", "denseRank"), result.columnNames());
  }
}

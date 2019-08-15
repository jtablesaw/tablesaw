package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.analytic.AnalyticAggregateFunctions.MaxFunctions;
import tech.tablesaw.analytic.AnalyticAggregateFunctions.SumFunctions;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

class AnalyticAggregateFunctionsTest {

  private Table table;

  @BeforeEach
  public void setUp() throws Exception {
    table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
  }

  @Test
  public void testSumFixed() {
    AggregateFunction<Integer, Double> function = new SumFunctions<Integer>().fixedFunction();
    function.addAllRightMost(ImmutableList.of(10, 10, 10));
    assertEquals(30, function.getValue());
  }

  @Test
  public void testSumSliding() {
    AggregateFunction<Integer, Double> function = new SumFunctions<Integer>().slidingFunction();
    function.addAllRightMost(ImmutableList.of(10, 10, 10));
    function.removeLeftMost();
    function.addRightMost(100);
    assertEquals(120, function.getValue());
  }

  @Test
  public void testMaxFixed() {
    AggregateFunction<Integer, Double> function = new MaxFunctions<Integer>().fixedFunction();
    function.addAllRightMost(ImmutableList.of(11, 10, 9));
    assertEquals(11, function.getValue());
  }

  @Test
  public void testMaxSliding() {
    AggregateFunction<Integer, Double> function = new MaxFunctions<Integer>().slidingFunction();
    function.addAllRightMost(ImmutableList.of(100, 10, 9));
    function.removeLeftMost();
    function.addRightMost(3);
    assertEquals(10, function.getValue());
  }

}
package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.analytic.AggregateFunctionImplementations.MaxFunctions;
import tech.tablesaw.analytic.AggregateFunctionImplementations.SumFunctions;
import tech.tablesaw.analytic.NumericAggregateFunction;
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
    NumericAggregateFunction<Integer> function = new SumFunctions<Integer>().fixedFunction();
    function.setWindow(ImmutableList.of(10, 10, 10));
    assertEquals(30, function.getValue());
  }

  @Test
  public void testSumSliding() {
    NumericAggregateFunction<Integer> function = new SumFunctions<Integer>().slidingFunction();
    function.setWindow(ImmutableList.of(10, 10, 10));
    function.shiftLeft();
    function.shiftRight(100);
    assertEquals(120, function.getValue());
  }
  @Test

  public void testSumGrowing() {
    NumericAggregateFunction<Integer> function = new SumFunctions<Integer>().growingFunction();
    function.setWindow(ImmutableList.of(10, 10, 10));
    function.shiftLeft();
    function.shiftRight(100);
    assertEquals(130, function.getValue());
  }

  @Test
  public void testMaxFixed() {
    NumericAggregateFunction<Integer> function = new MaxFunctions<Integer>().fixedFunction();
    function.setWindow(ImmutableList.of(11, 10, 9));
    assertEquals(11, function.getValue());
  }

  @Test
  public void testMaxSliding() {
    NumericAggregateFunction<Integer> function = new MaxFunctions<Integer>().slidingFunction();
    function.setWindow(ImmutableList.of(100, 10, 9));
    function.shiftLeft();
    function.shiftRight(3);
    assertEquals(10, function.getValue());
  }
  @Test

  public void testMaxGrowing() {
    NumericAggregateFunction<Integer> function = new MaxFunctions<Integer>().growingFunction();
    function.setWindow(ImmutableList.of(100, 10, 10));
    function.shiftLeft();
    function.shiftRight(30);
    assertEquals(100, function.getValue());
  }

}
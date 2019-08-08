package tech.tablesaw.analytic;

import java.util.function.Consumer;
import tech.tablesaw.analytic.AnalyticQuery.Order;
import tech.tablesaw.analytic.WindowSpecification.OrderPair;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public interface AnalyticQuerySteps {

  interface AnalyticFunctions {

    // Numbering Functions for Convenience.
    NameStep rowNumber(String columnName);
    // Requries order in SQL
    NameStep rank(String columnName);
    // Requires order in SQL
    NameStep denseRank(String columnName);

    // Regular Analytic Aggregates for Convenience.
    NameStep sum(String columnName);
    NameStep mean(String columnName);
    NameStep max(String columnName);
    NameStep min(String columnName);
    NameStep count(String columnName);

    // Add Consumer for multiColumn transformations pattern.
    AddAnalyticFunctionWithExecute apply(Consumer<Iterable<Row>> consumer);
    AddAnalyticFunctionWithExecute apply(Iterable<Consumer<Iterable<Row>>> consumers);
  }

  interface StartStep extends PartitionStep, DefineWindow {}

  interface PartitionStep {
    OrderStep partitionBy(String... columns);
  }

  /**
   * ASC puts nulls last.
   * DESC puts nulls last.
   */
  interface OrderStep {

    // Defaults to ASC
    DefineWindow orderBy(String column);

    DefineWindow orderBy(String column, Order strategy);

    DefineWindow orderBy(String c1, Order s1, String c2, Order s2);

    DefineWindow orderBy(String c1, Order s1, String c2, Order s2,
      String c3, Order s3);

    DefineWindow orderBy(String c1, Order s1, String c2, Order s2,
      String c3, Order s3, String c4, Order o4);

    DefineWindow orderBy(String c1, Order s1, String c2, Order s2,
      String c3, Order s3, String c4, Order o4, String c5, Order o5);

    DefineWindow orderBy(OrderPair... orderPairs);
  }


  interface DefineWindow {
    WindowStart rowsBetween();
  }

  interface WindowStart {
    WindowEndOptionOne unboundedPreceding();

    WindowEndOptionOne preceding(int nRows);

    WindowEndOptionTwo currentRow();

    WindowEndOptionTwo following(int nRows);
  }

  interface WindowEndOptionOne {
    AddAnalyticFunctions andPreceding(int nRows);

    AddAnalyticFunctions andCurrentRow();

    AddAnalyticFunctions andFollowing(int nRows);

    AddAnalyticFunctions andUnBoundedFollowing();
  }

  interface WindowEndOptionTwo {
    AddAnalyticFunctions andFollowing(int nRows);

    AddAnalyticFunctions andUnBoundedFollowing();
  }

  interface NameStep {
    AddAnalyticFunctionWithExecute as(String columnName);
  }

  interface AddAnalyticFunctions extends AnalyticFunctions {}

  interface AddAnalyticFunctionWithExecute extends AnalyticFunctions {

    AnalyticQuery build();
    // Creates a new table.
    Table execute();

    // Adds Columns to existing table.
    void executeInPlace();
  }

}

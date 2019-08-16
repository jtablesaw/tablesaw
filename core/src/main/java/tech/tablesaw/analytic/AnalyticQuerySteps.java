package tech.tablesaw.analytic;

import tech.tablesaw.analytic.AnalyticQuery.Order;
import tech.tablesaw.analytic.WindowSpecification.OrderPair;
import tech.tablesaw.api.Table;

public interface AnalyticQuerySteps {

  interface AnalyticFunctions {

    // Require Order.
    NameStep rowNumber();
    NameStep rank();
    NameStep denseRank();

    NameStep sum(String columnName);
    NameStep mean(String columnName);
    NameStep max(String columnName);
    NameStep min(String columnName);
    NameStep count(String columnName);
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

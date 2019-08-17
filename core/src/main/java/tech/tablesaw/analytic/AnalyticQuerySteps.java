package tech.tablesaw.analytic;

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
    DefineWindow orderBy(String... columnNames);
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

package tech.tablesaw.analytic;

import tech.tablesaw.api.Table;

public interface AnalyticQuerySteps {

  interface FullAnalyticQuerySteps {
    interface FromStep extends From<FullAnalyticQuerySteps.PartitionStep> {}

    interface PartitionStep extends Partition<FullAnalyticQuerySteps.OrderOptionalStep> {}

    interface OrderOptionalStep extends OrderOptional<DefineWindow> {}
  }

  interface NumberingQuerySteps {
    interface FromStep extends From<NumberingQuerySteps.PartitionStep> {}

    interface PartitionStep extends Partition<NumberingQuerySteps.OrderRequiredStep> {}

    interface OrderRequiredStep
        extends AnalyticQuerySteps.OrderRequiredStep<AddNumberingFunction> {}
  }

  interface QuickQuerySteps {
    interface FromStep extends From<DefineWindow> {}
  }

  interface From<Next> {
    Next from(Table table);
  }

  interface Partition<Next> {
    Next partitionBy(String... columnNames);
  }

  interface OrderOptional<Next> {
    Next orderBy(String... columnNames);
  }

  interface OrderRequiredStep<Next> {
    Next orderBy(String columnOne, String... rest);
  }

  interface AnalyticFunctions {
    NameStepAggregate sum(String columnName);

    NameStepAggregate mean(String columnName);

    NameStepAggregate max(String columnName);

    NameStepAggregate min(String columnName);

    NameStepAggregate count(String columnName);
  }

  interface NumberingFunctions {
    // Require Order.
    NameStepNumbering rowNumber();

    NameStepNumbering rank();

    NameStepNumbering denseRank();
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
    AddAggregateFunctions andPreceding(int nRows);

    AddAggregateFunctions andCurrentRow();

    AddAggregateFunctions andFollowing(int nRows);

    AddAggregateFunctions andUnBoundedFollowing();
  }

  interface WindowEndOptionTwo {
    AddAggregateFunctions andFollowing(int nRows);

    AddAggregateFunctions andUnBoundedFollowing();
  }

  interface NameStepAggregate {
    AddAggregateFunctionsWithExecute as(String columnName);
  }

  interface NameStepNumbering {
    AddNumberingFunctionWithExecute as(String columnName);
  }

  interface Execute {
    AnalyticQuery build();
    // Creates a new table.
    Table execute();

    // Adds Columns to existing table.
    void executeInPlace();
  }

  interface AddAggregateFunctions extends AnalyticFunctions {}

  interface AddAggregateFunctionsWithExecute extends AnalyticFunctions, Execute {}

  interface AddNumberingFunction extends NumberingFunctions {}

  interface AddNumberingFunctionWithExecute extends NumberingFunctions, Execute {}
}

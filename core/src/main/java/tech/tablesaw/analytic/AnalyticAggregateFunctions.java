package tech.tablesaw.analytic;


import java.util.Optional;
import tech.tablesaw.analytic.AggregateFunctionImplementations.MaxFunctions;
import tech.tablesaw.analytic.AggregateFunctionImplementations.SumFunctions;

/**
 * List of Analytic Aggregate functions that can be used in an Analytic Over query.
 */
enum AnalyticAggregateFunctions {
  SUM(new SumFunctions<>()),
  MEAN(null),
  MAX(new MaxFunctions<>()),
  MIN(null),
  COUNT(null);

  private final WindowDependentAggregateFunction<?> implementation;

  AnalyticAggregateFunctions(WindowDependentAggregateFunction<?> implementation) {
    this.implementation = implementation;
  }

  public WindowDependentAggregateFunction<?> getImplementation() {
    return Optional.ofNullable(this.implementation).orElseThrow(UnsupportedOperationException::new);
  }
}

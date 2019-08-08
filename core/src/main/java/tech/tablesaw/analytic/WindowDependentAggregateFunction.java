package tech.tablesaw.analytic;


import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;

abstract class WindowDependentAggregateFunction<T extends Number> {

  abstract NumericAggregateFunction<T> fixedFunction();
  abstract NumericAggregateFunction<T> growingFunction();
  abstract NumericAggregateFunction<T> slidingFunction();

  NumericAggregateFunction<T> functionFor(WindowGrowthType growthType) {
    switch (growthType) {
      case FIXED:
        return fixedFunction();
      case GROWING:
        growingFunction();
      case SLIDING:
        return slidingFunction();
    }
    throw new UnsupportedOperationException();
  }
}

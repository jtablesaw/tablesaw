package tech.tablesaw.analytic;


import java.util.ArrayDeque;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;
import tech.tablesaw.api.ColumnType;

/**
 * Analytic Aggregate functions.
 *
 * These require different implementations from regular aggregate functions because they can be called up to n times per
 * table can can take O(n) per call for a total of O(n^2). A table with the window definition ROWS BETWEEN UNBOUNDED
 * PRECEDING AND CURRENT ROW would be O(n^2).
 *
 * If at least one side of the window is unbounded the window is considered fixed. Most analytic functions
 * with fixed windows can be implemented in O(n). For example, calculating a sum over the window UNBOUNDED PRECEDING AND
 * CURRENT ROW can be done by simply summing the numbers. Windows ending in UNBOUNDED FOLLOWING can be
 * converted to a window that starts with UNBOUNDED PRECEDING to take advantage of the more efficient algorithm.
 *
 * Sliding windows are windows where both sides of the window are following(nrows), * preceding(nrows) or current row.
 * Analytic aggregate algorithms for sliding windows are generally implemented with a Deque so that elements can
 * be added or removed from the widow.
 *
 * This class creates two implementations per analytic function. One for fixed windows and one for sliding windows.
 */
enum AnalyticAggregateFunctions implements AnalyticFunctionMetaData {
  SUM(new SumFunctions<>()),
  MEAN(null),
  MAX(new MaxFunctions<>()),
  MIN(null),
  COUNT(null);

  private final WindowDependentAggregateFunction<?> implementation;

  AnalyticAggregateFunctions(WindowDependentAggregateFunction<?> implementation) {
    this.implementation = implementation;
  }

  AggregateFunction<?, Double> getImplementation(WindowGrowthType growthType) {
    return this.implementation.functionFor(growthType);
  }

  @Override
  public String functionName() {
    return name();
  }

  @Override
  public ColumnType returnType() {
    return ColumnType.DOUBLE;
  }

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.DOUBLE)
      || type.equals(ColumnType.FLOAT)
      || type.equals(ColumnType.INTEGER)
      || type.equals(ColumnType.SHORT)
      || type.equals(ColumnType.LONG);
  }

  private abstract static class WindowDependentAggregateFunction<T extends Number> {

    abstract AggregateFunction<T, Double> fixedFunction();

    abstract AggregateFunction<T, Double> slidingFunction();

    AggregateFunction<T, Double> functionFor(WindowGrowthType growthType) {
      switch (growthType) {
        case FIXED:
        case FIXED_START:
          return fixedFunction();
        case FIXED_END:
          // TODO. It is possible to use a fixed fuction here and get a more efficient implementation.
        case SLIDING:
          return slidingFunction();
      }
      throw new RuntimeException("Unrecognized growthType: " + growthType);
    }
  }

  static class SumFunctions<T extends Number> extends WindowDependentAggregateFunction<T> {
    @Override
    AggregateFunction<T, Double> fixedFunction() {
      return new AggregateFunction<T, Double>() {
        private double sum = Double.NaN;

        @Override
        public Double getValue() {
          return sum;
        }

        @Override
        public void removeLeftMost() {
        }

        @Override
        public void addRightMostMissing() {
        }

        @Override
        public void addRightMost(T newValue) {
          if (Double.isNaN(sum)) {
            this.sum = 0.0;
          }
          this.sum += newValue.doubleValue();
        }
      };
    }

    @Override
    AggregateFunction<T, Double> slidingFunction() {
      return new AggregateFunction<T, Double>() {
        private final ArrayDeque<Double> queue = new ArrayDeque<>();
        private Double sum = 0.0;
        private int missingCount = 0;

        @Override
        public void removeLeftMost() {
          Double removed = queue.remove();
          if (Double.isNaN(removed)) {
            missingCount--;
          } else {
            this.sum -= removed;
          }
        }

        @Override
        public void addRightMost(T newValue) {
          Double doubleValue = newValue.doubleValue();
          this.sum += doubleValue;
          queue.add(doubleValue);
        }

        @Override
        public void addRightMostMissing() {
          queue.add(Double.NaN);
          missingCount++;
        }

        @Override
        public Double getValue() {
          if (queue.size() == 0 || missingCount == queue.size()) {
            return Double.NaN;
          }
          return sum;
        }
      };
    }
  }

  static class MaxFunctions<T extends Number> extends WindowDependentAggregateFunction<T> {

    @Override
    AggregateFunction<T, Double> fixedFunction() {
      return new AggregateFunction<T, Double>() {
        private Double max = Double.NaN;

        @Override
        public void removeLeftMost() {
          throw new IllegalArgumentException("Should never call remove on fixed function");
        }

        @Override
        public void addRightMost(T newValue) {
          if (Double.isNaN(max)) {
            max = newValue.doubleValue();
            return;
          }
          this.max = Math.max(max, newValue.doubleValue());
        }

        @Override
        public void addRightMostMissing() {
        }

        @Override
        public Double getValue() {
          return max;
        }
      };
    }

    @Override
    AggregateFunction<T, Double> slidingFunction() {
      return new AggregateFunction<T, Double>() {
        private final ArrayDeque<Double> queue = new ArrayDeque<>();

        @Override
        public void removeLeftMost() {
          queue.remove();
        }

        @Override
        public void addRightMost(T newValue) {
          queue.add(newValue.doubleValue());
        }

        @Override
        public void addRightMostMissing() {
          queue.add(Double.NaN);
        }

        @Override
        public Double getValue() {
          return queue.stream().filter(d -> !Double.isNaN(d)).mapToDouble(Number::doubleValue)
            .max().orElse(Double.NaN);
        }
      };
    }
  }


}

package tech.tablesaw.analytic;

import java.util.ArrayDeque;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;
import tech.tablesaw.api.ColumnType;

/**
 * Analytic Aggregate functions.
 *
 * <p>Analytic Aggregate functions require different implementations compared to regular aggregate
 * functions because they are called once per row and must return a value for every row in the
 * table.
 *
 * <p>Consider calculating the SUM over a table with a window definition of ROWS BETWEEN UNBOUNDED
 * PRECEDING AND CURRENT ROW. If a regular aggregate function was used it would be called once for
 * each row and since each window contains O(n) rows for a total running time of O(n^2). Clearly you
 * can use a more efficient algorithm that keeps a running sum as rows are added to the window and
 * runs in O(n). This class allows for those more efficient algorithms to be used.
 *
 * <p>If at least one side of the window is unbounded the window is considered an append window.
 * With a bit of tweaking windows UNBOUNDED FOLLOWING windows can be converted to UNBOUNDED
 * PRECEDING windows so they are append only and can use a more efficient algorithm similar to the
 * one explained above.
 *
 * <p>Sliding windows are windows where both sides of the window are following, preceding or current
 * row. Analytic aggregate algorithms for sliding windows are generally implemented with a {@link
 * java.util.Deque} so that elements can be added or removed from either side of the window as it
 * slides.
 *
 * <p>This class creates two implementations per analytic aggregate function. One for append windows
 * and one for sliding windows.
 */
enum AggregateFunctions implements FunctionMetaData {
  SUM(new Sum<>()),
  MAX(new Max<>()),
  MIN(new Min<>()),
  MEAN(new Mean<>()),
  COUNT(new Count<>());

  private final WindowDependentAggregateFunction<?> implementation;

  AggregateFunctions(WindowDependentAggregateFunction<?> implementation) {
    this.implementation = implementation;
  }

  /** Get the right implementation for that window definition. */
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

    /** Sub classes of append windows should never call removeLeft. */
    abstract static class AppendAggregateFunction<T, R> implements AggregateFunction<T, R> {
      @Override
      public final void removeLeftMost() {
        throw new UnsupportedOperationException(
            "Implementers of append aggregate functions should never call removeLeftMost");
      }
    }

    abstract AppendAggregateFunction<T, Double> functionForAppendWindows();

    abstract AggregateFunction<T, Double> functionForSlidingWindows();

    AggregateFunction<T, Double> functionFor(WindowGrowthType growthType) {
      switch (growthType) {
        case FIXED:
        case FIXED_LEFT:
        case FIXED_RIGHT:
          return functionForAppendWindows();
        case SLIDING:
          return functionForSlidingWindows();
      }
      throw new RuntimeException("Unexpected growthType: " + growthType);
    }
  }

  static class Sum<T extends Number> extends WindowDependentAggregateFunction<T> {
    @Override
    AppendAggregateFunction<T, Double> functionForAppendWindows() {
      return new AppendAggregateFunction<T, Double>() {
        private double sum = Double.NaN;

        @Override
        public Double getValue() {
          return sum;
        }

        @Override
        public void addRightMostMissing() {}

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
    AggregateFunction<T, Double> functionForSlidingWindows() {
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

  static class Max<T extends Number> extends WindowDependentAggregateFunction<T> {

    @Override
    AppendAggregateFunction<T, Double> functionForAppendWindows() {
      return new AppendAggregateFunction<T, Double>() {
        private Double max = Double.NaN;

        @Override
        public void addRightMost(T newValue) {
          if (Double.isNaN(max)) {
            max = newValue.doubleValue();
            return;
          }
          this.max = Math.max(max, newValue.doubleValue());
        }

        @Override
        public void addRightMostMissing() {}

        @Override
        public Double getValue() {
          return max;
        }
      };
    }

    @Override
    AggregateFunction<T, Double> functionForSlidingWindows() {
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
          // This could be faster, but probably does not matter in practice because sliding windows
          // will be small.
          return queue.stream()
              .filter(d -> !Double.isNaN(d))
              .mapToDouble(Number::doubleValue)
              .max()
              .orElse(Double.NaN);
        }
      };
    }
  }

  static class Min<T extends Number> extends WindowDependentAggregateFunction<T> {

    @Override
    AppendAggregateFunction<T, Double> functionForAppendWindows() {
      throw new UnsupportedOperationException("Analytic Function Min Is Not implemented");
    }

    @Override
    AggregateFunction<T, Double> functionForSlidingWindows() {
      throw new UnsupportedOperationException("Analytic Function Min Is Not implemented");
    }
  }

  static class Mean<T extends Number> extends WindowDependentAggregateFunction<T> {

    @Override
    AppendAggregateFunction<T, Double> functionForAppendWindows() {
      throw new UnsupportedOperationException("Analytic Function Mean Is Not implemented");
    }

    @Override
    AggregateFunction<T, Double> functionForSlidingWindows() {
      throw new UnsupportedOperationException("Analytic Function Mean Is Not implemented");
    }
  }

  static class Count<T extends Number> extends WindowDependentAggregateFunction<T> {

    @Override
    AppendAggregateFunction<T, Double> functionForAppendWindows() {
      throw new UnsupportedOperationException("Analytic Function Count Is Not implemented");
    }

    @Override
    AggregateFunction<T, Double> functionForSlidingWindows() {
      throw new UnsupportedOperationException("Analytic Function Count Is Not implemented");
    }
  }
}

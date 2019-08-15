package tech.tablesaw.analytic;


import java.util.ArrayDeque;
import tech.tablesaw.analytic.WindowFrame.WindowGrowthType;
import tech.tablesaw.api.ColumnType;

/**
 * List of Analytic Aggregate functions that can be used in an Analytic Over query.
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
      if (growthType == WindowGrowthType.SLIDING) {
        return slidingFunction();
      }
      return fixedFunction();
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
            throw new IllegalArgumentException("Should never call remove on fixed function");
          }

          @Override
          public void addRightMostMissing() {}

          @Override
          public void addRightMost(T newValue) {
            if(Double.isNaN(sum)) {
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
        private int missigCount = 0;

        @Override
        public void removeLeftMost() {
          Double removed = queue.remove();
          if (Double.isNaN(removed)) {
            missigCount--;
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
          missigCount++;
        }

        @Override
        public Double getValue() {
          if (queue.size() == 0 || missigCount == queue.size()) {
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
            if(Double.isNaN(max)) {
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

package tech.tablesaw.analytic;

import com.google.common.collect.Streams;
import java.util.ArrayDeque;

// TODO handle overflow
// TODO handle NAN in sum
// TODO handle missing values
class AggregateFunctionImplementations {

  static class SumFunctions<T extends Number> extends WindowDependentAggregateFunction<T> {
    private static final AnalyticAggregateFunctions function = AnalyticAggregateFunctions.SUM;

    @Override
    NumericAggregateFunction<T> fixedFunction() {
      return new NumericAggregateFunction<T>() {
        private double sum;

        @Override
        public String functionName() {
          return function.toString();
        }

        @Override
        public void setWindow(Iterable<T> window) {
          this.sum = Streams.stream(window).mapToDouble(Number::doubleValue).sum();
        }

        @Override
        public Double getValue() {
          return sum;
        }

        @Override
        public void shiftLeft() {
        }

        @Override
        public void shiftRight(T newValue) {

        }
      };
    }

    @Override
    NumericAggregateFunction<T> growingFunction() {
      return new NumericAggregateFunction<T>() {
        private Double sum = 0.0;

        @Override
        public String functionName() {
          return function.toString();
        }

        @Override
        public void shiftLeft() {
        }

        @Override
        public void shiftRight(T newValue) {
          this.sum += newValue.doubleValue();
        }

        @Override
        public void setWindow(Iterable<T> window) {
          this.sum = Streams.stream(window).mapToDouble(Number::doubleValue).sum();
        }

        @Override
        public Double getValue() {
          return sum;
        }

      };
    }

    @Override
    NumericAggregateFunction<T> slidingFunction() {
      return new NumericAggregateFunction<T>() {
        private final ArrayDeque<Double> queue = new ArrayDeque<>();
        private Double sum = 0.0;

        @Override
        public String functionName() {
          return function.toString();
        }

        @Override
        public void shiftLeft() {
          double removed = queue.remove();
          this.sum -= removed;
        }

        @Override
        public void shiftRight(T newValue) {
          Double doubleValue = newValue.doubleValue();
          this.sum += doubleValue;
          queue.add(doubleValue);
        }

        @Override
        public void setWindow(Iterable<T> window) {
          window.forEach(v -> {
            double doubleValue = v.doubleValue();
            queue.add(doubleValue);
            this.sum += doubleValue;
          });
        }

        @Override
        public Double getValue() {
          return sum;
        }
      };
    }
  }

  static class MaxFunctions<T extends Number> extends WindowDependentAggregateFunction<T> {
    private static final AnalyticAggregateFunctions function = AnalyticAggregateFunctions.MAX;

    @Override
    NumericAggregateFunction<T> fixedFunction() {
      return new NumericAggregateFunction<T>() {
        private Double max;

        @Override
        public String functionName() {
          return function.toString();
        }

        @Override
        public void shiftLeft() {}

        @Override
        public void shiftRight(T newValue) {}


        @Override
        public void setWindow(Iterable<T> window) {
          this.max = Streams.stream(window).mapToDouble(Number::doubleValue).max().orElse(Double.NaN);
        }

        @Override
        public Double getValue() {
          return max;
        }
      };
    }

    @Override
    NumericAggregateFunction<T> growingFunction() {
      return new NumericAggregateFunction<T>() {
        private Double max = Double.NaN;

        @Override
        public String functionName() {
          return function.toString();
        }

        @Override
        public void shiftLeft() {}

        @Override
        public void shiftRight(T newValue) {
          this.max = Math.max(newValue.doubleValue(), max);
        }

        @Override
        public void setWindow(Iterable<T> window) {
          this.max = Streams.stream(window).mapToDouble(Number::doubleValue).max().orElse(Double.NaN);
        }

        @Override
        public Double getValue() {
          return max;
        }
      };
    }

    @Override
    NumericAggregateFunction<T> slidingFunction() {
      return new NumericAggregateFunction<T>() {
        private final ArrayDeque<Double> queue = new ArrayDeque<>();

        @Override
        public String functionName() {
          return function.toString();
        }

        @Override
        public void shiftLeft() {
          queue.remove();
        }

        @Override
        public void shiftRight(T newValue) {
          queue.add(newValue.doubleValue());

        }

        @Override
        public void setWindow(Iterable<T> window) {
          window.forEach(v -> queue.add(v.doubleValue()));
        }

        @Override
        public Double getValue() {
          return queue.stream().mapToDouble(Number::doubleValue).max().orElse(Double.NaN);
        }
      };
    }
  }
}

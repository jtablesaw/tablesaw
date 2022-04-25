package tech.tablesaw.aggregate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;

/** Static utility class for pre-defined instances of {@link AggregateFunction} */
public class AggregateFunctions {

  private AggregateFunctions() {}

  /**
   * A function that takes a column argument and returns the earliest date-time value in that column
   */
  public static final DateTimeAggregateFunction earliestDateTime =
      new DateTimeAggregateFunction("Earliest Date-Time") {

        @Override
        public LocalDateTime summarize(DateTimeColumn column) {
          return column.min();
        }
      };

  /** A function that takes a column argument and returns the earliest date in that column */
  public static final DateAggregateFunction earliestDate =
      new DateAggregateFunction("Earliest Date") {

        @Override
        public LocalDate summarize(DateColumn column) {
          return column.min();
        }
      };

  /** A function that takes a column argument and returns the latest date in that column */
  public static final DateAggregateFunction latestDate =
      new DateAggregateFunction("Latest Date") {

        @Override
        public LocalDate summarize(DateColumn column) {
          return column.max();
        }
      };

  /**
   * A function that takes a column argument and returns the latest date-time value in that column
   */
  public static final DateTimeAggregateFunction latestDateTime =
      new DateTimeAggregateFunction("Latest Date-Time") {

        @Override
        public LocalDateTime summarize(DateTimeColumn column) {
          return column.max();
        }
      };

  /** A function that takes a column argument and returns the latest instant in that column */
  public static final InstantAggregateFunction maxInstant =
      new InstantAggregateFunction("Max Instant") {
        @Override
        public Instant summarize(InstantColumn column) {
          return column.max();
        }
      };

  /** A function that takes a column argument and returns the earliest Instant in that column */
  public static final InstantAggregateFunction minInstant =
      new InstantAggregateFunction("Min Instant") {
        @Override
        public Instant summarize(InstantColumn column) {
          return column.min();
        }
      };

  /**
   * A function that takes a column argument and returns the number of {@code true} values in a
   * column
   */
  public static final BooleanIntAggregateFunction countTrue =
      new BooleanIntAggregateFunction("Number True") {

        @Override
        public Integer summarize(BooleanColumn column) {
          return column.countTrue();
        }
      };

  /**
   * A function that takes a column argument and returns {@code true} if all values in that column
   * are true
   */
  public static final BooleanAggregateFunction allTrue =
      new BooleanAggregateFunction("All True") {

        @Override
        public Boolean summarize(BooleanColumn column) {
          return column.all();
        }
      };

  /**
   * A function that takes a column argument and returns {@code true} if at least one value in the
   * column is true
   */
  public static final BooleanAggregateFunction anyTrue =
      new BooleanAggregateFunction("Any True") {

        @Override
        public Boolean summarize(BooleanColumn column) {
          return column.any();
        }
      };

  /**
   * A function that takes a column argument and returns {@code true} if no values in the column are
   * true
   */
  public static final BooleanAggregateFunction noneTrue =
      new BooleanAggregateFunction("None True") {

        @Override
        public Boolean summarize(BooleanColumn column) {
          return column.none();
        }
      };

  /**
   * A function that takes a column argument and returns the count of {@code false} values in the
   * column
   */
  public static final BooleanIntAggregateFunction countFalse =
      new BooleanIntAggregateFunction("Number False") {
        @Override
        public Integer summarize(BooleanColumn column) {
          return (column).countFalse();
        }
      };

  /**
   * A function that takes a column argument and returns the proportion of values in that column
   * that are {@code true}
   */
  public static final BooleanDoubleAggregateFunction proportionTrue =
      new BooleanDoubleAggregateFunction("Proportion True") {
        @Override
        public Double summarize(BooleanColumn column) {
          return (column).proportionTrue();
        }
      };

  /**
   * A function that takes a column argument and returns the proportion of values in the column that
   * are {@code false}
   */
  public static final BooleanDoubleAggregateFunction proportionFalse =
      new BooleanDoubleAggregateFunction("Proportion False") {
        @Override
        public Double summarize(BooleanColumn column) {
          return (column).proportionFalse();
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the first item without
   * sorting
   */
  public static final NumericAggregateFunction first =
      new NumericAggregateFunction("First") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return column.isEmpty() ? DoubleColumnType.missingValueIndicator() : column.getDouble(0);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the last item without
   * sorting
   */
  public static final NumericAggregateFunction last =
      new NumericAggregateFunction("Last") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return column.isEmpty()
              ? DoubleColumnType.missingValueIndicator()
              : column.getDouble(column.size() - 1);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the difference between the
   * last and first items
   */
  public static final NumericAggregateFunction change =
      new NumericAggregateFunction("Change") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return column.size() < 2
              ? DoubleColumnType.missingValueIndicator()
              : column.getDouble(column.size() - 1) - column.getDouble(0);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the percent difference
   * between the last and first items
   */
  public static final NumericAggregateFunction pctChange =
      new NumericAggregateFunction("Percent Change") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return column.size() < 2
              ? DoubleColumnType.missingValueIndicator()
              : (column.getDouble(column.size() - 1) - column.getDouble(0)) / column.getDouble(0);
        }
      };

  /**
   * A function that takes a {@link Column} argument and returns the count of values in the column
   * excluding missing values
   */
  public static final AnyIntAggregateFunction countNonMissing =
      new AnyIntAggregateFunction("Count") {

        @Override
        public Integer summarize(Column<?> column) {
          return column.size() - column.countMissing();
        }
      };

  /**
   * A function that takes a {@link Column} argument and returns the count of values in the column
   * excluding missing values. A synonym for countNonMissing
   */
  public static final AnyIntAggregateFunction count = countNonMissing;

  /**
   * A function that takes a {@link NumericColumn} argument and returns the count of missing values
   * in the column
   */
  public static final AnyIntAggregateFunction countMissing =
      new AnyIntAggregateFunction("Missing Values") {

        @Override
        public Integer summarize(Column<?> column) {
          return column.countMissing();
        }
      };

  /**
   * AA function that takes a {@link Column} argument and returns the number of non-missing unique
   * values in the column
   */
  public static final AnyIntAggregateFunction countUnique =
      new AnyIntAggregateFunction("Count Unique") {

        @Override
        public Integer summarize(Column<?> doubles) {
          return doubles.unique().removeMissing().size();
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the mean of the values in
   * the column
   */
  public static final NumericAggregateFunction mean =
      new NumericAggregateFunction("Mean") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.mean(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the coefficient of variation
   * (stdDev/mean) of the values in the column
   */
  public static final NumericAggregateFunction cv =
      new NumericAggregateFunction("CV") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          double[] col = removeMissing(column);
          return Math.sqrt(StatUtils.variance(col)) / StatUtils.mean(col);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the sum of the values in the
   * column
   */
  public static final NumericAggregateFunction sum =
      new NumericAggregateFunction("Sum") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.sum(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the median of the values in
   * the column
   */
  public static final NumericAggregateFunction median =
      new NumericAggregateFunction("Median") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return percentile(column, 50.0);
        }
      };

  /**
   * A function that takes a {@link Column} argument and returns the number of values in the column,
   * including missing values
   */
  public static final AnyIntAggregateFunction countWithMissing =
      new AnyIntAggregateFunction("Count (incl. missing)") {

        @Override
        public Integer summarize(Column<?> column) {
          return column.size();
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the first quartile of the
   * values in the column
   */
  public static final NumericAggregateFunction quartile1 =
      new NumericAggregateFunction("First Quartile") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return percentile(column, 25.0);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the third quartile of the
   * values in the column
   */
  public static final NumericAggregateFunction quartile3 =
      new NumericAggregateFunction("Third Quartile") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return percentile(column, 75.0);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the 90th percentile of the
   * values in the column
   */
  public static final NumericAggregateFunction percentile90 =
      new NumericAggregateFunction("90th Percentile") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return percentile(column, 90.0);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the 95th percentile of the
   * values in the column
   */
  public static final NumericAggregateFunction percentile95 =
      new NumericAggregateFunction("95th Percentile") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return percentile(column, 95.0);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the 99th percentile of the
   * values in the column
   */
  public static final NumericAggregateFunction percentile99 =
      new NumericAggregateFunction("99th Percentile") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return percentile(column, 99.0);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the difference between the
   * largest and smallest values in the column
   */
  public static final NumericAggregateFunction range =
      new NumericAggregateFunction("Range") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          double[] data = removeMissing(column);
          return StatUtils.max(data) - StatUtils.min(data);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the smallest value in the
   * column
   */
  public static final NumericAggregateFunction min =
      new NumericAggregateFunction("Min") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.min(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the largeset value in the
   * column
   */
  public static final NumericAggregateFunction max =
      new NumericAggregateFunction("Max") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.max(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the product of all values in
   * the column
   */
  public static final NumericAggregateFunction product =
      new NumericAggregateFunction("Product") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.product(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the geometric mean of all
   * values in the column
   */
  public static final NumericAggregateFunction geometricMean =
      new NumericAggregateFunction("Geometric Mean") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.geometricMean(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the population variance of
   * all values in the column
   */
  public static final NumericAggregateFunction populationVariance =
      new NumericAggregateFunction("Population Variance") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.populationVariance(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the quadratic mean, aka, the
   * root-mean-square
   */
  public static final NumericAggregateFunction quadraticMean =
      new NumericAggregateFunction("Quadratic Mean") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return new DescriptiveStatistics(removeMissing(column)).getQuadraticMean();
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the kurtosis of its values
   */
  public static final NumericAggregateFunction kurtosis =
      new NumericAggregateFunction("Kurtosis") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          double[] data = removeMissing(column);
          return new Kurtosis().evaluate(data, 0, data.length);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the skewness of its values
   */
  public static final NumericAggregateFunction skewness =
      new NumericAggregateFunction("Skewness") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          double[] data = removeMissing(column);
          return new Skewness().evaluate(data, 0, data.length);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the sumOfSquares of its
   * values
   */
  public static final NumericAggregateFunction sumOfSquares =
      new NumericAggregateFunction("Sum of Squares") {

        @Override
        public String functionName() {
          return "Sum of Squares";
        }

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.sumSq(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the sumOfLogs of its values
   */
  public static final NumericAggregateFunction sumOfLogs =
      new NumericAggregateFunction("Sum of Logs") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return StatUtils.sumLog(removeMissing(column));
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the variance of its values
   */
  public static final NumericAggregateFunction variance =
      new NumericAggregateFunction("Variance") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          double[] values = removeMissing(column);
          return StatUtils.variance(values);
        }
      };

  /**
   * A function that takes a {@link NumericColumn} argument and returns the standard deviation of
   * its values
   */
  public static final NumericAggregateFunction stdDev =
      new NumericAggregateFunction("Std. Deviation") {

        @Override
        public Double summarize(NumericColumn<?> column) {
          return Math.sqrt(StatUtils.variance(removeMissing(column)));
        }
      };

  /** Returns the given percentile of the values in the argument */
  public static Double percentile(NumericColumn<?> data, Double percentile) {
    return StatUtils.percentile(removeMissing(data), percentile);
  }

  private static double[] removeMissing(NumericColumn<?> column) {
    NumericColumn<?> numericColumn = (NumericColumn<?>) column.removeMissing();
    return numericColumn.asDoubleArray();
  }

  /**
   * Returns the given mean difference of the values in the arguments <br>
   * TODO(lwhite): These are two column reductions. We need a class for that
   */
  public static Double meanDifference(NumericColumn<?> column1, NumericColumn<?> column2) {
    return StatUtils.meanDifference(column1.asDoubleArray(), column2.asDoubleArray());
  }

  /**
   * Returns the given sum difference of the values in the arguments <br>
   * TODO(lwhite): These are two column reductions. We need a class for that
   */
  public static Double sumDifference(NumericColumn<?> column1, NumericColumn<?> column2) {
    return StatUtils.sumDifference(column1.asDoubleArray(), column2.asDoubleArray());
  }
}

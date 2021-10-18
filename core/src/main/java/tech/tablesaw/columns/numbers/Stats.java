/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.columns.numbers;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

/**
 * An object that calculates in one pass a variety of common statistical values that describe a
 * column
 */
public class Stats {

  private long n;
  private double sum;
  private double mean;
  private double min;
  private double max;
  private double variance;
  private double standardDeviation;
  private double geometricMean;
  private double quadraticMean;
  private double secondMoment;
  private double populationVariance;
  private double sumOfLogs;
  private double sumOfSquares;
  private final String name;

  /** Constructs a Stats object with the given name */
  private Stats(String name) {
    this.name = name;
  }

  /** Constructs a Stats object from the given column */
  public static Stats create(final NumericColumn<?> values) {
    SummaryStatistics summaryStatistics = new SummaryStatistics();
    for (int i = 0; i < values.size(); i++) {
      summaryStatistics.addValue(values.getDouble(i));
    }
    return getStats(values, summaryStatistics);
  }

  private static Stats getStats(NumericColumn<?> values, SummaryStatistics summaryStatistics) {
    Stats stats = new Stats("Column: " + values.name());
    stats.min = summaryStatistics.getMin();
    stats.max = summaryStatistics.getMax();
    stats.n = summaryStatistics.getN();
    stats.sum = summaryStatistics.getSum();
    stats.variance = summaryStatistics.getVariance();
    stats.populationVariance = summaryStatistics.getPopulationVariance();
    stats.quadraticMean = summaryStatistics.getQuadraticMean();
    stats.geometricMean = summaryStatistics.getGeometricMean();
    stats.mean = summaryStatistics.getMean();
    stats.standardDeviation = summaryStatistics.getStandardDeviation();
    stats.sumOfLogs = summaryStatistics.getSumOfLogs();
    stats.sumOfSquares = summaryStatistics.getSumsq();
    stats.secondMoment = summaryStatistics.getSecondMoment();
    return stats;
  }

  /** Returns the range of values in the data */
  public double range() {
    return (max - min);
  }

  /** Returns the standard deviation of values in the data */
  public double standardDeviation() {
    return standardDeviation;
  }

  /** Returns the number of values in the data */
  public long n() {
    return n;
  }

  /** Returns the mean of values in the data */
  public double mean() {
    return mean;
  }

  /** Returns the smallest value */
  public double min() {
    return min;
  }

  /** Returns the largest value */
  public double max() {
    return max;
  }

  /** Returns the sum of the values */
  public double sum() {
    return sum;
  }

  /** Returns the sample variance of the values */
  public double variance() {
    return variance;
  }

  /** Returns the sum of squares of the values */
  public double sumOfSquares() {
    return sumOfSquares;
  }

  /** Returns the population variance of the values */
  public double populationVariance() {
    return populationVariance;
  }

  /** Returns the sum of the logs of the values */
  public double sumOfLogs() {
    return sumOfLogs;
  }

  /** Returns the geometric mean of the values */
  public double geometricMean() {
    return geometricMean;
  }

  /** Returns the quadratic mean of the values */
  public double quadraticMean() {
    return quadraticMean;
  }

  /** Returns the second moment of the values */
  public double secondMoment() {
    return secondMoment;
  }

  /** Returns the most common calculated statistics in tabular form */
  public Table asTable() {
    Table t = Table.create(name);
    StringColumn measure = StringColumn.create("Measure");
    DoubleColumn value = DoubleColumn.create("Value");
    t.addColumns(measure);
    t.addColumns(value);

    measure.append("Count");
    value.append(n);

    measure.append("sum");
    value.append(sum());

    measure.append("Mean");
    value.append(mean());

    measure.append("Min");
    value.append(min());

    measure.append("Max");
    value.append(max());

    measure.append("Range");
    value.append(range());

    measure.append("Variance");
    value.append(variance());

    measure.append("Std. Dev");
    value.append(standardDeviation());

    return t;
  }

  /** Returns all the calculated statistics in tabular form */
  public Table asTableComplete() {
    Table t = asTable();

    StringColumn measure = t.stringColumn("Measure");
    DoubleColumn value = t.doubleColumn("Value");

    measure.append("Sum of Squares");
    value.append(sumOfSquares());

    measure.append("Sum of Logs");
    value.append(sumOfLogs());

    measure.append("Population Variance");
    value.append(populationVariance());

    measure.append("Geometric Mean");
    value.append(geometricMean());

    measure.append("Quadratic Mean");
    value.append(quadraticMean());

    measure.append("Second Moment");
    value.append(secondMoment());

    return t;
  }
}

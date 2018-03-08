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

package tech.tablesaw.util;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;

/**
 *
 */
public class Stats {

    long n;
    double sum;
    double mean;
    double min;
    double max;
    double variance;
    double standardDeviation;
    double geometricMean;
    double quadraticMean;
    double secondMoment;
    double populationVariance;
    double sumOfLogs;
    double sumOfSquares;
    private String name;

    public Stats(String name) {
        this.name = name;
    }

    public static Stats create(final FloatColumn values) {
        SummaryStatistics summaryStatistics = new SummaryStatistics();
        for (float f : values) {
            summaryStatistics.addValue(f);
        }
        return getStats(values, summaryStatistics);
    }

    public static Stats create(final DoubleColumn values) {
        SummaryStatistics summaryStatistics = new SummaryStatistics();
        for (double f : values) {
            summaryStatistics.addValue(f);
        }
        return getStats(values, summaryStatistics);
    }

    public static Stats create(final IntColumn ints) {
        FloatColumn values = new FloatColumn(ints.name(), ints.asFloatArray());
        return create(values);
    }

    public static Stats create(final ShortColumn ints) {
        FloatColumn values = new FloatColumn(ints.name(), ints.asFloatArray());
        return create(values);
    }

    public static Stats create(final LongColumn ints) {
        FloatColumn values = new FloatColumn(ints.name(), ints.asFloatArray());
        return create(values);
    }

    private static Stats getStats(FloatColumn values, SummaryStatistics summaryStatistics) {
        Stats stats = new Stats("Column: " + values.name());
        stats.min = (float) summaryStatistics.getMin();
        stats.max = (float) summaryStatistics.getMax();
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

    private static Stats getStats(DoubleColumn values, SummaryStatistics summaryStatistics) {
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

    public float range() {
        return (float) (max - min);
    }

    public float standardDeviation() {
        return (float) standardDeviation;
    }

    public long n() {
        return n;
    }

    public float mean() {
        return (float) (sum / (double) n);
    }

    public float min() {
        return (float) min;
    }

    public float max() {
        return (float) max;
    }

    public float sum() {
        return (float) sum;
    }

    public float variance() {
        return (float) variance;
    }

    public float sumOfSquares() {
        return (float) sumOfSquares;
    }

    public float populationVariance() {
        return (float) populationVariance;
    }

    public float sumOfLogs() {
        return (float) sumOfLogs;
    }

    public float geometricMean() {
        return (float) geometricMean;
    }

    public float quadraticMean() {
        return (float) quadraticMean;
    }

    public float secondMoment() {
        return (float) secondMoment;
    }

    public Table asTable() {
        Table t = Table.create(name);
        CategoryColumn measure = new CategoryColumn("Measure");
        FloatColumn value = new FloatColumn("Value");
        t.addColumn(measure);
        t.addColumn(value);

        measure.add("n");
        value.append(n);

        measure.add("sum");
        value.append(sum());

        measure.add("Mean");
        value.append(mean());

        measure.add("Min");
        value.append(min());

        measure.add("Max");
        value.append(max());

        measure.add("Range");
        value.append(range());

        measure.add("Variance");
        value.append(variance());

        measure.add("Std. Dev");
        value.append(standardDeviation());

        return t;
    }

    public Table asTableComplete() {
        Table t = asTable();

        CategoryColumn measure = t.categoryColumn("Measure");
        FloatColumn value = t.floatColumn("Value");

        measure.add("Sum of Squares");
        value.append(sumOfSquares());

        measure.add("Sum of Logs");
        value.append(sumOfLogs());

        measure.add("Population Variance");
        value.append(populationVariance());

        measure.add("Geometric Mean");
        value.append(geometricMean());

        measure.add("Quadratic Mean");
        value.append(quadraticMean());

        measure.add("Second Moment");
        value.append(secondMoment());

        return t;
    }
}

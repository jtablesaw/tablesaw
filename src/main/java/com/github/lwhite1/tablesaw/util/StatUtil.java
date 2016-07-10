package com.github.lwhite1.tablesaw.util;

import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.LongColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;

import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.variance;

/**
 *
 */
public class StatUtil {

  private StatUtil() { }

  public static float mean(FloatColumn values) {
    return (float) (values.sum()) / (float) values.size();
  }

  public static Stats stats(final FloatColumn values) {
    Stats stats = new Stats();
    stats.min = (float) values.min();
    stats.max = (float) values.max();
    stats.n = values.size();
    stats.sum = values.sum();
    stats.variance = variance.reduce(values);
    return stats;
  }

  public static IntStats stats(final IntColumn ints) {
    FloatColumn values = FloatColumn.create(ints.name(), ints.toFloatArray());
    IntStats stats = new IntStats();
    stats.min = ints.min();
    stats.max = ints.max();
    stats.n = values.size();
    stats.sum = ints.sum();
    stats.variance = variance.reduce(values);
    return stats;
  }

  public static IntStats stats(final ShortColumn ints) {
    FloatColumn values = FloatColumn.create(ints.name(), ints.toFloatArray());
    IntStats stats = new IntStats();
    stats.min = ints.min();
    stats.max = ints.max();
    stats.n = values.size();
    stats.sum = ints.sum();
    stats.variance = variance.reduce(values);
    return stats;
  }

  public static IntStats stats(final LongColumn ints) {
    FloatColumn values = FloatColumn.create(ints.name(), ints.toFloatArray());
    IntStats stats = new IntStats();
    stats.min = ints.min();
    stats.max = ints.max();
    stats.n = values.size();
    stats.sum = ints.sum();
    stats.variance = variance.reduce(values);
    return stats;
  }
}

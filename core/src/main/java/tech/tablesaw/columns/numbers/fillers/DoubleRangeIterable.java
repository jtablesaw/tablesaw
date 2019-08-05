package tech.tablesaw.columns.numbers.fillers;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;

public class DoubleRangeIterable implements Iterable<Double> {

  private final double from, to, by;
  private final boolean including;
  private final int count;

  private DoubleRangeIterable(
      final double from,
      final double to,
      final boolean including,
      final double by,
      final int count) {
    this.from = from;
    this.to = to;
    this.including = including;
    this.by = by;
    this.count = count;
  }

  private static DoubleRangeIterable range(
      final double from, final double to, final double by, final int count) {
    return new DoubleRangeIterable(from, to, false, by, count);
  }

  public static DoubleRangeIterable range(final double from, final double to, final double by) {
    return range(from, to, by, -1);
  }

  public static DoubleRangeIterable range(final double from, final double to) {
    return range(from, to, 1.0);
  }

  public static DoubleRangeIterable range(final double from, final double by, final int count) {
    return range(from, Double.NaN, by, count);
  }

  public static DoubleRangeIterable range(final double from, final int count) {
    return range(from, 1.0, count);
  }

  public DoubleIterator iterator() {

    return new DoubleIterator() {

      double next = from;
      int num = 0;

      @Override
      public boolean hasNext() {
        return (count < 0 || num < count)
            && (Double.isNaN(to)
                || Math.abs(next - from) < Math.abs(to - from)
                || (including && next == to));
      }

      @Override
      public double nextDouble() {
        final double current = next;
        next += by;
        num++;
        return current;
      }
    };
  }

  public IntIterator intIterator() {

    return new IntIterator() {

      int next = (int) from;
      int num = 0;

      @Override
      public boolean hasNext() {
        return (count < 0 || num < count)
            && (Double.isNaN(to)
                || Math.abs(next - from) < Math.abs(to - from)
                || (including && next == to));
      }

      @Override
      public int nextInt() {
        final int current = next;
        next += by;
        num++;
        return current;
      }
    };
  }
}

package tech.tablesaw.columns.booleans.fillers;

import it.unimi.dsi.fastutil.booleans.BooleanIterator;

public class BooleanIterable implements it.unimi.dsi.fastutil.booleans.BooleanIterable {

  private final long bits;
  private final int length;

  private BooleanIterable(final long bits, final int length) {
    this.bits = bits;
    this.length = length;
  }

  public static BooleanIterable bits(final long bits, final int length) {
    return new BooleanIterable(bits, length);
  }

  @Override
  public BooleanIterator iterator() {

    return new BooleanIterator() {

      int num = 0;
      boolean next = bit(num);

      private boolean bit(final int num) {
        return ((bits >> (length - num - 1)) & 1) == 1;
      }

      @Override
      public boolean hasNext() {
        return (num < length);
      }

      @Override
      public boolean nextBoolean() {
        final boolean current = next;
        num++;
        next = bit(num);
        return current;
      }
    };
  }
}

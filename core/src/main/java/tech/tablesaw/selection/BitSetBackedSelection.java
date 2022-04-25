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

package tech.tablesaw.selection;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.BitSet;
import java.util.PrimitiveIterator;
import java.util.Random;

/** A Selection implemented using java.util.BitSet */
public class BitSetBackedSelection implements Selection {

  private static final Random random = new Random();
  private final BitSet bitmap;

  /**
   * Returns a selection initialized from 0 to the given size, which cane be used for queries that
   * exclude certain items, by first selecting the items to exclude, then flipping the bits.
   *
   * @param size The size The end point, exclusive
   */
  public BitSetBackedSelection(int size) {
    this.bitmap = new BitSet(size);
  }

  /** Constructs a selection containing the elements in the given array */
  public BitSetBackedSelection(int[] arr) {
    this.bitmap = new BitSet(arr.length);
    add(arr);
  }

  /** Constructs a selection containing the elements in the given bitmap */
  public BitSetBackedSelection(BitSet bitmap) {
    this.bitmap = bitmap;
  }

  /** Constructs an empty Selection */
  public BitSetBackedSelection() {
    this.bitmap = new BitSet();
  }

  /** {@inheritDoc} */
  @Override
  public BitSetBackedSelection removeRange(long start, long end) {
    this.bitmap.clear((int) start, (int) end);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public BitSetBackedSelection flip(int rangeStart, int rangeEnd) {
    this.bitmap.flip(rangeStart, rangeEnd);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public BitSetBackedSelection add(int... ints) {
    for (int i : ints) {
      bitmap.set(i);
    }
    return this;
  }

  @Override
  public String toString() {
    return "Selection of size: " + bitmap.cardinality();
  }

  /** {@inheritDoc} */
  @Override
  public int size() {
    return bitmap.cardinality();
  }

  /** {@inheritDoc} */
  @Override
  public int[] toArray() {
    return bitmap.stream().toArray();
  }

  private BitSet toBitmap(Selection otherSelection) {
    if (otherSelection instanceof BitSetBackedSelection) {
      return (BitSet) ((BitSetBackedSelection) otherSelection).bitmap.clone();
    }
    BitSet bits = new BitSet();
    for (int i : otherSelection) {
      bits.set(i);
    }
    return bits;
  }

  /** Intersects the receiver and {@code otherSelection}, updating the receiver */
  @Override
  public Selection and(Selection otherSelection) {
    bitmap.and(toBitmap(otherSelection));
    return this;
  }

  /** Implements the union of the receiver and {@code otherSelection}, updating the receiver */
  @Override
  public Selection or(Selection otherSelection) {
    bitmap.or(toBitmap(otherSelection));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Selection andNot(Selection otherSelection) {
    bitmap.andNot(toBitmap(otherSelection));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  /** {@inheritDoc} */
  @Override
  public Selection clear() {
    bitmap.clear();
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public boolean contains(int i) {
    return bitmap.get(i);
  }

  /**
   * Adds to the current bitmap all integers in [rangeStart,rangeEnd)
   *
   * @param start inclusive beginning of range
   * @param end exclusive ending of range
   */
  @Override
  public Selection addRange(int start, int end) {
    bitmap.set(start, end);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public int get(int indexToFind) {
    if (indexToFind >= size())
      throw new IndexOutOfBoundsException("The requested index is larger than the selection");
    int currentStep = 0;
    int currentIndex = 0;
    while (true) {
      int value = bitmap.nextSetBit(currentIndex);
      if (indexToFind == currentStep) {
        return value;
      }
      currentStep++;
      currentIndex = value + 1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BitSetBackedSelection integers = (BitSetBackedSelection) o;

    return bitmap.equals(integers.bitmap);
  }

  @Override
  public int hashCode() {
    return bitmap.hashCode();
  }

  /** Returns a fastUtil intIterator that wraps a bitmap intIterator */
  @Override
  public IntIterator iterator() {

    return new IntIterator() {

      private final PrimitiveIterator.OfInt iterator = bitmap.stream().iterator();

      @Override
      public int nextInt() {
        return iterator.next();
      }

      @Override
      public int skip(int k) {
        throw new UnsupportedOperationException("Views do not support skipping in the iterator");
      }

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }
    };
  }

  /** Returns a Selection containing all indexes in the array */
  public static BitSetBackedSelection with(int... rows) {
    BitSetBackedSelection selection = new BitSetBackedSelection();
    for (int i : rows) {
      selection.add(i);
    }
    return selection;
  }

  /**
   * Returns a Selection containing all indexes in the range start (inclusive) to end (exclusive),
   */
  public static BitSetBackedSelection withRange(int start, int end) {
    BitSetBackedSelection selection = new BitSetBackedSelection();
    selection.addRange(start, end);
    return selection;
  }

  /**
   * Returns a Selection containing all values from totalRangeStart to totalRangeEnd, except for
   * those in the range from excludedRangeStart to excludedRangeEnd. Start values are inclusive, end
   * values exclusive.
   */
  protected static Selection withoutRange(
      int totalRangeStart, int totalRangeEnd, int excludedRangeStart, int excludedRangeEnd) {
    Preconditions.checkArgument(excludedRangeStart >= totalRangeStart);
    Preconditions.checkArgument(excludedRangeEnd <= totalRangeEnd);
    Preconditions.checkArgument(totalRangeEnd >= totalRangeStart);
    Preconditions.checkArgument(excludedRangeEnd >= excludedRangeStart);
    Selection selection = BitSetBackedSelection.withRange(totalRangeStart, totalRangeEnd);
    Selection exclusion = BitSetBackedSelection.withRange(excludedRangeStart, excludedRangeEnd);
    selection.andNot(exclusion);
    return selection;
  }

  /** Returns an randomly generated selection of size N where Max is the largest possible value */
  protected static BitSetBackedSelection selectNRowsAtRandom(int n, int max) {
    BitSetBackedSelection selection = new BitSetBackedSelection();
    if (n > max) {
      throw new IllegalArgumentException(
          "Illegal arguments: N (" + n + ") greater than Max (" + max + ")");
    }

    int[] rows = new int[n];
    if (n == max) {
      for (int k = 0; k < n; ++k) {
        selection.add(k);
      }
      return selection;
    }

    BitSet bs = new BitSet(max);
    int cardinality = 0;
    while (cardinality < n) {
      int v = random.nextInt(max);
      if (!bs.get(v)) {
        bs.set(v);
        cardinality++;
      }
    }
    int pos = 0;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      rows[pos++] = i;
    }
    for (int row : rows) {
      selection.add(row);
    }
    return selection;
  }

  @VisibleForTesting
  BitSet bitSet() {
    return bitmap;
  }
}

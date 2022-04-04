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

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.BitSet;
import java.util.Random;
import org.roaringbitmap.RoaringBitmap;

/** A Selection implemented using bitmaps */
public class BitmapBackedSelection implements Selection {

  private static final Random random = new Random();
  private final RoaringBitmap bitmap;

  /**
   * Returns a selection initialized from 0 to the given size, which cane be used for queries that
   * exclude certain items, by first selecting the items to exclude, then flipping the bits.
   *
   * @param size The size The end point, exclusive
   */
  public BitmapBackedSelection(int size) {
    this.bitmap = new RoaringBitmap();
    addRange(0, size);
  }

  /** Constructs a selection containing the elements in the given array */
  public BitmapBackedSelection(int[] arr) {
    this.bitmap = new RoaringBitmap();
    add(arr);
  }

  /** Constructs a selection containing the elements in the given bitmap */
  public BitmapBackedSelection(RoaringBitmap bitmap) {
    this.bitmap = bitmap;
  }

  /** Constructs an empty Selection */
  public BitmapBackedSelection() {
    this.bitmap = new RoaringBitmap();
  }

  /** {@inheritDoc} */
  @Override
  public Selection removeRange(long start, long end) {
    this.bitmap.remove(start, end);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Selection flip(int rangeStart, int rangeEnd) {
    this.bitmap.flip((long) rangeStart, rangeEnd);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Selection add(int... ints) {
    bitmap.add(ints);
    return this;
  }

  @Override
  public String toString() {
    return "Selection of size: " + bitmap.getCardinality();
  }

  /** {@inheritDoc} */
  @Override
  public int size() {
    return bitmap.getCardinality();
  }

  /** {@inheritDoc} */
  @Override
  public int[] toArray() {
    return bitmap.toArray();
  }

  private RoaringBitmap toBitmap(Selection otherSelection) {
    if (otherSelection instanceof BitmapBackedSelection) {
      return ((BitmapBackedSelection) otherSelection).bitmap.clone();
    }
    RoaringBitmap bits = new RoaringBitmap();
    for (int i : otherSelection) {
      bits.add(i);
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
    return bitmap.contains(i);
  }

  /**
   * Adds to the current bitmap all integers in [rangeStart,rangeEnd)
   *
   * @param start inclusive beginning of range
   * @param end exclusive ending of range
   */
  @Override
  public Selection addRange(int start, int end) {
    bitmap.add((long) start, end);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public int get(int i) {
    return bitmap.select(i);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BitmapBackedSelection integers = (BitmapBackedSelection) o;

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

      private final org.roaringbitmap.IntIterator iterator = bitmap.getIntIterator();

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
  protected static Selection with(int... rows) {
    BitmapBackedSelection selection = new BitmapBackedSelection();
    for (int i : rows) {
      selection.add(i);
    }
    return selection;
  }

  /** Returns a Selection containing all indexes in the array */
  protected static Selection fromBitmap(RoaringBitmap bitmap) {
    return new BitmapBackedSelection(bitmap);
  }

  /**
   * Returns a Selection containing all indexes in the range start (inclusive) to end (exclusive),
   */
  protected static Selection withRange(int start, int end) {
    BitmapBackedSelection selection = new BitmapBackedSelection();
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
    Selection selection = Selection.withRange(totalRangeStart, totalRangeEnd);
    Selection exclusion = Selection.withRange(excludedRangeStart, excludedRangeEnd);
    selection.andNot(exclusion);
    return selection;
  }

  /** Returns an randomly generated selection of size N where Max is the largest possible value */
  protected static Selection selectNRowsAtRandom(int n, int max) {
    Selection selection = new BitmapBackedSelection();
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
}

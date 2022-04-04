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

import it.unimi.dsi.fastutil.ints.IntIterable;
import org.roaringbitmap.RoaringBitmap;

/**
 * A selection maintains an ordered set of ints that can be used to filter rows from a table or
 * column. When applying the selection to the data (table, column, etc.) only those rows with
 * indexes included in the selection pass the filter
 */
public interface Selection extends IntIterable {

  /** Returns the elements of this selection as an array of ints */
  int[] toArray();

  /**
   * Adds the given integers to the Selection if it is not already present, and does nothing
   * otherwise
   */
  Selection add(int... ints);

  /**
   * Adds to the current bitmap all integers in [rangeStart,rangeEnd)
   *
   * @param start inclusive beginning of range
   * @param end exclusive ending of range
   */
  Selection addRange(int start, int end);

  /**
   * Removes from the current bitmap from all integers in [rangeStart,rangeEnd)
   *
   * @param start inclusive beginning of range
   * @param end exclusive ending of range
   */
  Selection removeRange(long start, long end);

  /** Returns the number of integers represented by this Selection */
  int size();

  /**
   * Returns this Selection object after its data has been intersected with {@code otherSelection}
   */
  Selection and(Selection otherSelection);

  /**
   * Returns this Selection object with its data replaced by the union of its starting data and
   * {@code otherSelection}
   */
  Selection or(Selection otherSelection);

  /**
   * Implements the set difference operation between the receiver and {@code otherSelection}, after
   * updating the receiver
   */
  Selection andNot(Selection otherSelection);

  /** Returns true if this selection has no values, and false otherwise */
  boolean isEmpty();

  /** Returns this selection with all its values cleared */
  Selection clear();

  /** Returns true if the index i is selected in this object */
  boolean contains(int i);

  /**
   * Returns the value of the ith element. For example, if there are three ints {4, 32, 71} in the
   * selection, get(0) returns 4, get(1) returns 32, and get(2) returns 71
   *
   * <p>It can be useful if you need to iterate over the data, although there is also an iterator
   */
  int get(int i);

  /** Returns a selection with the bits from this selection flipped over the given range */
  Selection flip(int rangeStart, int rangeEnd);

  /** Returns an randomly generated selection of size N where Max is the largest possible value */
  static Selection selectNRowsAtRandom(int n, int max) {
    return BitmapBackedSelection.selectNRowsAtRandom(n, max);
  }

  /** Returns a Selection containing all indexes in the array */
  static Selection with(int... rows) {
    return BitmapBackedSelection.with(rows);
  }

  /** */
  static Selection fromBitmap(RoaringBitmap bitmap) {
    return BitmapBackedSelection.fromBitmap(bitmap);
  }

  /**
   * Returns a Selection containing all indexes in the range start (inclusive) to end (exclusive),
   */
  static Selection withRange(int start, int end) {
    return BitmapBackedSelection.withRange(start, end);
  }

  /**
   * Returns a Selection containing all values from totalRangeStart to totalRangeEnd, except for
   * those in the range from excludedRangeStart to excludedRangeEnd. Start values are inclusive, end
   * values exclusive.
   */
  static Selection withoutRange(
      int totalRangeStart, int totalRangeEnd, int excludedRangeStart, int excludedRangeEnd) {
    return BitmapBackedSelection.withoutRange(
        totalRangeStart, totalRangeEnd, excludedRangeStart, excludedRangeEnd);
  }
}

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
import it.unimi.dsi.fastutil.ints.IntIterable;
import org.apache.commons.lang3.RandomUtils;
import org.roaringbitmap.RoaringBitmap;

import java.util.BitSet;

/**
 * A selection maintains an ordered set of ints that can be used to eval rows from a table or column
 */
public interface Selection extends IntIterable {

    static Selection with(int... rows) {
        BitmapBackedSelection selection = new BitmapBackedSelection();
        for (int i : rows) {
            selection.add(i);
        }
        return selection;
    }

    static Selection withRange(int start, int end) {
        BitmapBackedSelection selection = new BitmapBackedSelection();
        selection.addRange(start, end);
        return selection;
    }

    static Selection withoutRange(int totalRangeStart, int totalRangeEnd, int excludedRangeStart, int excludedRangeEnd) {
        Preconditions.checkArgument(excludedRangeStart >= totalRangeStart);
        Preconditions.checkArgument(excludedRangeEnd <= totalRangeEnd);
        Preconditions.checkArgument(totalRangeEnd >= totalRangeStart);
        Preconditions.checkArgument(excludedRangeEnd >= excludedRangeStart);
        Selection selection = Selection.withRange(totalRangeStart, totalRangeEnd);
        Selection exclusion = Selection.withRange(excludedRangeStart, excludedRangeEnd);
        selection.andNot(exclusion);
        return selection;
    }

    int[] toArray();

    /**
     * Returns a bitmap that represents the state of this selection
     */
    RoaringBitmap toBitmapInternal();

    /**
     * Adds the given integers to the Selection if it is not already present, and does nothing otherwise
     */
    Selection add(int... ints);

    /**
     * Adds to the current bitmap all integers in [rangeStart,rangeEnd)
     *
     * @param start inclusive beginning of range
     * @param end   exclusive ending of range
     */
    Selection addRange(int start, int end);

    Selection removeRange(long start, long end);

    int size();

    /**
     * Returns the intersection of the receiver and {@code otherSelection}, after updating the receiver
     */
    Selection and(Selection otherSelection);

    /**
     * Returns the union of the receiver and {@code otherSelection}, after updating the receiver
     */
    Selection or(Selection otherSelection);

    /**
     * Implements the set difference operation between the receiver and {@code otherSelection}, after updating the receiver
     */
    Selection andNot(Selection otherSelection);

    boolean isEmpty();

    Selection clear();

    boolean contains(int i);

    /**
     * Returns the value of the ith element. For example, if there are three ints {4, 32, 71} in the selection,
     * get(0) returns 4, get(1) returns 32, and get(2) returns 71
     *
     * It can be useful if you need to iterate over the data, although there is also an iterator
     */
    int get(int i);

    /**
     * Returns a selection with the bits from this selection flipped over the given range
     */
    Selection flip(int rangeStart, int rangeEnd);

    /**
     * Returns an randomly generated selection of size N where Max is the largest possible value
     */
    static Selection selectNRowsAtRandom(int n, int max) {
        Selection selection = new BitmapBackedSelection();
        if (n > max) {
            throw new IllegalArgumentException("Illegal arguments: N (" + n + ") greater than Max (" + max + ")");
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
            int v = RandomUtils.nextInt(0, max);
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

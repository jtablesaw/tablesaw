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
import tech.tablesaw.filtering.Filter;

import java.util.List;

/**
 * A selection maintains an ordered set of ints that can be used to eval rows from a table or column
 */
public interface Selection extends IntIterable, Filter {

    static Selection with(List<Integer> indexes) {
        BitmapBackedSelection selection = new BitmapBackedSelection();
        for (Integer i : indexes) {
            selection.add(i);
        }
        return selection;
    }

    /**
     * Returns a selection of tableSize, where the records in the input indexes are to be removed
     * <p>
     * Tablesize should be
     *
     * @param indexes   The indexes to be received
     * @param tableSize The size of the source table before selection
     * @return Selection
     */
    static Selection without(int tableSize, List<Integer> indexes) {
        Selection selection = Selection.withRange(0, tableSize);
        Selection exclusion = Selection.with(indexes);
        selection.andNot(exclusion);
        return selection;
    }

    static Selection with(int... rows) {
        BitmapBackedSelection selection = new BitmapBackedSelection();
        for (int i : rows) {
            selection.add(i);
        }
        return selection;
    }

    static Selection without(int tableSize, int... rows) {
        Selection selection = Selection.withRange(0, tableSize);
        Selection exclusion = Selection.with(rows);
        selection.andNot(exclusion);
        return selection;
    }

    static Selection withRange(int start, int end) {
        BitmapBackedSelection selection = new BitmapBackedSelection();
        selection.addRange(start, end);
        return selection;
    }

    static Selection withoutRange(int tableSize, int start, int end) {
        Selection selection = Selection.withRange(0, tableSize);
        Selection exclusion = Selection.withRange(start, end);
        selection.andNot(exclusion);
        return selection;
    }

    static Selection withRow(int row) {
        BitmapBackedSelection selection = new BitmapBackedSelection();
        selection.add(row);
        return selection;
    }

    static Selection withoutRow(int tableSize, int row) {
        Selection selection = Selection.withRange(0, tableSize);
        Selection exclusion = Selection.with(row);
        selection.andNot(exclusion);
        return selection;
    }

    int[] toArray();

    RoaringBitmap toBitmap();

    /**
     * Adds the given integer to the Selection if it is not already present, and does nothing otherwise
     */
    void add(int i);

    int size();

    /**
     * Intersects the receiver and {@code otherSelection}, updating the receiver
     */
    void and(Selection otherSelection);

    /**
     * Implements the union of the receiver and {@code otherSelection}, updating the receiver
     */
    void or(Selection otherSelection);

    /**
     * Implements the set difference operation between the receiver and {@code otherSelection}, updating the receiver
     */
    void andNot(Selection otherSelection);

    boolean isEmpty();

    void clear();

    boolean contains(int i);

    /**
     * Adds to the current bitmap all integers in [rangeStart,rangeEnd)
     *
     * @param start inclusive beginning of range
     * @param end   exclusive ending of range
     */
    void addRange(int start, int end);

    int get(int i);

    void remove(long start, long end);

    void add(long start, long end);

    void flip();

    @Override
    default Selection apply(int size) {
        return this;
    }
}

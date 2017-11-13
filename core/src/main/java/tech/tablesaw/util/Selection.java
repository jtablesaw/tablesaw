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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterable;
import org.roaringbitmap.RoaringBitmap;

/**
 * A selection maintains an ordered set of ints that can be used to select rows from a table or column
 */
public interface Selection extends IntIterable {

    int[] toArray();

    RoaringBitmap toBitmap();

    /**
     * Returns an IntArrayList containing the ints in this selection
     */
    IntArrayList toIntArrayList();

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
}

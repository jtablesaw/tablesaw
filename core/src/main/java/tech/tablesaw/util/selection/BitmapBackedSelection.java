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

package tech.tablesaw.util.selection;

import it.unimi.dsi.fastutil.ints.IntIterator;
import org.roaringbitmap.RoaringBitmap;

public class BitmapBackedSelection implements Selection {

    private final RoaringBitmap bitmap;

    /**
     * Returns a selection initialized from 0 to the given size, which cane be used for
     * queries that exclude certain items, by first selecting the items to exclude,
     * then flipping the bits.
     *
     * @param size The size    The end point, exclusive
     */
    public BitmapBackedSelection(int size) {
        this.bitmap = new RoaringBitmap();
        addRange(0, size);
    }

    public BitmapBackedSelection(RoaringBitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BitmapBackedSelection() {
        this.bitmap = new RoaringBitmap();
    }

    public void remove(long start, long end) {
        this.bitmap.remove(start, end);
    }

    public void add(long start, long end) {
        this.bitmap.add(start, end);
    }

    public void flip() {
        this.bitmap.flip((long) 0, bitmap.getCardinality());
    }

    public void add(int i) {
        bitmap.add(i);
    }

    @Override
    public String toString() {
        return "Selection of size: " + bitmap.getCardinality();
    }

    @Override
    public int size() {
        return bitmap.getCardinality();
    }

    @Override
    public int[] toArray() {
        return bitmap.toArray();
    }

    @Override
    public RoaringBitmap toBitmap() {
        return bitmap.clone();
    }

    /**
     * Intersects the receiver and {@code otherSelection}, updating the receiver
     */
    @Override
    public void and(Selection otherSelection) {
        bitmap.and(otherSelection.toBitmap());
    }

    /**
     * Implements the union of the receiver and {@code otherSelection}, updating the receiver
     */
    @Override
    public void or(Selection otherSelection) {
        bitmap.or(otherSelection.toBitmap());
    }

    /**
     * Implements the set difference operation between the receiver and {@code otherSelection}, updating the receiver
     */
    @Override
    public void andNot(Selection otherSelection) {
        bitmap.andNot(otherSelection.toBitmap());
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        bitmap.clear();
    }

    @Override
    public boolean contains(int i) {
        return bitmap.contains(i);
    }

    /**
     * Adds to the current bitmap all integers in [rangeStart,rangeEnd)
     *
     * @param start inclusive beginning of range
     * @param end   exclusive ending of range
     */
    @Override
    public void addRange(int start, int end) {
        bitmap.add((long) start, end);
    }

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
}

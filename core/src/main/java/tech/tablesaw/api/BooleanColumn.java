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

package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.booleans.BooleanOpenHashSet;
import it.unimi.dsi.fastutil.booleans.BooleanSet;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.BooleanPredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.mapping.BooleanMapUtils;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static tech.tablesaw.columns.BooleanColumnUtils.isMissing;
import static tech.tablesaw.columns.BooleanColumnUtils.isNotMissing;

/**
 * A column in a base table that contains float values
 */
public class BooleanColumn extends AbstractColumn implements BooleanMapUtils, IntConvertibleColumn {

    public static final byte MISSING_VALUE = Byte.MIN_VALUE;

    private static final int BYTE_SIZE = 1;

    private static int DEFAULT_ARRAY_SIZE = 128;

    private ByteComparator reverseByteComparator = new ByteComparator() {

        @Override
        public int compare(Byte o1, Byte o2) {
            return Byte.compare(o2, o1);
        }

        @Override
        public int compare(byte o1, byte o2) {
            return Byte.compare(o2, o1);
        }
    };

    private ByteArrayList data;

    IntComparator comparator = new IntComparator() {

        @Override
        public int compare(Integer r1, Integer r2) {
            return compare((int) r1, (int) r2);
        }

        @Override
        public int compare(int r1, int r2) {
            boolean f1 = get(r1);
            boolean f2 = get(r2);
            return Boolean.compare(f1, f2);
        }
    };

    public BooleanColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new ByteArrayList(DEFAULT_ARRAY_SIZE);
    }

    public BooleanColumn(String name) {
        this(name, new ByteArrayList(DEFAULT_ARRAY_SIZE));
    }

    public BooleanColumn(String name, int initialSize) {
        this(name, new ByteArrayList(initialSize));
    }

    private BooleanColumn(String name, ByteArrayList values) {
        super(name);
        data = values;
    }

    public BooleanColumn(String name, Selection hits, int columnSize) {
        super(name);
        if (columnSize == 0) {
            return;
        }
        ByteArrayList data = new ByteArrayList(columnSize);

        for (int i = 0; i < columnSize; i++) {
            data.add((byte) 0);
        }

        for (Integer hit : hits) {
            byte b = (byte) 1;
            int i = hit;
            data.set(i, b);
        }
        this.data = data;
    }

    public BooleanColumn(String name, boolean[] array) {
        super(name);
        this.data = new ByteArrayList(array.length);
        for (boolean b : array) {
            append(b);
        }
    }

    public static byte convert(String stringValue) {

        // TODO(lwhite): Improve handling of missing booleans by using a supporting index (bytearray) to indicate missing?
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return 0;

        } else if (TypeUtils.TRUE_STRINGS.contains(stringValue)) {
            return 1;
        } else if (TypeUtils.FALSE_STRINGS.contains(stringValue)) {
            return 0;
        } else {
            throw new IllegalArgumentException("Attempting to convert non-boolean value " +
                    stringValue + " to Boolean");
        }
    }

    public int size() {
        return data.size();
    }

    @Override
    public Table summary() {

        Byte2IntMap counts = new Byte2IntOpenHashMap(3);
        counts.put((byte) 0, 0);
        counts.put((byte) 1, 0);

        for (byte next : data) {
            counts.put(next, counts.get(next) + 1);
        }

        Table table = Table.create(name());

        BooleanColumn booleanColumn = new BooleanColumn("Value");
        IntColumn countColumn = new IntColumn("Count");
        table.addColumn(booleanColumn);
        table.addColumn(countColumn);

        for (Map.Entry<Byte, Integer> entry : counts.byte2IntEntrySet()) {
            booleanColumn.append(entry.getKey());
            countColumn.append(entry.getValue());
        }
        return table;
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (getByte(i) == MISSING_VALUE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int countUnique() {
        ByteSet count = new ByteOpenHashSet(3);
        for (byte next : data) {
            count.add(next);
        }
        return count.size();
    }

    @Override
    public BooleanColumn unique() {
        ByteSet count = new ByteOpenHashSet(3);
        for (byte next : data) {
            count.add(next);
        }
        ByteArrayList list = new ByteArrayList(count);
        return new BooleanColumn(name() + " Unique values", list);
    }

    @Override
    public ColumnType type() {
        return ColumnType.BOOLEAN;
    }

    public void append(boolean b) {
        if (b) {
            data.add((byte) 1);
        } else {
            data.add((byte) 0);
        }
    }

    public void append(byte b) {
        data.add(b);
    }

    @Override
    public String getString(int row) {
        return String.valueOf(get(row));
    }

    @Override
    public BooleanColumn emptyCopy() {
        BooleanColumn column = new BooleanColumn(name());
        column.setComment(comment());
        return column;
    }

    @Override
    public BooleanColumn emptyCopy(int rowSize) {
        BooleanColumn column = new BooleanColumn(name(), rowSize);
        column.setComment(comment());
        return column;
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public BooleanColumn copy() {
        BooleanColumn column = new BooleanColumn(name(), data);
        column.setComment(comment());
        return column;
    }

    @Override
    public void sortAscending() {
        ByteArrays.mergeSort(data.elements());
    }

    @Override
    public void sortDescending() {
        ByteArrays.mergeSort(data.elements(), reverseByteComparator);
    }

    public void appendCell(String object) {
        append(convert(object));
    }

    /**
     * Returns the value in row i as a Boolean
     *
     * @param i the row number
     * @return A Boolean object (may be null)
     */
    public Boolean get(int i) {
        byte b = data.getByte(i);
        if (b == 1) {
            return Boolean.TRUE;
        }
        if (b == 0) {
            return Boolean.FALSE;
        }
        return null;
    }

    /**
     * Returns the value in row i as a byte (0, 1, or Byte.MIN_VALUE representing missing data)
     *
     * @param i the row number
     */
    public byte getByte(int i) {
        return data.getByte(i);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int countTrue() {
        int count = 0;
        for (byte b : data) {
            if (b == 1) {
                count++;
            }
        }
        return count;
    }

    public int countFalse() {
        int count = 0;
        for (byte b : data) {
            if (b == 0) {
                count++;
            }
        }
        return count;
    }

    public Selection isFalse() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (byte next : data) {
            if (next == 0) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isTrue() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (byte next : data) {
            if (next == 1) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isEqualTo(BooleanColumn other) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        ByteIterator booleanIterator = other.byteIterator();
        for (byte next : data) {
            if (next == booleanIterator.nextByte()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    /**
     * Returns a ByteArrayList containing 0 (false), 1 (true) or Byte.MIN_VALUE (missing)
     */
    public ByteArrayList data() {
        return data;
    }

    public void set(int i, boolean b) {
        data.set(i, b ? (byte) 1 : (byte) 0);
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     **/
    public void set(boolean newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        BooleanColumn booleanColumn = (BooleanColumn) column;
        for (int i = 0; i < booleanColumn.size(); i++) {
            append(booleanColumn.get(i));
        }
    }

    public double[] asDoubleArray() {
        double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getByte(i);
        }
        return output;
    }

    // TODO(lwhite): this won't scale
    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (byte next : data) {
            if (next == (byte) 0) {
                builder.append(false);
            } else if (next == (byte) 1) {
                builder.append(true);
            } else {
                builder.append("NA");
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public Selection isMissing() {  //TODO
        return select(isMissing);
    }

    @Override
    public Selection isNotMissing() { //TODO
        return select(isNotMissing);
    }

    public Iterator<Boolean> iterator() {
        return new BooleanColumnIterator(this.byteIterator());
    }

    public ByteIterator byteIterator() {
        return data.iterator();
    }

    @Override
    public String toString() {
        return "Boolean column: " + name();
    }

    public BooleanSet asSet() {
        BooleanSet set = new BooleanOpenHashSet(3);
        BooleanColumn unique = unique();
        for (int i = 0; i < unique.size(); i++) {
            set.add(unique.get(i));
        }
        return set;
    }

    public boolean contains(boolean aBoolean) {
        return data().contains(aBoolean);
    }

    @Override
    public int byteSize() {
        return BYTE_SIZE;
    }

    @Override
    public byte[] asBytes(int row) {
        byte[] result = new byte[1];
        result[0] = (byte) (get(row) ? 1 : 0);
        return result;
    }

    public Selection select(BooleanPredicate predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            byte next = data.getByte(idx);
            if (predicate.test(next)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    @Override
    public int[] asIntArray() {
        int[] output = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getByte(i);
        }
        return output;
    }

    public IntColumn asIntColumn() {
        IntColumn intColumn = new IntColumn(this.name() + ": ints", size());
        ByteArrayList data = data();
        for (int i = 0; i < size(); i++) {
            intColumn.append(data.getByte(i));
        }
        return intColumn;
    }

    private static class BooleanColumnIterator implements Iterator<Boolean> {

        final ByteIterator iterator;

        BooleanColumnIterator(ByteIterator iterator) {
            this.iterator = iterator;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws java.util.NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Boolean next() {
            byte b = iterator.nextByte();
            if (b == (byte) 0) {
                return false;
            }
            if (b == (byte) 1) {
                return true;
            }
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanColumn that = (BooleanColumn) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}

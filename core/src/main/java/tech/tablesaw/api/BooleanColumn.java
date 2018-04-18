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

import static com.google.common.base.Preconditions.checkArgument;
import static tech.tablesaw.api.ColumnType.BOOLEAN;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Strings;

import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanOpenHashSet;
import it.unimi.dsi.fastutil.booleans.BooleanSet;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.booleans.BooleanColumnUtils;
import tech.tablesaw.columns.booleans.BooleanFillers;
import tech.tablesaw.columns.booleans.BooleanFormatter;
import tech.tablesaw.columns.booleans.BooleanMapUtils;
import tech.tablesaw.filtering.predicates.BytePredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * A column in a base table that contains float values
 */
public class BooleanColumn extends AbstractColumn implements BooleanMapUtils, IntConvertibleColumn, BooleanFillers<BooleanColumn>, BooleanIterable {

    public static final byte MISSING_VALUE = (Byte) BOOLEAN.getMissingValue();

    private static final byte BYTE_TRUE = 1;
    private static final byte BYTE_FALSE = 0;

    private final ByteComparator descendingByteComparator = (o1, o2) -> Byte.compare(o2, o1);

    private ByteArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        boolean f1 = get(r1);
        boolean f2 = get(r2);
        return Boolean.compare(f1, f2);
    };

    private BooleanFormatter formatter = new BooleanFormatter("true", "false", "");

    private BooleanColumn(String name, ByteArrayList values) {
        super(BOOLEAN, name);
        data = values;
    }

    public static boolean valueIsMissing(byte b) {
        return b == MISSING_VALUE;
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return valueIsMissing(getByte(rowNumber));
    }

    public static BooleanColumn create(String name, Selection hits, int columnSize) {
        BooleanColumn column = create(name, columnSize);

        checkArgument(
                (hits.size() <= columnSize),
                "Cannot have more true values than total values in a boolean column");

        for (int i = 0; i < columnSize; i++) {
            column.append((byte) 0);
        }

        for (int hit : hits) {
            column.set(hit, true);
        }
        return column;
    }

    public static BooleanColumn create(String name) {
        return new BooleanColumn(name, new ByteArrayList(DEFAULT_ARRAY_SIZE));
    }

    public static BooleanColumn create(String name, int initialSize) {
        return new BooleanColumn(name, new ByteArrayList(initialSize));
    }
    public static BooleanColumn create(String name, boolean[] values) {

        BooleanColumn column = create(name, values.length);
        for (boolean b : values) {
            column.append(b);
        }
        return column;
    }

    public static BooleanColumn create(String name, List<Boolean> values) {
        BooleanColumn column = create(name, values.size());
        for (Boolean b : values) {
            column.append(b);
        }
        return column;
    }

    public static BooleanColumn create(String name, Boolean[] objects) {
        BooleanColumn column = create(name, objects.length);
        for (Boolean b : objects) {
            column.append(b);
        }
        return column;
    }

    public void setPrintFormatter(BooleanFormatter formatter) {
        this.formatter = formatter;
    }

    public BooleanFormatter getPrintFormatter() {
        return formatter;
    }

    public static byte convert(String stringValue) {

        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return MISSING_VALUE;

        } else if (TypeUtils.TRUE_STRINGS.contains(stringValue)) {
            return BYTE_TRUE;
        } else if (TypeUtils.FALSE_STRINGS.contains(stringValue)) {
            return BYTE_FALSE;
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
        counts.put(BYTE_FALSE, 0);
        counts.put(BYTE_TRUE, 0);

        for (byte next : data) {
            counts.put(next, counts.get(next) + 1);
        }

        Table table = Table.create(name());

        BooleanColumn booleanColumn = create("Value");
        NumberColumn countColumn = DoubleColumn.create("Count");
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
            if (valueIsMissing(getByte(i))) {
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
        return BOOLEAN;
    }

    public void append(boolean b) {
        if (b) {
            data.add(BYTE_TRUE);
        } else {
            data.add(BYTE_FALSE);
        }
    }

    public void append(Boolean b) {
        if (b == null) {
            data.add(MISSING_VALUE);
        }
        else if (b) {
            data.add(BYTE_TRUE);
        } else {
            data.add(BYTE_FALSE);
        }
    }

    public void append(byte b) {
        data.add(b);
    }

    @Override
    public void appendMissing() {
        append(MISSING_VALUE);
    }

    @Override
    public String getString(int row) {
        return formatter.format(get(row));
    }

    @Override
    public String getUnformattedString(int row) {
        return String.valueOf(get(row));
    }

    @Override
    public BooleanColumn emptyCopy() {
        return create(name());
    }

    @Override
    public BooleanColumn emptyCopy(int rowSize) {
        return create(name(), rowSize);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public BooleanColumn copy() {
        return new BooleanColumn(name(), data);
    }

    @Override
    public void sortAscending() {
        ByteArrays.mergeSort(data.elements());
    }

    @Override
    public void sortDescending() {
        ByteArrays.mergeSort(data.elements(), descendingByteComparator);
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
        if (b == BYTE_TRUE) {
            return Boolean.TRUE;
        }
        if (b == BYTE_FALSE) {
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
            if (b == BYTE_TRUE) {
                count++;
            }
        }
        return count;
    }

    public int countFalse() {
        int count = 0;
        for (byte b : data) {
            if (b == BYTE_FALSE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the proportion of non-missing row elements that contain true
     */
    public double proportionTrue() {
        double n = size() - countMissing();
        double trueCount = countTrue();
        return trueCount / n;
    }

    /**
     * Returns the proportion of non-missing row elements that contain true
     */
    public double proportionFalse() {
        return 1.0 - proportionTrue();
    }

    /**
     * Returns true if the column contains any true values, and false otherwise
     */
    public boolean any() {
        return countTrue() > 0;
    }

    /**
     * Returns true if the column contains only true values, and false otherwise. If there are any missing values
     * it returns false.
     */
    public boolean all() {
        return countTrue() == size();
    }

    /**
     * Returns true if the column contains no true values, and false otherwise
     */
    public boolean none() {
        return countTrue() == 0;
    }

    public Selection isFalse() {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (byte next : data) {
            if (next == BYTE_FALSE) {
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
            if (next == BYTE_TRUE) {
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
        if (b) {
            data.set(i, BYTE_TRUE);
        } else {
            data.set(i, BYTE_FALSE);
        }
    }

    public BooleanColumn lead(int n) {
        BooleanColumn column = lag(-n);
        column.setName(name() + " lead(" + n + ")");
        return column;
    }

    public BooleanColumn lag(int n) {
        int srcPos = n >= 0 ? 0 : 0 - n;
        byte[] dest = new byte[size()];
        int destPos = n <= 0 ? 0 : n;
        int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        System.arraycopy(data.toByteArray(), srcPos, dest, destPos, length);

        BooleanColumn copy = emptyCopy(size());
        copy.data = new ByteArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
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
    public double getDouble(int row) {
        return getByte(row);
    }

    @Override
    public double[] asDoubleArray() {
        double[] doubles = new double[data.size()];
        for (int i = 0; i < size(); i++) {
            doubles[i] = data.getByte(i);
        }
        return doubles;
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    @Override
    public void append(Column column) {
        checkArgument(column.type() == this.type());
        BooleanColumn booleanColumn = (BooleanColumn) column;
        for (int i = 0; i < booleanColumn.size(); i++) {
            append(booleanColumn.get(i));
        }
    }

    public Selection asSelection() {
        Selection selection = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            byte value = getByte(i);
            if (value == 1) {
                selection.add(i);
            }
        }
        return selection;
    }

    @Override
    public Selection isMissing() {
        return eval(BooleanColumnUtils.isMissing);
    }

    @Override
    public Selection isNotMissing() {
        return eval(BooleanColumnUtils.isNotMissing);
    }

    public BooleanIterator iterator() {
        return new BooleanColumnIterator(this.byteIterator());
    }

    public ByteIterator byteIterator() {
        return data.iterator();
    }

    public BooleanSet asSet() {
        BooleanSet set = new BooleanOpenHashSet(3);
        BooleanColumn unique = unique();
        for (int i = 0; i < unique.size(); i++) {
            set.add((boolean) unique.get(i));
        }
        return set;
    }

    public boolean contains(boolean aBoolean) {
        if (aBoolean) {
            return data().contains(BYTE_TRUE);
        }
        return data().contains(BYTE_FALSE);
    }

    @Override
    public int byteSize() {
        return type().byteSize();
    }

    @Override
    public byte[] asBytes(int row) {
        byte[] result = new byte[byteSize()];
        result[0] = (get(row) ? BYTE_TRUE : BYTE_FALSE);
        return result;
    }

    public BooleanColumn where(Selection selection) {
        return (BooleanColumn) subset(selection);
    }

    @Override
    public BooleanColumn removeMissing() {
        BooleanColumn noMissing = emptyCopy();
        ByteListIterator iterator = byteListIterator();
        while(iterator.hasNext()) {
            byte b = iterator.nextByte();
            if (!valueIsMissing(b)) {
                noMissing.append(b);
            }
        }
        return noMissing;
    }

    public Selection eval(BytePredicate predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            byte next = data.getByte(idx);
            if (predicate.test(next)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Selection eval(Predicate<Boolean> predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            if (predicate.test(get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Selection eval(BiPredicate<Boolean, Boolean> predicate, Boolean valueToCompare) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            if (predicate.test(get(idx), valueToCompare)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    /**
     * Returns a byteListIterator, which allows iteration by byte (value) and int (index)
     */
    private ByteListIterator byteListIterator() {
        return data.iterator();
    }

    @Override
    public int[] asIntArray() {
        int[] output = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getByte(i);
        }
        return output;
    }

    public NumberColumn asNumberColumn() {
        NumberColumn numberColumn = DoubleColumn.create(this.name() + ": ints", size());
        ByteArrayList data = data();
        for (int i = 0; i < size(); i++) {
            numberColumn.append(data.getByte(i));
        }
        return numberColumn;
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

    private static class BooleanColumnIterator implements BooleanIterator {

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
        public boolean nextBoolean() {
            byte b = iterator.nextByte();
            if (b == (byte) 0) {
                return false;
            }
            if (b == (byte) 1) {
                return true;
            }
            return false;
        }
    }
    
    // fillWith methods
    
	@Override
	public BooleanColumn fillWith(BooleanIterator iterator) {
		for (int r = 0; r < size(); r++) {
			if (! iterator.hasNext()) {
				break;
			}
			set(r, iterator.nextBoolean());
		}
		return this;
	}

	@Override
	public BooleanColumn fillWith(BooleanIterable iterable) {
		BooleanIterator iterator = null;
		for (int r = 0; r < size(); r++) {
			if (iterator == null || (! iterator.hasNext())) {
				iterator = iterable.iterator();
				if (! iterator.hasNext()) {
					break;
				}
			}
			set(r, iterator.nextBoolean());
		}
		return this;
	}

	@Override
	public BooleanColumn fillWith(Supplier<Boolean> supplier) {
		for (int r = 0; r < size(); r++) {
			try {
				set(r, supplier.get());
			} catch (Exception e) {
				break;
			}
		}
		return this;
	}
}

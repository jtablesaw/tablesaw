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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2IntMap;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.strings.StringColumnFormatter;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.strings.StringFilters;
import tech.tablesaw.columns.strings.StringMapFunctions;
import tech.tablesaw.columns.strings.StringReduceUtils;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import static tech.tablesaw.api.ColumnType.STRING;

/**
 * A column that contains String values. They are assumed to be 'categorical' rather than free-form text, so are
 * stored in an encoding that takes advantage of the expected repetition of string values.
 * <p>
 * Because the MISSING_VALUE for this column type is an empty string, there is little or no need for special handling
 * of missing values in this class's methods.
 */
public class StringColumn extends AbstractColumn<String>
        implements CategoricalColumn<String>, StringFilters, StringMapFunctions, StringReduceUtils {

    public static final String MISSING_VALUE = (String) STRING.getMissingValueIndicator();

    //private final AtomicInteger nextIndex = new AtomicInteger(Short.MIN_VALUE + 2);
    private final AtomicInteger nextIndex = new AtomicInteger(1);

    // holds a key for each element in the column. the key can be used to lookup the backing string value
    private ShortArrayList values;

    // a bidirectional map of keys to backing string values.
    private final DictionaryMap lookupTable = new DictionaryMap();

    private StringColumnFormatter printFormatter = new StringColumnFormatter();

    private final IntComparator rowComparator = (i, i1) -> {
        String f1 = get(i);
        String f2 = get(i1);
        return f1.compareTo(f2);
    };

    public static boolean valueIsMissing(String string) {
        return MISSING_VALUE.equals(string);
    }

    @Override
    public StringColumn appendMissing() {
        append(MISSING_VALUE);
        return this;
    }

    private final ShortComparator reverseDictionarySortComparator = (i, i1) -> -lookupTable.get(i).compareTo(lookupTable.get(i1));

    private final ShortComparator dictionarySortComparator = (i, i1) -> lookupTable.get(i).compareTo(lookupTable.get(i1));

    public static StringColumn create(String name) {
        return create(name, DEFAULT_ARRAY_SIZE);
    }

    public static StringColumn create(String name, String[] strings) {
        return create(name, Arrays.asList(strings));
    }

    public static StringColumn create(String name, List<String> strings) {
        return new StringColumn(name, strings);
    }

    public static StringColumn create(String name, int size) {
        return new StringColumn(name, new ArrayList<>(size));
    }

    private StringColumn(String name, List<String> strings) {
        super(STRING, name);
        values = new ShortArrayList(strings.size());
        for (String string : strings) {
            append(string);
        }
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return get(rowNumber).equals(MISSING_VALUE);
    }

    public void setPrintFormatter(StringColumnFormatter formatter) {
        Preconditions.checkNotNull(formatter);
        this.printFormatter = formatter;
    }

    public StringColumnFormatter getPrintFormatter() {
        return printFormatter;
    }

    @Override
    public String getString(int row) {
        return printFormatter.format(get(row));
    }

    @Override
    public String getUnformattedString(int row) {
        return String.valueOf(get(row));
    }

    @Override
    public StringColumn emptyCopy() {
        return create(name());
    }

    @Override
    public StringColumn emptyCopy(int rowSize) {
        return create(name(), rowSize);
    }

    @Override
    public void sortAscending() {
        short[] elements = values.toShortArray();
        ShortArrays.parallelQuickSort(elements, dictionarySortComparator);
        this.values = new ShortArrayList(elements);
    }

    @Override
    public void sortDescending() {
        short[] elements = values.toShortArray();
        ShortArrays.parallelQuickSort(elements, reverseDictionarySortComparator);
        this.values = new ShortArrayList(elements);
    }

    /**
     * Returns the number of elements (a.k.a. rows or cells) in the column
     *
     * @return size as int
     */
    @Override
    public int size() {
        return values.size();
    }

    /**
     * Returns the value at rowIndex in this column. The index is zero-based.
     *
     * @param rowIndex index of the row
     * @return value as String
     * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
     */
    public String get(int rowIndex) {
        short k = values.getShort(rowIndex);
        return lookupTable.get(k);
    }

    /**
     * Returns a List&lt;String&gt; representation of all the values in this column
     * <p>
     * NOTE: Unless you really need a string consider using the column itself for large datasets as it uses much less memory
     *
     * @return values as a list of String.
     */
    public List<String> asList() {
        List<String> strings = new ArrayList<>();
        for (String category : this) {
            strings.add(category);
        }
        return strings;
    }

    @Override
    public Table summary() {
        return countByCategory();
    }

    /**
     */
    @Override
    public Table countByCategory() {
        Table t = new Table("Column: " + name());
        StringColumn categories = create("Category");
        IntColumn counts = IntColumn.create("Count");

        Short2IntMap valueToCount = new Short2IntOpenHashMap();

        for (short next : values) {
            if (valueToCount.containsKey(next)) {
                valueToCount.put(next, valueToCount.get(next) + 1);
            } else {
                valueToCount.put(next, 1);
            }
        }
        for (Map.Entry<Short, Integer> entry : valueToCount.short2IntEntrySet()) {
            categories.append(lookupTable.get(entry.getKey()));
            counts.append(entry.getValue());
        }
        if (countMissing() > 0) {
            categories.append("* missing values");
            counts.append(countMissing());
        }
        t.addColumns(categories);
        t.addColumns(counts);
        return t;
    }

    @Override
    public void clear() {
        values.clear();
        lookupTable.clear();
    }

    public StringColumn lead(int n) {
        StringColumn column = lag(-n);
        column.setName(name() + " lead(" + n + ")");
        return column;
    }

    public StringColumn lag(int n) {

        StringColumn copy = emptyCopy(size());
        copy.setName(name() + " lag(" + n + ")");

        if (n >= 0) {
            for (int m = 0; m < n; m++) {
                copy.appendCell(MISSING_VALUE);
            }
            for (int i = 0; i < size(); i++) {
                if (i + n >= size()) {
                    break;
                }
                copy.appendCell(get(i));
            }
        } else {
            for (int i = -n; i < size(); i++) {
                copy.appendCell(get(i));
            }
            for (int m = 0; m > n; m--) {
                copy.appendCell(MISSING_VALUE);
            }
        }

        return copy;
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Examples:
     * myCatColumn.set(myCatColumn.isEqualTo("Cat"), "Dog"); // no more cats
     * myCatColumn.set(myCatColumn.valueIsMissing(), "Fox"); // no more missing values
     */
    public StringColumn set(Selection rowSelection, String newValue) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
        return this;
    }

    public StringColumn set(int rowIndex, String stringValue) {
        String str = MISSING_VALUE;
        if (stringValue != null) {
            str = stringValue;
        }
        short valueId = lookupTable.get(str);
        if (valueId <= 0) {
            valueId = getValueId();
            lookupTable.put(valueId, str);
        }
        values.set(rowIndex, valueId);
        return this;
    }

    private short getValueId() {
        int nextValue = nextIndex.getAndIncrement();
        if (nextValue > Short.MAX_VALUE) {
            throw new IndexOutOfBoundsException("String column can only contain %d unique values");
        }
        return (short) nextValue;
    }

    @Override
    public int countUnique() {
        return lookupTable.size();
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public List<String> top(int n) {
        List<String> top = new ArrayList<>();
        StringColumn copy = this.copy();
        copy.sortDescending();
        for (int i = 0; i < n; i++) {
            top.add(copy.get(i));
        }
        return top;
    }

    /**
     * Returns the smallest ("bottom") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the smallest n observations
     */
    public List<String> bottom(int n) {
        List<String> bottom = new ArrayList<>();
        StringColumn copy = this.copy();
        copy.sortAscending();
        for (int i = 0; i < n; i++) {
            bottom.add(copy.get(i));
        }
        return bottom;
    }

    private void addValue(String value) {
        short key = lookupTable.get(value);
        if (key <= 0) {
            key = getValueId();
            lookupTable.put(key, value);
        }
        values.add(key);
    }

    /**
     * Initializes this Column with the given values for performance
     */
    public void initializeWith(ShortArrayList list, StringColumn old) {
        for (short key : list) {
            append(old.lookupTable.get(key));
        }
    }

    /**
     * Returns true if this column contains a cell with the given string, and false otherwise
     *
     * @param aString the value to look for
     * @return true if contains, false otherwise
     */
    public boolean contains(String aString) {
        return firstIndexOf(aString) >= 0;
    }

    /**
     * Returns all the values associated with the given indexes.
     *
     * @param indexes the indexes
     * @return values as {@link ShortArrayList}
     */
    public ShortArrayList getValues(ShortArrayList indexes) {
        ShortArrayList newList = new ShortArrayList(indexes.size());
        for (int i : indexes) {
            newList.add(values.getShort(i));
        }
        return newList;
    }

    /**
     * Add all the strings in the list to this column
     *
     * @param stringValues a list of values
     */
    public StringColumn addAll(List<String> stringValues) {
        for (String stringValue : stringValues) {
            append(stringValue);
        }
        return this;
    }

    @Override
    public StringColumn appendCell(String object) {
        addValue(StringColumnType.DEFAULT_PARSER.parse(object));
        return this;
    }

    @Override
    public StringColumn appendCell(String object, StringParser<?> parser) {
        return appendObj(parser.parse(object));
    }

    @Override
    public IntComparator rowComparator() {
        return rowComparator;
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }


    public Selection isEqualTo(String string) {
        Selection results = new BitmapBackedSelection();
        int key = lookupTable.get(string);
        addValuesToSelection(results, key);
        return results;
    }

    public Selection isNotEqualTo(String string) {
        Selection selection = new BitmapBackedSelection();
        selection.addRange(0, size());
        selection.andNot(isEqualTo(string));
        return selection;
    }

    /**
     * Returns a list of boolean columns suitable for use as dummy variables in, for example, regression analysis,
     * select a column of categorical data must be encoded as a list of columns, such that each column represents
     * a single category and indicates whether it is present (1) or not present (0)
     *
     * @return a list of {@link BooleanColumn}
     */
    public List<BooleanColumn> getDummies() {
        List<BooleanColumn> results = new ArrayList<>();

        // createFromCsv the necessary columns
        for (Short2ObjectMap.Entry<String> entry : lookupTable.keyToValueMap().short2ObjectEntrySet()) {
            BooleanColumn column = BooleanColumn.create(entry.getValue());
            results.add(column);
        }

        // iterate over the values, updating the dummy variable columns as appropriate
        for (short next : values) {
            String category = lookupTable.get(next);
            for (BooleanColumn column : results) {
                if (category.equals(column.name())) {
                    //TODO(lwhite): update the correct row more efficiently, by using set rather than add & only
                    // updating true
                    column.append(true);
                } else {
                    column.append(false);
                }
            }
        }
        return results;
    }

    /**
     * Returns the int key for the string at rowNumber. The key will be the same for all records with the same string,
     * and different if the string is different
     */
    private int getShort(int rowNumber) {
        return values.getShort(rowNumber);
    }

    /**
     * Returns a new Column containing all the unique values in this column
     *
     * @return a column with unique values.
     */
    @Override
    public StringColumn unique() {
        List<String> strings = new ArrayList<>(lookupTable.categories());
        return StringColumn.create(name() + " Unique values", strings);
    }

    /**
     * Returns the integers that back this column.
     *
     * @return data as {@link IntArrayList}
     */
    public ShortArrayList data() {
        return values;
    }

    public DoubleColumn asNumberColumn() {
        DoubleColumn numberColumn = DoubleColumn.create(this.name() + ": codes", size());
        ShortArrayList data = data();
        for (int i = 0; i < size(); i++) {
            numberColumn.append(data.getShort(i));
        }
        return numberColumn;
    }

    public StringColumn where(Selection selection) {
        return subset(selection.toArray());
    }

    @Override
    public StringColumn copy() {
        StringColumn newCol = create(name(), size());
        for (String string : this) {
            newCol.append(string);
        }
        return newCol;
    }

    @Override
    public StringColumn append(Column<String> column) {
        Preconditions.checkArgument(column.type() == this.type());
        StringColumn source = (StringColumn) column;
        for (String string : source) {
            append(string);
        }
        return this;
    }

    @Override
    public Column<String> append(Column<String> column, int row) {
        return append(column.getUnformattedString(row));
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (MISSING_VALUE.equals(get(i))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public StringColumn removeMissing() {
        StringColumn noMissing = emptyCopy();
        for (String v : this) {
            if (valueIsMissing(v)) {
                noMissing.append(v);
            }
        }
        return noMissing;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private final ShortListIterator valuesIt = values.iterator();

            @Override
            public boolean hasNext() {
                return valuesIt.hasNext();
            }

            @Override
            public String next() {
                return lookupTable.get(valuesIt.nextShort());
            }
        };
    }

    public Set<String> asSet() {
        return lookupTable.categories();
    }

    /**
     * Returns the integer encoded value of each cell in this column. It can be used to lookup the mapped string in
     * the lookupTable
     *
     * @return values a {@link IntArrayList}
     */
    private ShortArrayList values() {
        return values;
    }

    @Override
    public int byteSize() {
        return type().byteSize();
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(byteSize()).putInt(getShort(rowNumber)).array();
    }

    public double getDouble(int i) {
        return getShort(i);
    }

    public double[] asDoubleArray() {
        double[] doubles = new double[values.size()];
        for (int i = 0; i < size(); i++) {
            doubles[i] = values.getShort(i);
        }
        return doubles;
    }

    /**
     * Given a key matching some string, add to the selection the index of every record that matches that key
     */
    private void addValuesToSelection(Selection results, int key) {
        if (key >= 0) {
            int i = 0;
            for (int next : values) {
                if (key == next) {
                    results.add(i);
                }
                i++;
            }
        }
    }

    /**
     * Added for naming consistency with all other columns
     */
    public StringColumn append(String value) {
        appendCell(value);
        return this;
    }

    @Override
    public StringColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (!(obj instanceof String)) {
            throw new IllegalArgumentException("Cannot append " + obj.getClass().getName() + " to StringColumn");
        }
        return append((String) obj);
    }

    @Override
    public Selection isIn(String... strings) {
        return selectIsIn(strings);
    }

    private Selection selectIsIn(String... strings) {
        IntArrayList keys = new IntArrayList();
        for (String string : strings) {
            int key = lookupTable.get(string);
            if (key > 0) {
                keys.add(key);
            }
        }

        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < values.size(); i++) {
            if (keys.contains(values.getShort(i))) {
                results.add(i);
            }
        }
        return results;
    }

    @Override
    public Selection isNotIn(String... strings) {
        Selection results = new BitmapBackedSelection();
        results.addRange(0, size());
        results.andNot(selectIsIn(strings));
        return results;
    }

    public Short2ObjectMap<String> keyToValueMap() {
        return new Short2ObjectOpenHashMap<>(lookupTable.keyToValue);
    }

    public int firstIndexOf(String value) {
        return values.indexOf(lookupTable.get(value));
    }

    public double countOccurrences(String value) {
        if (!lookupTable.contains(value)) {
            return 0;
        }
        int key = lookupTable.get(value);
        int count = 0;
        for (int k : values) {
            if (k == key) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Object[] asObjectArray() {
        final String[] output = new String[values().size()];
        for (int i = 0; i < values().size(); i++) {
            output[i] = get(i);
        }
        return output;

    }

    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }

    @Override
    public StringColumn setName(String name) {
        return (StringColumn) super.setName(name);
    }

    @Override
    public StringColumn filter(Predicate<? super String> test) {
        return (StringColumn) super.filter(test);
    }

    @Override
    public StringColumn subset(int[] rows) {
        return (StringColumn) super.subset(rows);
    }

    @Override
    public StringColumn sorted(Comparator<? super String> comp) {
        return (StringColumn) super.sorted(comp);
    }

    @Override
    public StringColumn map(Function<? super String, ? extends String> fun) {
        return (StringColumn) super.map(fun);
    }

    @Override
    public StringColumn min(Column<String> other) {
        return (StringColumn) super.min(other);
    }

    @Override
    public StringColumn max(Column<String> other) {
        return (StringColumn) super.max(other);
    }

    @Override
    public StringColumn set(Selection condition, Column<String> other) {
        return (StringColumn) super.set(condition, other);
    }

    @Override
    public StringColumn first(int numRows) {
        return (StringColumn) super.first(numRows);
    }

    @Override
    public StringColumn last(int numRows) {
        return (StringColumn) super.last(numRows);
    }

    @Override
    public StringColumn inRange(int start, int end) {
        return (StringColumn) super.inRange(start, end);
    }

    @Override
    public StringColumn sampleN(int n) {
        return (StringColumn) super.sampleN(n);
    }

    @Override
    public StringColumn sampleX(double proportion) {
        return (StringColumn) super.sampleX(proportion);
    }

    /**
     * A map that supports reversible key value pairs of int-String
     */
    static class DictionaryMap {

        private final Short2ObjectMap<String> keyToValue = new Short2ObjectOpenHashMap<>();

        private final Object2ShortOpenHashMap<String> valueToKey = new Object2ShortOpenHashMap<>();

        DictionaryMap() {
            super();
            valueToKey.defaultReturnValue((short) -1);
        }

        /**
         * Returns a new DictionaryMap that is a deep copy of the original
         */
        DictionaryMap(DictionaryMap original) {
            for (Short2ObjectMap.Entry<String> entry : original.keyToValue.short2ObjectEntrySet()) {
                keyToValue.put(entry.getShortKey(), entry.getValue());
                valueToKey.put(entry.getValue(), entry.getShortKey());
            }
            //valueToKey.defaultReturnValue(Short.MIN_VALUE);
            valueToKey.defaultReturnValue((short) -1);
        }

        void put(short key, String value) {
            keyToValue.put(key, value);
            valueToKey.put(value, key);
        }

        String get(short key) {
            return keyToValue.get(key);
        }

        short get(String value) {
            return valueToKey.getShort(value);
        }

        void remove(short key) {
            String value = keyToValue.remove(key);
            valueToKey.removeShort(value);
        }

        void remove(String value) {
            short key = valueToKey.removeShort(value);
            keyToValue.remove(key);
        }

        void clear() {
            keyToValue.clear();
            valueToKey.clear();
        }

        /**
         * Returns true if we have seen this stringValue before, and it hasn't been removed.
         * <p>
         * NOTE: An answer of true does not imply that the column still contains the value, only that
         * it is in the dictionary map
         */
        boolean contains(String stringValue) {
            return valueToKey.containsKey(stringValue);
        }

        int size() {
            return categories().size();
        }

        Set<String> categories() {
            return valueToKey.keySet();
        }

        /**
         * Returns the strings in the dictionary as an array in order of the numeric key
         *
         * @deprecated This is an implementation detail that should not be public.
         * If you need the strings you can get them by calling unique() or asSet() on the column,
         */
        @Deprecated
        String[] categoryArray() {
            return keyToValue.values().toArray(new String[size()]);
        }

        ShortCollection values() {
            return valueToKey.values();
        }

        Short2ObjectMap<String> keyToValueMap() {
            return keyToValue;
        }

        Object2ShortMap<String> valueToKeyMap() {
            return valueToKey;
        }
    }
}

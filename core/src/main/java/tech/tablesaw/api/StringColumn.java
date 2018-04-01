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

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.string.CategoryColumnUtils;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.StringBiPredicate;
import tech.tablesaw.filtering.StringPredicate;
import tech.tablesaw.columns.string.filters.CategoryFilters;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.selection.BitmapBackedSelection;
import tech.tablesaw.util.selection.Selection;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * A column that contains String values. They are assumed to be 'categorical' rather than free-form filters, so are
 * stored in an encoding that takes advantage of the expected repetition of string values.
 * <p>
 * Because the MISSING_VALUE for this column type is an empty string, there is little or no need for special handling
 * of missing values in this class's methods.
 */
public class StringColumn extends AbstractColumn
        implements CategoryFilters, CategoryColumnUtils, IntConvertibleColumn, Iterable<String> {

    public static final String MISSING_VALUE = (String) ColumnType.CATEGORY.getMissingValue();
    private static final int BYTE_SIZE = 4;
    private static int DEFAULT_ARRAY_SIZE = 128;

    private int id = 0;

    // holds a key for each row in the table. the key can be used to lookup the backing string value
    private IntArrayList values;

    // a bidirectional map of keys to backing string values.
    private DictionaryMap lookupTable = new DictionaryMap();

    public final IntComparator rowComparator = new IntComparator() {

        @Override
        public int compare(int i, int i1) {
            String f1 = get(i);
            String f2 = get(i1);
            return f1.compareTo(f2);
        }

        @Override
        public int compare(Integer i, Integer i1) {
            return compare((int) i, (int) i1);
        }
    };
    private final IntComparator reverseDictionarySortComparator = new IntComparator() {
        @Override
        public int compare(int i, int i1) {
            return -lookupTable.get(i).compareTo(lookupTable.get(i1));
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return compare((int) o1, (int) o2);
        }
    };
    private IntComparator dictionarySortComparator = new IntComparator() {
        @Override
        public int compare(int i, int i1) {
            return lookupTable.get(i).compareTo(lookupTable.get(i1));
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return compare((int) o1, (int) o2);
        }
    };

    public StringColumn(String name) {
        super(name);
        values = new IntArrayList(DEFAULT_ARRAY_SIZE);
    }

    public StringColumn(String name, String[] categories) {
        this(name, Arrays.asList(categories));
    }

    public StringColumn(String name, List<String> categories) {
        super(name);
        values = new IntArrayList(categories.size());
        for (String string : categories) {
            append(string);
        }
    }

    public StringColumn(ColumnMetadata metadata) {
        super(metadata);
        values = new IntArrayList(DEFAULT_ARRAY_SIZE);
    }

    public StringColumn(String name, int size) {
        super(name);
        values = new IntArrayList(size);
    }

    public static String convert(String stringValue) {
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return MISSING_VALUE;
        }
        return stringValue;
    }

    @Override
    public ColumnType type() {
        return ColumnType.CATEGORY;
    }

    @Override
    public String getString(int row) {
        return get(row);
    }

    @Override
    public StringColumn emptyCopy() {
        StringColumn copy = new StringColumn(name());
        copy.setComment(comment());
        return copy;
    }

    @Override
    public StringColumn emptyCopy(int rowSize) {
        StringColumn copy = new StringColumn(name(), rowSize);
        copy.setComment(comment());
        return copy;
    }

    @Override
    public void sortAscending() {
        IntArrays.parallelQuickSort(values.elements(), dictionarySortComparator);
    }

    @Override
    public void sortDescending() {
        IntArrays.parallelQuickSort(values.elements(), reverseDictionarySortComparator);
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
        int k = values.getInt(rowIndex);
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
    public int[] asIntArray() {
        return data().toIntArray();
    }

    @Override
    public Table summary() {
        return countByCategory();
    }

    /**
     */
    public Table countByCategory() {
        Table t = new Table("Column: " + name());
        StringColumn categories = new StringColumn("Category");
        IntColumn counts = new IntColumn("Count");

        Int2IntMap valueToCount = new Int2IntOpenHashMap();

        for (int next : values) {
            if (valueToCount.containsKey(next)) {
                valueToCount.put(next, valueToCount.get(next) + 1);
            } else {
                valueToCount.put(next, 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : valueToCount.int2IntEntrySet()) {
            categories.append(lookupTable.get(entry.getKey()));
            counts.append(entry.getValue());
        }
        if (countMissing() > 0) {
            categories.append("* missing values");
            counts.append(countMissing());
        }
        t.addColumn(categories);
        t.addColumn(counts);
        return t;
    }

    @Override
    public void clear() {
        values.clear();
        lookupTable.clear();
    }

    public Selection isEqualTo(StringColumn other) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        Iterator<String> iterator = other.iterator();
        for (String next : this) {
            if (next.equals(iterator.next())) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Examples:
     * myCatColumn.set("Dog", myCatColumn.isEqualTo("Cat")); // no more cats
     * myCatColumn.set("Fox", myCatColumn.isMissing()); // no more missing values
     */
    public void set(String newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    public void set(int rowIndex, String stringValue) {
        if (stringValue == null) {
            stringValue = MISSING_VALUE;
        }
        boolean b = lookupTable.contains(stringValue);
        int valueId;
        if (!b) {
// TODO(lwhite): synchronize id() or column-level saveTable lock so we can increment id safely without atomic integer
// objects
            valueId = id++;
            lookupTable.put(valueId, stringValue);
        } else {
            valueId = lookupTable.get(stringValue);
        }
        values.set(rowIndex, valueId);
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

    /**
     * @deprecated Use append(String value) instead
     */
    public void add(String stringValue) {
        addValue(convert(stringValue));
    }

    /**
     * Puts Strings directly into the backing data structure.
     * Do not use
     * This method is unsafe, and will be removed in a future release without replacement
     */
    public void UnsafePutToDictionary(int key, String value) {
        lookupTable.put(key, value);
    }

    private void addValue(String value) {
        int key = lookupTable.get(value);
        if (key < 0) {
            key = id++;
            lookupTable.put(key, value);
        }
        values.add(key);
    }

    /**
     * Initializes this Column with the given values for performance
     */
    public void initializeWith(IntArrayList list, DictionaryMap map) {
        for (int key : list) {
            add(map.get(key));
        }
    }

    /**
     * Returns true if this column contains a cell with the given string, and false otherwise
     *
     * @param aString the value to look for
     * @return true if contains, false otherwhise
     */
    public boolean contains(String aString) {
        return values.indexOf(dictionaryMap().get(aString)) >= 0;
    }

    /**
     * Returns all the values associated with the given indexes.
     *
     * @param indexes the indexes
     * @return values as {@link IntArrayList}
     */
    public IntArrayList getValues(IntArrayList indexes) {
        IntArrayList newList = new IntArrayList(indexes.size());
        for (int i : indexes) {
            newList.add(values.getInt(i));
        }
        return newList;
    }

    /**
     * Add all the strings in the list to this column
     *
     * @param stringValues a list of values
     */
    public void addAll(List<String> stringValues) {
        for (String stringValue : stringValues) {
            add(stringValue);
        }
    }

    @Override
    public void appendCell(String object) {
        addValue(convert(object));
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
     * selectWhere a column of categorical data must be encoded as a list of columns, such that each column represents
     * a single category and indicates whether it is present (1) or not present (0)
     *
     * @return a list of {@link BooleanColumn}
     */
    public List<BooleanColumn> getDummies() {
        List<BooleanColumn> results = new ArrayList<>();

        // createFromCsv the necessary columns
        for (Int2ObjectMap.Entry<String> entry : lookupTable.keyToValueMap().int2ObjectEntrySet()) {
            BooleanColumn column = new BooleanColumn(entry.getValue());
            results.add(column);
        }

        // iterate over the values, updating the dummy variable columns as appropriate
        for (int next : values) {
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

    public int getInt(int rowNumber) {
        return values.getInt(rowNumber);
    }

    /**
     * Returns a new Column containing all the unique values in this column
     *
     * @return a column with unique values.
     */
    @Override
    public StringColumn unique() {
        List<String> strings = new ArrayList<>(lookupTable.categories());
        return new StringColumn(name() + " Unique values", strings);
    }

    /**
     * Returns the integers that back this column.
     *
     * @return data as {@link IntArrayList}
     */
    public IntArrayList data() {
        return values;
    }


    public IntColumn asIntColumn() {
        IntColumn intColumn = new IntColumn(this.name() + ": codes", size());
        IntArrayList data = data();
        for (int i = 0; i < size(); i++) {
            intColumn.append(data.getInt(i));
        }
        return intColumn;
    }

    /**
     * @deprecated This is an implementation detail that should not be public. Going away soon.
     */
    @Deprecated
    public DictionaryMap dictionaryMap() {
        return lookupTable;
    }

    @Override
    public String toString() {
        return "Category column: " + name();
    }

    /**
     * Returns the raw indexes that this column contains.
     *
     * @return indexes as int[]
     */
    public int[] indexes() {
        int[] rowIndexes = new int[size()];
        for (int i = 0; i < size(); i++) {
            rowIndexes[i] = i;
        }
        return rowIndexes;
    }

    /**
     * Return a copy of this column with the given string appended
     *
     * @param append the column to append
     * @return the new column
     */
    public StringColumn appendString(StringColumn append) {
        StringColumn newColumn = new StringColumn(name() + "[column appended]", this.size());
        for (int r = 0; r < size(); r++) {
            newColumn.add(get(r) + append.get(r));
        }
        return newColumn;
    }

    /**
     * Return a copy of this column with the given string appended
     *
     * @param append the string to append
     * @return the new column
     */
    public StringColumn appendString(String append) {
        StringColumn newColumn = new StringColumn(name() + "[append]", this.size());
        for (int r = 0; r < size(); r++) {
            newColumn.add(get(r) + append);
        }
        return newColumn;
    }

    /**
     * Creates a new column, replacing each string in this column with a new string formed by
     * replacing any substring that matches the regex
     *
     * @param regexArray  the regex array to replace
     * @param replacement the replacement array
     * @return the new column
     */
    public StringColumn replaceAll(String[] regexArray, String replacement) {

        StringColumn newColumn = new StringColumn(name() + "[repl]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = get(r);
            for (String regex : regexArray) {
                value = value.replaceAll(regex, replacement);
            }
            newColumn.add(value);
        }
        return newColumn;
    }

    public StringColumn tokenizeAndSort(String separator) {
        StringColumn newColumn = new StringColumn(name() + "[sorted]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = get(r);

            Splitter splitter = Splitter.on(separator);
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens =
                    new ArrayList<>(splitter.splitToList(value));
            Collections.sort(tokens);
            value = String.join(" ", tokens);
            newColumn.add(value);
        }
        return newColumn;
    }

    /**
     * Splits on Whitespace and returns the lexicographically sorted result.
     *
     * @return a {@link StringColumn}
     */
    public StringColumn tokenizeAndSort() {
        StringColumn newColumn = new StringColumn(name() + "[sorted]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = get(r);
            Splitter splitter = Splitter.on(CharMatcher.whitespace());
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens = new ArrayList<>(splitter.splitToList(value));
            Collections.sort(tokens);
            value = String.join(" ", tokens);
            newColumn.add(value);
        }
        return newColumn;
    }

    public StringColumn tokenizeAndRemoveDuplicates() {
        StringColumn newColumn = new StringColumn(name() + "[without duplicates]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = get(r);

            Splitter splitter = Splitter.on(CharMatcher.whitespace());
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens = new ArrayList<>(splitter.splitToList(value));

            value = String.join(" ", new HashSet<>(tokens));
            newColumn.add(value);
        }
        return newColumn;
    }

    @Override
    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (int next : values) {
            builder.append(get(next));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public Selection isMissing() {
        return select(isMissing);
    }

    @Override
    public Selection isNotMissing() {
        return select(isNotMissing);
    }

    public Selection select(StringPredicate predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data().size(); idx++) {
            if (predicate.test(get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Selection select(StringBiPredicate predicate, String value) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data().size(); idx++) {
            if (predicate.test(get(idx), value)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    @Override
    public StringColumn copy() {
        StringColumn newCol = new StringColumn(name(), size());
        newCol.lookupTable = new DictionaryMap(lookupTable);
        newCol.values.addAll(values);
        newCol.setComment(comment());
        return newCol;
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        StringColumn intColumn = (StringColumn) column;
        for (int i = 0; i < intColumn.size(); i++) {
            add(intColumn.get(i));
        }
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
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private final IntListIterator valuesIt = values.iterator();

            @Override
            public boolean hasNext() {
                return valuesIt.hasNext();
            }

            @Override
            public String next() {
                return lookupTable.get(valuesIt.nextInt());
            }
        };
    }

    public StringColumn selectIf(StringPredicate predicate) {
        StringColumn column = emptyCopy();
        for (String next : this) {
            if (predicate.test(next)) {
                column.add(next);
            }
        }
        return column;
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
    @Override
    public IntArrayList values() {
        return values;
    }

    @Override
    public int byteSize() {
        return BYTE_SIZE;
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(4).putInt(getInt(rowNumber)).array();
    }

    public Selection isIn(String... strings) {
        Selection results = new BitmapBackedSelection();
        for (String string : strings) {
            int key = lookupTable.get(string);
            addValuesToSelection(results, key);
        }
        return results;
    }

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

    public Selection isIn(Collection<String> strings) {
        return isIn(strings.toArray(new String[strings.size()]));
    }

    /**
     * Added for naming consistency with all other columns
     */
    public void append(String value) {
        appendCell(value);
    }

    public Selection isNotIn(String... strings) {
        Selection results = new BitmapBackedSelection();
        for (String string : strings) {
            int key = lookupTable.get(string);
            if (key >= 0) {
                int i = 0;
                for (int next : values) {
                    if (key != next) {
                        results.add(i);
                    }
                    i++;
                }
            }
        }

        return results;
    }

    public Selection isNotIn(Collection<String> strings) {
        return isNotIn(strings.toArray(new String[strings.size()]));
    }

    public Int2ObjectMap<String> keyToValueMap() {
        return new Int2ObjectOpenHashMap<>(lookupTable.keyToValue);
    }

    /**
     * A map that supports reversible key value pairs of int-String
     */
    static class DictionaryMap {

        private final Int2ObjectMap<String> keyToValue = new Int2ObjectOpenHashMap<>();

        private final Object2IntMap<String> valueToKey = new Object2IntOpenHashMap<>();

        DictionaryMap() {
            super();
            valueToKey.defaultReturnValue(-1);
        }

        /**
         * Returns a new DictionaryMap that is a deep copy of the original
         */
        DictionaryMap(DictionaryMap original) {
            for (Int2ObjectMap.Entry<String> entry : original.keyToValue.int2ObjectEntrySet()) {
                keyToValue.put(entry.getIntKey(), entry.getValue());
                valueToKey.put(entry.getValue(), entry.getIntKey());
            }
            valueToKey.defaultReturnValue(-1);
        }

        void put(int key, String value) {
            keyToValue.put(key, value);
            valueToKey.put(value, key);
        }

        String get(int key) {
            return keyToValue.get(key);
        }

        int get(String value) {
            return valueToKey.getInt(value);
        }

        void remove(short key) {
            String value = keyToValue.remove(key);
            valueToKey.removeInt(value);
        }

        void remove(String value) {
            int key = valueToKey.removeInt(value);
            keyToValue.remove(key);
        }

        void clear() {
            keyToValue.clear();
            valueToKey.clear();
        }

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

        IntCollection values() {
            return valueToKey.values();
        }

        Int2ObjectMap<String> keyToValueMap() {
            return keyToValue;
        }

        Object2IntMap<String> valueToKeyMap() {
            return valueToKey;
        }
    }
}

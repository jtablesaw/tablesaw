package tech.tablesaw.columns.strings;

import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A map that supports reversible key value pairs of int-String
 */
public class ByteDictionaryMap implements DictionaryMap {

    // The maximum number of unique values or categories that I can hold. If the column has more unique values,
    // use a TextColumn
    private static final int MAX_UNIQUE = Byte.MAX_VALUE - Byte.MIN_VALUE;

    private static final byte MISSING_VALUE = Byte.MAX_VALUE;

    private static final byte DEFAULT_RETURN_VALUE = Byte.MIN_VALUE;

    private final ByteComparator reverseDictionarySortComparator = (i, i1) -> -getValueForByteKey(i).compareTo(getValueForByteKey(i1));

    private final ByteComparator dictionarySortComparator = (i, i1) -> getValueForByteKey(i).compareTo(getValueForByteKey(i1));

    // holds a key for each element in the column. the key can be used to lookup the backing string value
    private ByteArrayList values = new ByteArrayList();

    private final AtomicInteger nextIndex = new AtomicInteger(DEFAULT_RETURN_VALUE);

    // we maintain two maps, one from strings to keys, and the second from keys to strings.
    private final Byte2ObjectMap<String> keyToValue = new Byte2ObjectOpenHashMap<>();

    private final Object2ByteOpenHashMap<String> valueToKey = new Object2ByteOpenHashMap<>();

    public ByteDictionaryMap() {
        valueToKey.defaultReturnValue(DEFAULT_RETURN_VALUE);
    }

    private void put(byte key, String value) {
        keyToValue.put(key, value);
        valueToKey.put(value, key);
    }

    private byte getKeyForValue(String value) {
        return valueToKey.getByte(value);
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

    @Override
    public String getValueForIndex(int rowIndex) {
        byte k = values.getByte(rowIndex);
        return getValueForKey(k);
    }

    @Override
    public int getKeyForIndex(int rowIndex) {
        return values.getByte(rowIndex);
    }

    /**
     * Returns true if we have seen this stringValue before, and it hasn't been removed.
     * <p>
     * NOTE: An answer of true does not imply that the column still contains the value, only that
     * it is in the dictionary map
     */
    private boolean contains(String stringValue) {
        return valueToKey.containsKey(stringValue);
    }

    private Set<String> categories() {
        return valueToKey.keySet();
    }

    private Byte2ObjectMap<String> keyToValueMap() {
        return keyToValue;
    }

    @Override
    public void sortAscending() {
        byte[] elements = values.toByteArray();
        ByteArrays.parallelQuickSort(elements, dictionarySortComparator);
        this.values = new ByteArrayList(elements);
    }

    @Override
    public String getValueForKey(int key) {
        return keyToValue.get((byte) key);
    }

    private String getValueForByteKey(byte key) {
        return keyToValue.get(key);
    }

    @Override
    public void sortDescending() {
        byte[] elements = values.toByteArray();
        ByteArrays.parallelQuickSort(elements, reverseDictionarySortComparator);
        this.values = new ByteArrayList(elements);
    }

    public int countOccurrences(String value) {
        if (!contains(value)) {
            return 0;
        }
        byte key = getKeyForValue(value);
        int count = 0;
        for (byte k : values) {
            if (k == key) {
                count++;
            }
        }
        return count;
    }

    public Set<String> asSet() {
        return categories();
    }

    public int firstIndexOf(String value) {
        return values.indexOf(getKeyForValue(value));
    }

    @Override
    public String[] asObjectArray() {
        final String[] output = new String[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getValueForIndex(i);
        }
        return output;
    }

    @Override
    public Selection selectIsIn(String... strings) {
        ByteOpenHashSet keys = new ByteOpenHashSet();
        for (String string : strings) {
            byte key = getKeyForValue(string);
            if (key != DEFAULT_RETURN_VALUE) {
                keys.add(key);
            }
        }

        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < values.size(); i++) {
            if (keys.contains(values.getByte(i))) {
                results.add(i);
            }
        }
        return results;
    }

    @Override
    public Selection selectIsIn(Collection<String> strings) {
        ByteOpenHashSet keys = new ByteOpenHashSet();

        for (String string : strings) {
            byte key = getKeyForValue(string);
            if (key != DEFAULT_RETURN_VALUE) {
                keys.add(key);
            }
        }

        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < values.size(); i++) {
            if (keys.contains(values.getByte(i))) {
                results.add(i);
            }
        }
        return results;
    }

    @Override
    public void append(String value) throws NoKeysAvailableException {
        byte key;
        if (value == null || StringColumnType.missingValueIndicator().equals(value)) {
            key = MISSING_VALUE;
            put(key, StringColumnType.missingValueIndicator());
        } else {
            key = getKeyForValue(value);
        }
        if (key == DEFAULT_RETURN_VALUE) {
            key = getValueId();
            put(key, value);
        }
        values.add(key);
    }

    private byte getValueId() throws NoKeysAvailableException {
        int nextValue = nextIndex.incrementAndGet();
        if (nextValue >= Byte.MAX_VALUE) {
            String msg = String.format("String column can only contain %d unique values. Column has more.", MAX_UNIQUE);
            throw new NoKeysAvailableException(msg);
        }
        return (byte) nextValue;
    }

    /**
     * Given a key matching some string, add to the selection the index of every record that matches that key
     */
    private void addValuesToSelection(Selection results, byte key) {
        if (key != DEFAULT_RETURN_VALUE) {
            int i = 0;
            for (byte next : values) {
                if (key == next) {
                    results.add(i);
                }
                i++;
            }
        }
    }


    @Override
    public void set(int rowIndex, String stringValue) throws NoKeysAvailableException {
        String str = StringColumnType.missingValueIndicator();
        if (stringValue != null) {
            str = stringValue;
        }
        byte valueId = getKeyForValue(str);

        if (valueId == DEFAULT_RETURN_VALUE) { // this is a new value not in dictionary
            valueId = getValueId();
            put(valueId, str);
        }
        values.set(rowIndex, valueId);
    }

    @Override
    public void clear() {
        values.clear();
        keyToValue.clear();
        valueToKey.clear();
    }

    @Override
    public int countUnique() {
        return keyToValueMap().size();
    }

    /**
     */
    @Override
    public Table countByCategory(String columnName) {
        Table t = Table.create("Column: " + columnName);
        StringColumn categories = StringColumn.create("Category");
        IntColumn counts = IntColumn.create("Count");

        Byte2IntMap valueToCount = new Byte2IntOpenHashMap();

        for (byte next : values) {
            if (valueToCount.containsKey(next)) {
                valueToCount.put(next, valueToCount.get(next) + 1);
            } else {
                valueToCount.put(next, 1);
            }
        }
        for (Map.Entry<Byte, Integer> entry : valueToCount.byte2IntEntrySet()) {
            categories.append(getValueForKey(entry.getKey()));
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
    public Selection isEqualTo(String string) {
        Selection results = new BitmapBackedSelection();
        byte key = getKeyForValue(string);
        addValuesToSelection(results, key);
        return results;
    }

    /**
     * Returns a list of boolean columns suitable for use as dummy variables in, for example, regression analysis,
     * select a column of categorical data must be encoded as a list of columns, such that each column represents
     * a single category and indicates whether it is present (1) or not present (0)
     *
     * @return a list of {@link BooleanColumn}
     */
    @Override
    public List<BooleanColumn> getDummies() {
        List<BooleanColumn> results = new ArrayList<>();

        // createFromCsv the necessary columns
        for (Byte2ObjectMap.Entry<String> entry : keyToValueMap().byte2ObjectEntrySet()) {
            BooleanColumn column = BooleanColumn.create(entry.getValue());
            results.add(column);
        }

        // iterate over the values, updating the dummy variable columns as appropriate
        for (byte next : values) {
            String category = getValueForKey(next);
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
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(byteSize()).put((byte) getKeyForIndex(rowNumber)).array();
    }

    private int byteSize() {
        return 1;
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (MISSING_VALUE == getKeyForIndex(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private final ByteListIterator valuesIt = values.iterator();

            @Override
            public boolean hasNext() {
                return valuesIt.hasNext();
            }

            @Override
            public String next() {
                return getValueForKey(valuesIt.nextByte());
            }
        };
    }

    @Override
    public void appendMissing() {
        try {
            append(StringColumnType.missingValueIndicator());
        } catch (NoKeysAvailableException e) {
            // This can't happen because missing value key is the first one allocated
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return getKeyForIndex(rowNumber) == MISSING_VALUE;
    }

    @Override
    public DictionaryMap promoteYourself() {

        ShortDictionaryMap dictionaryMap;

        try {
            dictionaryMap = new ShortDictionaryMap(this);
        } catch (NoKeysAvailableException e) {
            // this should never happen;
            throw new IllegalStateException(e);
        }
        return dictionaryMap;
    }
}

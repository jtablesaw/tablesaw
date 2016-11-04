package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.AbstractColumn;
import com.github.lwhite1.tablesaw.columns.CategoryColumnUtils;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.filtering.StringBiPredicate;
import com.github.lwhite1.tablesaw.filtering.StringPredicate;
import com.github.lwhite1.tablesaw.filtering.text.CategoryFilters;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.util.BitmapBackedSelection;
import com.github.lwhite1.tablesaw.util.Dictionary;
import com.github.lwhite1.tablesaw.util.OffHeapDictionaryMap;
import com.github.lwhite1.tablesaw.util.Selection;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.*;
import org.mapdb.DBMaker;
import org.mapdb.IndexTreeList;
import org.mapdb.Serializer;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * A column in a base table that contains String values
 */
public class CategoryColumn extends AbstractColumn
    implements CategoryFilters, CategoryColumnUtils, Iterable<String> {

  private static final int BYTE_SIZE = 4;

  public static final String MISSING_VALUE = (String) ColumnType.CATEGORY.getMissingValue();

  private static int DEFAULT_ARRAY_SIZE = 128;

  private int id = 0;

  // holds a key for each row in the table. the key can be used to lookup the backing string value
//  private IntArrayList values;

  private IndexTreeList<Integer> values;

  // a bidirectional map of keys to backing string values.
//  private DictionaryMap lookupTable = new DictionaryMap();
  private OffHeapDictionaryMap lookupTable;

  public static CategoryColumn create(String name) {
    return create(name, DEFAULT_ARRAY_SIZE);
  }

  public static CategoryColumn create(String name, int size) {
    return new CategoryColumn(name, size);
  }

  public static CategoryColumn create(String name, List<String> categories) {
    CategoryColumn column = new CategoryColumn(name, categories.size());
    categories.forEach(column::add);
    return column;
  }

  private CategoryColumn(String name) {
    super(name);
//    values = new IntArrayList(DEFAULT_ARRAY_SIZE);
    values = DBMaker.tempFileDB()
        .fileMmapEnableIfSupported()
        .closeOnJvmShutdown()
        .concurrencyScale(16)
        .make()
        .indexTreeList("values", Serializer.INTEGER_PACKED)
        .create();

    String id = id();
    lookupTable = new OffHeapDictionaryMap(id);
  }

  public CategoryColumn(ColumnMetadata metadata) {
    super(metadata);
//    values = new IntArrayList(metadata.getSize());
    values = DBMaker.tempFileDB()
        .fileMmapEnableIfSupported()
        .closeOnJvmShutdown()
        .concurrencyScale(16)
        .make()
        .indexTreeList("values", Serializer.INTEGER_PACKED)
        .create();
    String id = id();
    lookupTable = new OffHeapDictionaryMap(id);
  }

  public CategoryColumn(String name, int size) {
    super(name);
//    values = new IntArrayList(size);
    values = DBMaker.tempFileDB()
        .fileMmapEnableIfSupported()
        .closeOnJvmShutdown()
        .concurrencyScale(16)
        .make()
        .indexTreeList("values", Serializer.INTEGER_PACKED)
        .create();
    String id = id();
    lookupTable = new OffHeapDictionaryMap(id);
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
  public CategoryColumn emptyCopy() {
    return new CategoryColumn(name());
  }

  @Override
  public CategoryColumn emptyCopy(int rowSize) {
    return CategoryColumn.create(name(), rowSize);
  }

  @Override
  public void sortAscending() {
    sort(dictionarySortComparator);
  }

  private void sort(IntComparator cmp) {
    IntArrayList data = data();
    IntArrays.parallelQuickSort(data.elements(), cmp);
    values.clear();
    values.addAll(data);
  }

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

  private IntComparator reverseDictionarySortComparator = new IntComparator() {
    @Override
    public int compare(int i, int i1) {
      return -lookupTable.get(i).compareTo(lookupTable.get(i1));
    }

    @Override
    public int compare(Integer o1, Integer o2) {
      return compare((int) o1, (int) o2);
    }
  };

  @Override
  public void sortDescending() {
    sort(reverseDictionarySortComparator);
  }

  /**
   * Returns the number of elements (a.k.a. rows or cells) in the column
   */
  @Override
  public int size() {
    return values.size();
  }

  /**
   * Returns the value at rowIndex in this column. The index is zero-based.
   *
   * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
   */
  public String get(int rowIndex) {
//    int k = values.getInt(rowIndex);
    int k = values.get(rowIndex);
    return lookupTable.get(k);
  }

  public List<String> toList() {
    return Lists.newArrayList(lookupTable.categoryArray());
  }

  @Override
  public Table summary() {
    return countByCategory();
  }

  Table countByCategory() {
    Table t = new Table("Column: " + name());
    CategoryColumn categories = CategoryColumn.create("Category");
    IntColumn counts = IntColumn.create("Count");

    Int2IntMap valueToCount = new Int2IntOpenHashMap();
    for (int next : values) {
      if (valueToCount.containsKey(next)) {
        valueToCount.put(next, valueToCount.get(next) + 1);
      } else {
        valueToCount.put(next, 1);
      }
    }

    for (Map.Entry<Integer, Integer> entry : valueToCount.entrySet()) {
      categories.add(lookupTable.get(entry.getKey()));
      counts.add(entry.getValue());
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

  public void set(int rowIndex, String stringValue) {
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
    CategoryColumn copy = this.copy();
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
    CategoryColumn copy = this.copy();
    copy.sortAscending();
    for (int i = 0; i < n; i++) {
      bottom.add(copy.get(i));
    }
    return bottom;
  }

  public void add(String stringValue) {
    int valueId = lookupTable.get(stringValue);
    if (valueId < 0) {
      valueId = id++;
      lookupTable.put(valueId, stringValue);
    }
    values.add(valueId);
  }

  /**
   * Initializes this Column with the given values for performance
   */
  public void initializeWith(IntArrayList list, Dictionary map) {
//    values = list;
//    lookupTable = map;
    list.forEach(values::add);
    map.keyToValueMap().forEach(lookupTable::put);
  }

  /**
   * Returns true if this column contains a cell with the given string, and false otherwise
   */
  public boolean contains(String aString) {
    int k = lookupTable.get(aString);
    return values.indexOf(k) >= 0;
  }

  /**
   * Returns all the values associated with the given indexes
   */
  public IntArrayList getValues(IntArrayList indexes) {
    IntArrayList newList = new IntArrayList(indexes.size());
    for (int i : indexes) {
//      newList.add(values.getInt(i));
      newList.add(values.get(i));
    }
    return newList;
  }

  /**
   * Add all the strings in the list to this column
   */
  public void addAll(List<String> stringValues) {
    for (String stringValue : stringValues) {
      add(stringValue);
    }
  }

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

  public static String convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return MISSING_VALUE;
    }
    return stringValue;
  }

  public void addCell(String object) {
    try {
      add(convert(object));
    } catch (NullPointerException e) {
      throw new RuntimeException(name() + ": "
          + String.valueOf(object) + ": "
          + e.getMessage());
    }
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
    if (key >= 0) {
      int i = 0;
      for (int next : values) {
        if (key == next) {
          results.add(i);
        }
        i++;
      }
    }
    return results;
  }

  public Selection isNotEqualTo(String string) {
    Selection results = new BitmapBackedSelection();
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
    return results;
  }

  /**
   * Returns a list of boolean columns suitable for use as dummy variables in, for example, regression analysis,
   * selectWhere a column of categorical data must be encoded as a list of columns, such that each column represents
   * a single
   * category and indicates whether it is present (1) or not present (0)
   */
  public List<BooleanColumn> getDummies() {
    List<BooleanColumn> results = new ArrayList<>();

    // createFromCsv the necessary columns
//    for (Int2ObjectMap.Entry<String> entry : lookupTable.keyToValueMap().int2ObjectEntrySet()) {
//      BooleanColumn column = BooleanColumn.create(entry.getValue());
//      results.add(column);
//    }

    for (String s : lookupTable.categories()) {
      BooleanColumn column = BooleanColumn.create(s);
      results.add(column);
    }

    // iterate over the values, updating the dummy variable columns as appropriate
    for (int next : values) {
      String category = get(next);
      for (BooleanColumn column : results) {
        if (category.equals(column.name())) {
          //TODO(lwhite): update the correct row more efficiently, by using set rather than add & only updating true
          column.add(true);
        } else {
          column.add(false);
        }
      }
    }
    return results;
  }

  public int getInt(int rowNumber) {
//    return values.getInt(rowNumber);
    return values.get(rowNumber);
  }

  public CategoryColumn unique() {
    List<String> strings = new ArrayList<>(lookupTable.categories());
    return CategoryColumn.create(name() + " Unique values", strings);
  }

  /**
   * Returns the integers that back this column
   */
  public IntArrayList data() {
//    return values;
    IntArrayList result = new IntArrayList(values.size());
    values.forEach(result::add);
    return result;
  }

  public IntColumn toIntColumn() {
    IntColumn intColumn = IntColumn.create(this.name() + ": codes", size());
    IntArrayList data = data();
    for (int i = 0; i < size(); i++) {
      intColumn.add(data.getInt(i));
    }
    return intColumn;
  }

  public com.github.lwhite1.tablesaw.util.Dictionary dictionaryMap() {
//    DictionaryMap dictionaryMap = new DictionaryMap();
//    lookupTable.keyToValueMap().forEach(dictionaryMap::put);
//    return dictionaryMap;
    return lookupTable;
  }

  @Override
  public String toString() {
    return "Category column: " + name();
  }

  public int[] indexes() {
    int[] rowIndexes = new int[size()];
    for (int i = 0; i < size(); i++) {
      rowIndexes[i] = i;
    }
    return rowIndexes;
  }

  public CategoryColumn replaceAll(String[] regexArray, String replacement) {

    CategoryColumn newColumn = CategoryColumn.create(name() + "[repl]", this.size());

    for (int r = 0; r < size(); r++) {
      String value = get(r);
      for (String regex : regexArray) {
        value = value.replaceAll(regex, replacement);
      }
      newColumn.add(value);
    }
    return newColumn;
  }

  public CategoryColumn tokenizeAndSort(String separator) {
    CategoryColumn newColumn = CategoryColumn.create(name() + "[sorted]", this.size());

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
   * Splits on Whitespace and returns the lexicographically sorted result
   */
  public CategoryColumn tokenizeAndSort() {
    CategoryColumn newColumn = CategoryColumn.create(name() + "[sorted]", this.size());

    for (int r = 0; r < size(); r++) {
      String value = get(r);
      Splitter splitter = Splitter.on(CharMatcher.WHITESPACE);
      splitter = splitter.trimResults();
      splitter = splitter.omitEmptyStrings();
      List<String> tokens = new ArrayList<>(splitter.splitToList(value));
      Collections.sort(tokens);
      value = String.join(" ", tokens);
      newColumn.add(value);
    }
    return newColumn;
  }

  public CategoryColumn tokenizeAndRemoveDuplicates() {
    CategoryColumn newColumn = CategoryColumn.create(name() + "[without duplicates]", this.size());

    for (int r = 0; r < size(); r++) {
      String value = get(r);

      Splitter splitter = Splitter.on(CharMatcher.WHITESPACE);
      splitter = splitter.trimResults();
      splitter = splitter.omitEmptyStrings();
      List<String> tokens = new ArrayList<>(splitter.splitToList(value));

      value = String.join(" ", new HashSet<>(tokens));
      newColumn.add(value);
    }
    return newColumn;
  }

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
      int next = data().getInt(idx);
      if (predicate.test(get(next))) {
        selection.add(idx);
      }
    }
    return selection;
  }

  public Selection select(StringBiPredicate predicate, String value) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < data().size(); idx++) {
      int next = data().getInt(idx);
      if (predicate.test(get(next), value)) {
        selection.add(idx);
      }
    }
    return selection;
  }

  public CategoryColumn copy() {
    CategoryColumn newCol = CategoryColumn.create(name(), size());
//    newCol.lookupTable = new DictionaryMap(lookupTable);
    lookupTable.keyToValueMap().forEach((key, value) -> newCol.lookupTable.put(key, value));
    newCol.values.addAll(values);
    return newCol;
  }

  @Override
  public void append(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    CategoryColumn intColumn = (CategoryColumn) column;
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

//      private IntListIterator valuesIt = values.iterator();
      private Iterator<Integer> valuesIt = values.iterator();

      @Override
      public boolean hasNext() {
        return valuesIt.hasNext();
      }

      @Override
      public String next() {
        return lookupTable.get(valuesIt.next());
      }
    };
  }

  public CategoryColumn selectIf(StringPredicate predicate) {
    CategoryColumn column = emptyCopy();
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
   */
  public IntArrayList values() {
//    return values;
    return data();
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

  public Selection isIn(String ... strings) {
    IntArrayList keys = new IntArrayList();
    for (String string : strings) {
      int key = lookupTable.get(string);
      if (key >= 0) {
        keys.add(key);
      }
    }

    int i = 0;
    Selection results = new BitmapBackedSelection();
    for (int next : values) {
      if (keys.contains(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }
}

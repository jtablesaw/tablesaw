package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.aggregator.CategoryReduceUtils;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.filter.StringPredicate;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.util.DictionaryMap;
import com.github.lwhite1.tablesaw.filter.text.CategoryFilters;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import org.roaringbitmap.RoaringBitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A column in a base table that contains float values
 */
public class CategoryColumn extends AbstractColumn
    implements CategoryFilters, CategoryReduceUtils, Iterable<String> {

  public static final String MISSING_VALUE = (String) ColumnType.CATEGORY.getMissingValue();

  private static int DEFAULT_ARRAY_SIZE = 128;

  private int id = 0;

  // holds a key for each row in the table. the key can be used to lookup the backing string value
  private IntArrayList values;

  // a bidirectional map of keys to backing string values.
  private DictionaryMap lookupTable = new DictionaryMap();

  public static CategoryColumn create(String name) {
    return create(name, DEFAULT_ARRAY_SIZE);
  }

  public static CategoryColumn create(String name, int size) {
    return new CategoryColumn(name, size);
  }

  public static CategoryColumn create(String name, List<String> categories) {
    CategoryColumn column = new CategoryColumn(name, categories.size());
    for (String string : categories) {
      column.add(string);
    }
    return column;
  }

  private CategoryColumn(String name) {
    super(name);
    values = new IntArrayList(DEFAULT_ARRAY_SIZE);
  }

  public CategoryColumn(ColumnMetadata metadata) {
    super(metadata);
    values = new IntArrayList(DEFAULT_ARRAY_SIZE);
  }

  public CategoryColumn(String name, int size) {
    super(name);
    values = new IntArrayList(size);
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
  public void sortAscending() {
    IntArrays.parallelQuickSort(values.elements(), dictionarySortComparator);
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
    IntArrays.parallelQuickSort(values.elements(), reverseDictionarySortComparator);
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
    int k = values.getInt(rowIndex);
    return lookupTable.get(k);
  }

  @Override
  public Table summary() {
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
  public List<String> max(int n) {
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
  public List<String> min(int n) {
    List<String> bottom = new ArrayList<>();
    CategoryColumn copy = this.copy();
    copy.sortAscending();
    for (int i = 0; i < n; i++) {
      bottom.add(copy.get(i));
    }
    return bottom;
  }

  public void add(String stringValue) {
    boolean b = lookupTable.contains(stringValue);
    int valueId;
    if (!b) {
      valueId = id++;
      lookupTable.put(valueId, stringValue);
    } else {
      valueId = lookupTable.get(stringValue);
    }
    values.add(valueId);
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

  public RoaringBitmap isEqualTo(String string) {
    RoaringBitmap results = new RoaringBitmap();
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

  /**
   * Returns a list of boolean columns suitable for use as dummy variables in, for example, regression analysis,
   * selectWhere a column of categorical data must be encoded as a list of columns, such that each column represents a single
   * category and indicates whether it is present (1) or not present (0)
   */
  public List<BooleanColumn> getDummies() {
    List<BooleanColumn> results = new ArrayList<>();

    // create the necessary columns
    for (Int2ObjectMap.Entry<String> entry : lookupTable.keyToValueMap().int2ObjectEntrySet()) {
      BooleanColumn column = BooleanColumn.create(entry.getValue());
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
    return values.getInt(rowNumber);
  }

  public CategoryColumn unique() {
    List<String> strings = new ArrayList<>(lookupTable.categories());
    return CategoryColumn.create(name() + " Unique values", strings);

  }

  /**
   * Returns the integers that back this column
   */
  public IntArrayList data() {
    return values;
  }

  public DictionaryMap dictionaryMap() {
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

  public CategoryColumn copy() {
    CategoryColumn newCol = CategoryColumn.create(name() + "1", size());
    for (int next : values) {
      newCol.add(get(next));
    }
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

  @Override
  public Iterator<String> iterator() {
    return new Iterator<String>() {

      private IntListIterator valuesIt = values.iterator();

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
}

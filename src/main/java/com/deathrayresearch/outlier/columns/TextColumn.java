package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.aggregator.StringReduceUtils;
import com.deathrayresearch.outlier.filter.IntPredicate;
import com.deathrayresearch.outlier.filter.StringPredicate;
import com.deathrayresearch.outlier.filter.text.StringFilters;
import com.deathrayresearch.outlier.mapper.StringIntMapper;
import com.deathrayresearch.outlier.mapper.StringMapUtils;
import com.deathrayresearch.outlier.mapper.StringStringMapper;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import org.roaringbitmap.RoaringBitmap;

import java.util.*;

/**
 * A column in a base table that contains float values
 */
public class TextColumn extends AbstractColumn
        implements StringMapUtils, StringFilters, StringReduceUtils, Iterable<String> {

  private static final int DEFAULT_ARRAY_SIZE = 128;

  private List<String> data;

  public static TextColumn create(String name) {
    return new TextColumn(name);
  }

  public static TextColumn create(String name, int size) {
    return new TextColumn(name, size);
  }

  public TextColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new ArrayList<>(metadata.getSize());
  }

  private TextColumn(String name) {
    super(name);
    data = new ArrayList<>(DEFAULT_ARRAY_SIZE);
  }

  public TextColumn(String name, int initialSize) {
    super(name);
    data = new ArrayList<>(initialSize);
  }

  public int size() {
    return data.size();
  }

  @Override
  public ColumnType type() {
    return ColumnType.TEXT;
  }

  public void add(String text) {
    data.add(text);
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    List<String> temp = new ArrayList<>(size() * 2);
    temp.addAll(data);
    data = temp;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    List<String> temp = new ArrayList<>(size() + 100);
    temp.addAll(data);
    data = temp;
  }

  @Override
  public String getString(int row) {
    return String.valueOf(data.get(row));
  }

  @Override
  public TextColumn emptyCopy() {
    return new TextColumn(name());
  }

  @Override
  public void clear() {
    data.clear();
  }

  private TextColumn copy() {
    TextColumn copy = emptyCopy();
    for (String aData : data) {
      copy.add(aData);
    }
    return copy;
  }

  @Override
  public void sortAscending() {
    Collections.sort(data);
  }

  @Override
  public void sortDescending() {
    Collections.sort(data, reverseStringComparator);
  }

  /** Sorts strings in reverse lexicographical order */
  private Comparator<String> reverseStringComparator = (o1, o2) -> -o1.compareTo(o2);

  // TODO(lwhite): Implement column summary()
  @Override
  public Table summary() {
    return new Table("");
  }

  @Override
  public int countUnique() {
    Set<String> stringSet = new HashSet<>();
    stringSet.addAll(data);
    return stringSet.size();
  }

  public TextColumn unique() {
    TextColumn textColumn = TextColumn.create(name() + " Unique values");
    Set<String> stringSet = new HashSet<>();
    stringSet.addAll(data);
    for (String string : stringSet) {
      textColumn.add(string);
    }
    return textColumn;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  public void addCell(String s) {
    this.add(s);
  }

  public String get(int index) {
    return data.get(index);
  }

  public RoaringBitmap isEqualTo(String string) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (String next : data) {
      if (string.equals(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  IntComparator comparator = new IntComparator() {
    @Override
    public int compare(int i, int i1) {
      String f1 = data.get(i);
      String f2 = data.get(i1);
      return f1.compareTo(f2);
    }

    @Override
    public int compare(Integer r1, Integer r2) {
      String f1 = data.get(r1);
      String f2 = data.get(r2);
      return f1.compareTo(f2);
    }
  };

  public void set(int i, String s) {
    if (i > data.size()) {
      resize();
    }
    data.set(i, s);
  }

  public int[] indexes() {
    int[] rowIndexes = new int[size()];
    for (int i = 0; i < size(); i++) {
      rowIndexes[i] = i;
    }
    return rowIndexes;
  }

  @Override
  public String toString() {
    return "Text column: " + name();
  }


  public TextColumn replaceAll(String[] regexArray, String replacement) {

    TextColumn newColumn = TextColumn.create(name() + "[repl]", this.size());

    for (int r = 0; r < size(); r++) {
      String value = get(r);
      for (String regex : regexArray) {
        value = value.replaceAll(regex, replacement);
      }
      newColumn.add(value);
    }
    return newColumn;
  }

  public String print() {
    StringBuilder builder = new StringBuilder();
    builder.append(title());
    for (String next : data) {
      builder.append(String.valueOf(next));
      builder.append('\n');
    }
    return builder.toString();
  }

  @Override
  public void appendColumnData(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    TextColumn intColumn = (TextColumn) column;
    for (int i = 0; i < intColumn.size(); i++) {
      add(intColumn.get(i));
    }
  }

  TextColumn collectIntoTextColumn(String newColumnName, StringStringMapper mapper) {
    TextColumn result = new TextColumn(newColumnName);
    for (String s : this) {
      result.add(mapper.map(s));
    }
    return result;
  }

  CategoryColumn collectIntoCategoryColumn(String newColumnName, StringStringMapper mapper) {
    CategoryColumn result = CategoryColumn.create(newColumnName);
    for (String s : this) {
      result.add(mapper.map(s));
    }
    return result;
  }

  IntColumn collectIntoIntColumn(String newColumnName, StringIntMapper mapper) {
    IntColumn result = IntColumn.create(newColumnName);
    for (String s : this) {
      result.add(mapper.map(s));
    }
    return result;
  }

  public TextColumn selectIf(StringPredicate predicate) {
    TextColumn column = emptyCopy();
    for (String next : this) {
      if (predicate.test(next)) {
        column.add(next);
      }
    }
    return column;
  }

  @Override
  public Iterator<String> iterator() {
    return data.iterator();
  }
}

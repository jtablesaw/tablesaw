package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.aggregator.StringReduceUtils;
import com.deathrayresearch.outlier.filter.text.StringFilters;
import com.deathrayresearch.outlier.mapper.StringMapUtils;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A column in a base table that contains float values
 */
public class TextColumn extends AbstractColumn
        implements StringMapUtils, StringFilters, StringReduceUtils {

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private String[] data;

  public static TextColumn create(String name) {
    return new TextColumn(name);
  }

  public static TextColumn create(String name, int size) {
    return new TextColumn(name, size);
  }

  public TextColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new String[metadata.getSize()];
  }

  private TextColumn(String name) {
    super(name);
    data = new String[DEFAULT_ARRAY_SIZE];
  }

  public TextColumn(String name, int initialSize) {
    super(name);
    data = new String[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public ColumnType type() {
    return ColumnType.TEXT;
  }

  @Override
  public boolean hasNext() {
    return pointer < N;
  }

  public String next() {
    return data[pointer++];
  }

  public void add(String f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = f;
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    String[] temp = new String[Math.round(data.length * 2)];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    String[] temp = new String[N + 100];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }

  @Override
  public String getString(int row) {
    return String.valueOf(data[row]);
  }

  @Override
  public TextColumn emptyCopy() {
    return new TextColumn(name());
  }

  @Override
  public void clear() {
    data = new String[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private TextColumn copy() {
    TextColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    TextColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    TextColumn copy = this.copy();
    // TODO(lwhite): BUG This sort is reversed (Q: Can we use this sort and reverse the iterator?)
    Arrays.sort(copy.data);
    return copy;
  }

  // TODO(lwhite): Implement column summary()
  @Override
  public Table summary() {
    return null;
  }

  @Override
  public int countUnique() {
    Set<String> stringSet = new HashSet<>();
    Collections.addAll(stringSet, data);
    return stringSet.size();
  }

  public TextColumn unique() {
    TextColumn textColumn = TextColumn.create(name() + " Unique values");
    Set<String> stringSet = new HashSet<>();
    Collections.addAll(stringSet, data);
    for (String string : stringSet) {
      textColumn.add(string);
    }
    return textColumn;
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }

  public void addCell(String s) {
    this.add(s);
  }

  public String get(int index) {
    return data[index];
  }

  public RoaringBitmap isEqualTo(String string) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (string.equals(next())) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  IntComparator comparator = new IntComparator() {
    @Override
    public int compare(int i, int i1) {
      String f1 = data[i];
      String f2 = data[i1];
      return f1.compareTo(f2);
    }

    @Override
    public int compare(Integer r1, Integer r2) {
      String f1 = data[r1];
      String f2 = data[r2];
      return f1.compareTo(f2);
    }
  };

  public void set(int i, String s) {
    if (i > data.length) {
      resize();
    }
    data[i] = s;
  }

  public int[] indexes() {
    int[] rowIndexes = new int[size()];
    for (int i = 0; i < size(); i++) {
      rowIndexes[i] = i;
    }
    return rowIndexes;
  }

  public String[] elements() {
    return data;
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
    while (hasNext()) {
      builder.append(String.valueOf(next()));
    }
    return builder.toString();
  }
}

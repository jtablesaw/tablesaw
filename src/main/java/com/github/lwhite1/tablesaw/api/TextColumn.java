package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.AbstractColumn;
import com.github.lwhite1.tablesaw.columns.CategoryColumnUtils;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.filtering.StringPredicate;
import com.github.lwhite1.tablesaw.filtering.text.CategoryFilters;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.util.BitmapBackedSelection;
import com.github.lwhite1.tablesaw.util.DictionaryMap;
import com.github.lwhite1.tablesaw.util.Selection;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

public class TextColumn extends AbstractColumn implements CategoryFilters, CategoryColumnUtils, Iterable<String> {
  private static final String MISSING_VALUE = String.valueOf(ColumnType.TEXT.getMissingValue());

  private IntArrayList values;

  private Map<String, Integer> uniqueValues;
  private Map<Integer, String> reverseIndex;

  private int id = 0; // uniqueValueCount

  public TextColumn(String name) {
    super(name);
    values = new IntArrayList(128);
    createMaps();
  }

  private void createMaps() {
    String dir = "/Users/apple/depot/tablesaw2/mapdb/" + this.id();
    DB db = DBMaker.fileDB(dir)
        .closeOnJvmShutdown()
        .fileMmapEnableIfSupported()
        .make();
    uniqueValues = db.hashMap("uniqueValues")
        .keySerializer(Serializer.STRING)
        .valueSerializer(Serializer.INTEGER_PACKED)
        .counterEnable()
        .createOrOpen();
    reverseIndex = db.hashMap("reverseIndex")
        .keySerializer(Serializer.INTEGER_PACKED)
        .valueSerializer(Serializer.STRING)
        .counterEnable()
        .createOrOpen();
  }

  public TextColumn(ColumnMetadata metadata) {
    super(metadata);
    values = new IntArrayList(128);
    createMaps();
  }

  public TextColumn(String name, int columnSize) {
    super(name);
    this.values = new IntArrayList(columnSize);
    createMaps();
  }

  @Override public int size() { return values.size(); }

  @Override
  public Table summary() {
    // TODO
    return null;
  }

  @Override
  public int countMissing() {
    if (!uniqueValues.containsKey(MISSING_VALUE)) { return 0;}

    int valueKey = uniqueValues.get(MISSING_VALUE);
    int count = 0;
    for (int value : values) if (valueKey == value) count++;

    return count;
  }

  @Override public int countUnique() { return reverseIndex.size(); }

  @Override
  public Column unique() {
    TextColumn newColumn = new TextColumn(name() + " Unique Values", values.size());
    uniqueValues.keySet().forEach(newColumn::add);
    return newColumn;
  }

  @Override public ColumnType type() { return ColumnType.TEXT; }

  @Override
  public String getString(int row) {
    int valueKey = values.getInt(row);
    if (!reverseIndex.containsKey(valueKey))
      throw new IllegalStateException("String dictionary does not contain key " + valueKey);

    return reverseIndex.get(valueKey);
  }

  @Override public Column emptyCopy() { return new TextColumn(name()); }

  @Override
  public Column copy() {
    TextColumn textColumn = new TextColumn(name(), values.size());
    textColumn.values.addAll(values);
    uniqueValues.forEach((k, v) -> textColumn.uniqueValues.put(k, v));
    reverseIndex.forEach((k, v) -> textColumn.reverseIndex.put(k, v));

    return textColumn;
  }

  @Override public Column emptyCopy(int rowSize) { return new TextColumn(name(), rowSize); }

  @Override
  public void clear() {
    values.clear();
    uniqueValues.clear();
    reverseIndex.clear();
  }

  @Override
  public void sortAscending() {
    IntArrays.parallelQuickSort(values.elements(), new IntComparator() {
      @Override
      public int compare(int k1, int k2) {
        Preconditions.checkArgument(reverseIndex.containsKey(k1), "No value corresponding to index: " + k1);
        Preconditions.checkArgument(reverseIndex.containsKey(k2), "No value corresponding to index: " + k2);
        String a = reverseIndex.get(k1);
        String b = reverseIndex.get(k2);
        return a.compareTo(b);
      }

      @Override
      public int compare(Integer o1, Integer o2) {
        return compare(o1.intValue(), o2.intValue());
      }
    });
  }

  @Override
  public void sortDescending() {
    IntArrays.parallelQuickSort(values.elements(), new IntComparator() {
      @Override
      public int compare(int k1, int k2) {
        Preconditions.checkArgument(reverseIndex.containsKey(k1), "No value corresponding to index: " + k1);
        Preconditions.checkArgument(reverseIndex.containsKey(k2), "No value corresponding to index: " + k2);
        String a = reverseIndex.get(k1);
        String b = reverseIndex.get(k2);
        return b.compareTo(a);
      }

      @Override
      public int compare(Integer o1, Integer o2) {
        return compare(o1.intValue(), o2.intValue());
      }
    });
  }

  @Override public boolean isEmpty() { return values.isEmpty(); }

  @Override
  public void addCell(String stringValue) {
    String s = Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue) ?
        MISSING_VALUE : stringValue;
    add(s);
  }

  @Override
  public IntComparator rowComparator() {
    return new IntComparator() {
      @Override
      public int compare(int k1, int k2) {
        String a = reverseIndex.get(values.getInt(k1));
        String b = reverseIndex.get(values.getInt(k2));
        return a.compareTo(b);
      }

      @Override public int compare(Integer o1, Integer o2) { return compare(o1.intValue(), o2.intValue()); }
    };
  }

  @Override
  public void append(Column column) {
    Preconditions.checkArgument(column.type() == ColumnType.TEXT);
    TextColumn other = (TextColumn) column;
    for (String s : other) add(s);
  }

  @Override
  public String print() {
    // TODO
    return null;
  }

  @Override public Selection isMissing() { return select(s -> s.equals(MISSING_VALUE)); }
  @Override public Selection isNotMissing() { return select(s -> !s.equals(MISSING_VALUE)); }

  private Selection select(StringPredicate predicate) {
    Selection selection = new BitmapBackedSelection();
    for (int idx : values) {
      String s = reverseIndex.get(idx);
      if (predicate.test(s)) selection.add(idx);
    }

    return selection;
  }

  @Override public int byteSize() { return 4; }

  @Override
  public byte[] asBytes(int rowNumber) {
    int idx = values.getInt(rowNumber);
    return ByteBuffer.allocate(4).putInt(idx).array();
  }

  @Override
  public DictionaryMap dictionaryMap() {
    DictionaryMap dict = new DictionaryMap();
    reverseIndex.forEach(dict::put);

    return dict;
  }

  @Override public IntArrayList values() { return values; }

  @Override
  public Iterator<String> iterator() {
    return new Iterator<String>() {
      private final IntListIterator itr = values.iterator();
      @Override public boolean hasNext() { return itr.hasNext(); }
      @Override public String next() { return reverseIndex.get(itr.next()); }
    };
  }

  public void add(String value) {
    if (!uniqueValues.containsKey(value)) uniqueValues.put(value, id++);

    int valueId = uniqueValues.get(value);
    values.add(valueId);
  }

}

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

import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanOpenHashSet;
import it.unimi.dsi.fastutil.booleans.BooleanSet;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.booleans.*;
import tech.tablesaw.filtering.BooleanFilterSpec;
import tech.tablesaw.filtering.predicates.BytePredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A column that contains boolean values */
public class BooleanColumn extends AbstractColumn<BooleanColumn, Boolean>
    implements BooleanFilterSpec<Selection>,
        BooleanMapUtils,
        CategoricalColumn<Boolean>,
        BooleanFillers<BooleanColumn>,
        BooleanFilters {

  /** The data held by this column */
  // protected ByteArrayList data;
  BooleanData data;

  /** An IntComparator. The ints are row indexes */
  private final IntComparator comparator =
      (r1, r2) -> {
        byte f1 = getByte(r1);
        byte f2 = getByte(r2);
        return Byte.compare(f1, f2);
      };

  /** The print formatter for this column, if any */
  private BooleanFormatter formatter = new BooleanFormatter("true", "false", "");

  /**
   * Constructs a BooleanColumn with the given name and values.
   *
   * @param name The column name
   * @param values The values: 0 is false, 1 is true, Byte.MIN_VALUE is the missing value indicator
   */
  private BooleanColumn(String name, ByteArrayList values) {
    super(BooleanColumnType.instance(), name, BooleanColumnType.DEFAULT_PARSER);
    data = new BitSetBooleanData(values);
  }

  public BooleanColumn(String name, BooleanData data) {
    super(BooleanColumnType.BOOLEAN, name, BooleanColumnType.DEFAULT_PARSER);
    this.data = data;
  }

  /** Returns {@code true} if b is the missing value indicator for this column type */
  public static boolean valueIsMissing(byte b) {
    return BooleanColumnType.valueIsMissing(b);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissing(int rowNumber) {
    return valueIsMissing(getByte(rowNumber));
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn setMissing(int i) {
    set(i, BooleanColumnType.missingValueIndicator());
    return this;
  }

  /**
   * Returns a new Boolean column of the given size. Elements indexed by the selection are set to
   * true
   *
   * @param name The column name
   * @param hits The true values
   * @param columnSize The column size
   * @return A new BooleanColumn
   */
  public static BooleanColumn create(String name, Selection hits, int columnSize) {
    BooleanColumn column = create(name, columnSize);
    checkArgument(
        (hits.size() <= columnSize),
        "Cannot have more true values than total values in a boolean column");

    for (int hit : hits) {
      column.set(hit, true);
    }
    column.set(column.isMissing(), false);
    return column;
  }

  /**
   * Returns a new, empty Boolean column with the given name.
   *
   * @param name The column name
   * @return A new BooleanColumn
   */
  public static BooleanColumn create(String name) {
    return new BooleanColumn(name, new ByteArrayList(DEFAULT_ARRAY_SIZE));
  }

  /**
   * Returns a new Boolean column of the given size.
   *
   * @param name The column name
   * @param initialSize The column size
   * @return A new BooleanColumn
   */
  public static BooleanColumn create(String name, int initialSize) {
    BooleanColumn column = new BooleanColumn(name, new ByteArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  /** Returns a new Boolean column with the given name and values */
  public static BooleanColumn create(String name, boolean... values) {
    BooleanColumn column = create(name, values.length);
    int r = 0;
    for (boolean b : values) {
      column.set(r, b);
      r++;
    }
    return column;
  }

  /** Returns a new Boolean column with the given name and values */
  public static BooleanColumn create(String name, Collection<Boolean> values) {
    BooleanColumn column = create(name);
    for (Boolean b : values) {
      column.append(b);
    }
    return column;
  }

  /** Returns a new Boolean column with the given name and values */
  public static BooleanColumn create(String name, Boolean[] values) {
    BooleanColumn column = create(name);
    for (Boolean val : values) {
      column.append(val);
    }
    return column;
  }

  /** Returns a new Boolean column with the given name and values */
  public static BooleanColumn create(String name, Stream<Boolean> values) {
    BooleanColumn column = create(name);
    values.forEach(column::append);
    return column;
  }

  /** Sets the print formatter for this column */
  public void setPrintFormatter(BooleanFormatter formatter) {
    this.formatter = formatter;
  }

  /** Returns the print formatter for this column */
  public BooleanFormatter getPrintFormatter() {
    return formatter;
  }

  /** {@inheritDoc} */
  @Override
  public int size() {
    return data.size();
  }

  /** {@inheritDoc} */
  @Override
  public Table summary() {
    Byte2IntMap counts = new Byte2IntOpenHashMap(3);
    counts.put(BooleanColumnType.BYTE_FALSE, 0);
    counts.put(BooleanColumnType.BYTE_TRUE, 0);

    for (byte next : data) {
      counts.put(next, counts.get(next) + 1);
    }

    Table table = Table.create(name());

    StringColumn label = StringColumn.create("Value");
    DoubleColumn countColumn = DoubleColumn.create("Count");
    table.addColumns(label);
    table.addColumns(countColumn);

    for (Map.Entry<Byte, Integer> entry : counts.byte2IntEntrySet()) {
      label.append(entry.getKey() == 1 ? "true" : "false");
      countColumn.append(entry.getValue());
    }
    return table;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    return data.countMissing();
  }

  /** {@inheritDoc} */
  @Override
  public int countUnique() {
    return data.countUnique();
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn unique() {
    ByteSet count = new ByteOpenHashSet(3);
    for (byte next : data) {
      count.add(next);
    }
    ByteArrayList list = new ByteArrayList(count);
    return new BooleanColumn(name() + " Unique values", list);
  }

  /** Appends b to the end of this column and returns this column */
  public BooleanColumn append(boolean b) {
    if (b) {
      data.add(BooleanColumnType.BYTE_TRUE);
    } else {
      data.add(BooleanColumnType.BYTE_FALSE);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn append(Boolean b) {
    if (b == null) {
      appendMissing();
    } else if (b) {
      data.add(BooleanColumnType.BYTE_TRUE);
    } else {
      data.add(BooleanColumnType.BYTE_FALSE);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (!(obj instanceof Boolean)) {
      throw new IllegalArgumentException(
          "Cannot append " + obj.getClass().getName() + " to BooleanColumn");
    }
    return append((Boolean) obj);
  }

  /** Appends b to the end of this column and returns this column */
  public BooleanColumn append(byte b) {
    data.add(b);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn appendMissing() {
    append(BooleanColumnType.MISSING_VALUE);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public int valueHash(int rowNumber) {
    return getByte(rowNumber);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(int rowNumber1, int rowNumber2) {
    return getByte(rowNumber1) == getByte(rowNumber2);
  }

  /** {@inheritDoc} */
  @Override
  public String getString(int row) {
    return formatter.format(get(row));
  }

  /** {@inheritDoc} */
  @Override
  public String getUnformattedString(int row) {
    Boolean b = get(row);
    if (b == null) {
      return "";
    }
    return String.valueOf(b);
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn emptyCopy() {
    BooleanColumn empty = create(name());
    empty.setPrintFormatter(getPrintFormatter());
    return empty;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn emptyCopy(int rowSize) {
    return create(name(), rowSize);
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    data.clear();
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn copy() {
    return new BooleanColumn(name(), data.copy());
  }

  /** {@inheritDoc} */
  @Override
  public void sortAscending() {
    data.sortAscending();
  }

  /** {@inheritDoc} */
  @Override
  public void sortDescending() {
    data.sortDescending();
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn appendCell(String object) {
    return append(parser().parseByte(object));
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn appendCell(String object, AbstractColumnParser<?> parser) {
    return append(parser.parseByte(object));
  }

  /**
   * Returns the value in row i as a Boolean
   *
   * @param i the row number
   * @return A Boolean object (may be null)
   */
  @Override
  public Boolean get(int i) {
    byte b = data.getByte(i);
    if (b == BooleanColumnType.BYTE_TRUE) {
      return Boolean.TRUE;
    }
    if (b == BooleanColumnType.BYTE_FALSE) {
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

  /** {@inheritDoc} */
  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  /** Returns the number of {@code true} elements in this column */
  public int countTrue() {
    return data.countTrue();
  }

  /** Returns the number of {@code false} elements in this column */
  public int countFalse() {
    return data.countFalse();
  }

  /** Returns the proportion of non-missing row elements that contain true */
  public double proportionTrue() {
    return (double) countTrue() / (size() - countMissing());
  }

  /** Returns the proportion of non-missing row elements that contain false */
  public double proportionFalse() {
    return 1.0 - proportionTrue();
  }

  /** Returns true if the column contains any true values, and false otherwise */
  public boolean any() {
    return countTrue() > 0;
  }

  /**
   * Returns true if the column contains only true values, and false otherwise. If there are any
   * missing values it returns false.
   */
  public boolean all() {
    return countTrue() == size();
  }

  /** Returns true if the column contains no true values, and false otherwise */
  public boolean none() {
    return countTrue() == 0;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isFalse() {
    return data.isFalse();
  }

  /** {@inheritDoc} */
  @Override
  public Selection isTrue() {
    return data.isTrue();
  }

  /** {@inheritDoc} */
  @Override
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

  /** Returns a ByteArrayList containing 0 (false), 1 (true) or Byte.MIN_VALUE (missing) */
  public ByteArrayList toByteArrayList() {
    return data.toByteArrayList();
  }

  public BooleanData data() {
    return data;
  }

  /** Sets the value at i to b, and returns this column */
  public BooleanColumn set(int i, boolean b) {
    if (b) {
      data.set(i, BooleanColumnType.BYTE_TRUE);
    } else {
      data.set(i, BooleanColumnType.BYTE_FALSE);
    }
    return this;
  }

  /** Sets the value at i to b, and returns this column */
  public BooleanColumn set(int i, byte b) {
    data.set(i, b);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn set(int i, Boolean val) {
    return val == null ? setMissing(i) : set(i, val.booleanValue());
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn set(int row, String stringValue, AbstractColumnParser<?> parser) {
    return set(row, parser.parseByte(stringValue));
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn lead(int n) {
    BooleanColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn lag(int n) {
    int srcPos = n >= 0 ? 0 : -n;
    byte[] dest = new byte[size()];
    int destPos = Math.max(n, 0);
    int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = BooleanColumnType.MISSING_VALUE;
    }

    System.arraycopy(data.toByteArray(), srcPos, dest, destPos, length);

    BooleanColumn copy = emptyCopy(size());
    copy.data = new BitSetBooleanData(new ByteArrayList(dest));
    copy.setName(name() + " lag(" + n + ")");
    return copy;
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   */
  public BooleanColumn set(Selection rowSelection, boolean newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria.
   *
   * @param rowSelection the rows to be updated
   * @param newValue a byte representation of boolean values. The only valid arguments are 0, 1, and
   *     {@link BooleanColumnType#missingValueIndicator()}
   */
  public BooleanColumn set(Selection rowSelection, byte newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  /**
   * Returns the value at row as a double, with true values encoded as 1.0 and false values as 0.0
   */
  public double getDouble(int row) {
    return getByte(row);
  }

  /**
   * Returns all the values in this column as an array of doubles, with true values encoded as 1.0
   * and false values as 0.0
   */
  public double[] asDoubleArray() {
    double[] doubles = new double[data.size()];
    for (int i = 0; i < size(); i++) {
      doubles[i] = data.getByte(i);
    }
    return doubles;
  }

  /** {@inheritDoc} */
  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn append(Column<Boolean> column) {
    checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    BooleanColumn col = (BooleanColumn) column;
    final int size = col.size();
    for (int i = 0; i < size; i++) {
      append(col.getByte(i));
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Column<Boolean> append(Column<Boolean> column, int row) {
    checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    BooleanColumn col = (BooleanColumn) column;
    append(col.getByte(row));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Column<Boolean> set(int row, Column<Boolean> column, int sourceRow) {
    checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    BooleanColumn col = (BooleanColumn) column;
    set(row, col.getByte(sourceRow));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Selection asSelection() {
    return data.asSelection();
  }

  /** {@inheritDoc} */
  @Override
  public Selection isMissing() {
    return eval(BooleanColumnUtils.isMissing);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isNotMissing() {
    return eval(BooleanColumnUtils.isNotMissing);
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<Boolean> iterator() {
    return new BooleanColumnIterator(this.byteIterator());
  }

  /** Returns a ByteIterator for this column */
  public ByteIterator byteIterator() {
    return data.iterator();
  }

  /** Returns the values in this column as a BooleanSet instance */
  public BooleanSet asSet() {
    BooleanSet set = new BooleanOpenHashSet(3);
    BooleanColumn unique = unique();
    for (int i = 0; i < unique.size(); i++) {
      set.add((boolean) unique.get(i));
    }
    return set;
  }

  /** Returns true if the column contains at least one value like {@code aBoolean} */
  public boolean contains(boolean aBoolean) {
    if (aBoolean) {
      return data().contains(BooleanColumnType.BYTE_TRUE);
    }
    return data().contains(BooleanColumnType.BYTE_FALSE);
  }

  /** {@inheritDoc} */
  @Override
  public int byteSize() {
    return type().byteSize();
  }

  /** {@inheritDoc} */
  @Override
  public byte[] asBytes(int row) {
    byte[] result = new byte[byteSize()];
    result[0] = (get(row) ? BooleanColumnType.BYTE_TRUE : BooleanColumnType.BYTE_FALSE);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn removeMissing() {
    BooleanColumn noMissing = emptyCopy();
    ByteIterator iterator = byteIterator();
    while (iterator.hasNext()) {
      byte b = iterator.nextByte();
      if (!valueIsMissing(b)) {
        noMissing.append(b);
      }
    }
    return noMissing;
  }

  /** Returns a Selection of the elements that return true when the predicate is evaluated */
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

  /** Returns a Selection of the elements that return true when the predicate is evaluated */
  public Selection eval(Predicate<Boolean> predicate) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < data.size(); idx++) {
      if (predicate.test(get(idx))) {
        selection.add(idx);
      }
    }
    return selection;
  }

  /**
   * Returns a Selection of the elements that return true when the predicate is evaluated with the
   * given Boolean argument
   */
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
   * Returns a DoubleColumn containing the elements in this column, with true as 1.0 and false as
   * 0.0.
   */
  public DoubleColumn asDoubleColumn() {
    DoubleColumn numberColumn = DoubleColumn.create(this.name(), size());
    for (int i = 0; i < size(); i++) {
      numberColumn.set(i, data.getByte(i));
    }
    return numberColumn;
  }

  /** {@inheritDoc} */
  @Override
  public int compare(Boolean o1, Boolean o2) {
    return Boolean.compare(o1, o2);
  }

  private static class BooleanColumnIterator implements Iterator<Boolean> {

    private final ByteIterator iterator;

    BooleanColumnIterator(ByteIterator iterator) {
      this.iterator = iterator;
    }

    /**
     * Returns {@code true} if the iteration has more elements. (In other words, returns {@code
     * true} if {@link #next()} would return an element rather than throwing an exception.)
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

  // fillWith methods

  /** {@inheritDoc} */
  @Override
  public BooleanColumn fillWith(BooleanIterator iterator) {
    for (int r = 0; r < size(); r++) {
      if (!iterator.hasNext()) {
        break;
      }
      set(r, iterator.nextBoolean());
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public BooleanColumn fillWith(BooleanIterable iterable) {
    BooleanIterator iterator = iterable.iterator();
    for (int r = 0; r < size(); r++) {
      if (!iterator.hasNext()) {
        iterator = iterable.iterator();
        if (!iterator.hasNext()) {
          break;
        }
      }
      set(r, iterator.nextBoolean());
    }
    return this;
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public Boolean[] asObjectArray() {
    final Boolean[] output = new Boolean[data.size()];
    for (int i = 0; i < data.size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  /**
   * Returns a byte representation of the true values, encoded in the format specified in {@link
   * java.util.BitSet#toByteArray()}
   */
  public byte[] trueBytes() {
    return data.trueBytes();
  }

  /**
   * Returns a byte representation of the false values, encoded in the format specified in {@link
   * java.util.BitSet#toByteArray()}
   */
  public byte[] falseBytes() {
    return data.falseBytes();
  }

  /**
   * Returns a byte representation of the missing values, encoded in the format specified in {@link
   * java.util.BitSet#toByteArray()}
   */
  public byte[] missingBytes() {
    return data.missingBytes();
  }

  /**
   * Sets the true values in the data from a byte[] encoding
   *
   * @param encodedValues The true values encoded in the format specified in {@link
   *     java.util.BitSet}
   */
  public void trueBytes(byte[] encodedValues) {
    data.setTrueBytes(encodedValues);
  }

  /**
   * Sets the false values in the data from a byte[] encoding
   *
   * @param encodedValues The false values encoded in the format specified in {@link
   *     java.util.BitSet}
   */
  public void falseBytes(byte[] encodedValues) {
    data.setFalseBytes(encodedValues);
  }

  /**
   * Sets the missing values in the data from a byte[] encoding
   *
   * @param encodedValues The missing values encoded in the format specified in {@link
   *     java.util.BitSet}
   */
  public void missingBytes(byte[] encodedValues) {
    data.setMissingBytes(encodedValues);
  }
}

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
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.DateColumnFormatter;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.dates.DateFillers;
import tech.tablesaw.columns.dates.DateFilters;
import tech.tablesaw.columns.dates.DateMapFunctions;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.selection.Selection;

/** A column in a base table that contains float values */
public class DateColumn extends AbstractColumn<DateColumn, LocalDate>
    implements DateFilters,
        DateFillers<DateColumn>,
        DateMapFunctions,
        CategoricalColumn<LocalDate> {

  private IntArrayList data;

  private final IntComparator comparator =
      (r1, r2) -> {
        final int f1 = getIntInternal(r1);
        int f2 = getIntInternal(r2);
        return Integer.compare(f1, f2);
      };

  private DateColumnFormatter printFormatter = new DateColumnFormatter();

  public static DateColumn create(final String name) {
    return new DateColumn(name, new IntArrayList(DEFAULT_ARRAY_SIZE));
  }

  public static DateColumn create(final String name, final int initialSize) {
    DateColumn column = new DateColumn(name, new IntArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static DateColumn create(String name, Collection<LocalDate> data) {
    DateColumn column = new DateColumn(name, new IntArrayList(data.size()));
    for (LocalDate date : data) {
      column.append(date);
    }
    return column;
  }

  public static DateColumn create(String name, LocalDate... data) {
    DateColumn column = new DateColumn(name, new IntArrayList(data.length));
    for (LocalDate date : data) {
      column.append(date);
    }
    return column;
  }

  public static DateColumn create(String name, Stream<LocalDate> stream) {
    DateColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  private DateColumn(String name, IntArrayList data) {
    super(DateColumnType.instance(), name);
    this.data = data;
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public DateColumn subset(final int[] rows) {
    final DateColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.appendInternal(getIntInternal(row));
    }
    return c;
  }

  public DateColumn appendInternal(int f) {
    data.add(f);
    return this;
  }

  public DateColumn set(int index, int value) {
    data.set(index, value);
    return this;
  }

  @Override
  public DateColumn set(int index, LocalDate value) {
    data.set(index, PackedLocalDate.pack(value));
    return this;
  }

  public void setPrintFormatter(DateTimeFormatter dateTimeFormatter, String missingValueString) {
    Preconditions.checkNotNull(dateTimeFormatter);
    Preconditions.checkNotNull(missingValueString);
    this.printFormatter = new DateColumnFormatter(dateTimeFormatter, missingValueString);
  }

  public void setPrintFormatter(DateTimeFormatter dateTimeFormatter) {
    Preconditions.checkNotNull(dateTimeFormatter);
    this.printFormatter = new DateColumnFormatter(dateTimeFormatter);
  }

  @Override
  public String getString(int row) {
    return printFormatter.format(getPackedDate(row));
  }

  @Override
  public String getUnformattedString(int row) {
    return PackedLocalDate.toDateString(getPackedDate(row));
  }

  @Override
  public DateColumn emptyCopy() {
    DateColumn empty = create(name());
    empty.printFormatter = printFormatter;
    return empty;
  }

  @Override
  public DateColumn emptyCopy(int rowSize) {
    DateColumn copy = create(name(), rowSize);
    copy.printFormatter = printFormatter;
    return copy;
  }

  @Override
  public DateColumn copy() {
    DateColumn copy = emptyCopy(data.size());
    copy.data = data.clone();
    return copy;
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public DateColumn lead(int n) {
    DateColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  @Override
  public DateColumn lag(int n) {
    int srcPos = n >= 0 ? 0 : 0 - n;
    int[] dest = new int[size()];
    int destPos = n <= 0 ? 0 : n;
    int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = DateColumnType.missingValueIndicator();
    }

    System.arraycopy(data.toIntArray(), srcPos, dest, destPos, length);

    DateColumn copy = emptyCopy(size());
    copy.data = new IntArrayList(dest);
    copy.setName(name() + " lag(" + n + ")");
    return copy;
  }

  @Override
  public void sortAscending() {
    data.sort(IntComparators.NATURAL_COMPARATOR);
  }

  @Override
  public void sortDescending() {
    data.sort(IntComparators.OPPOSITE_COMPARATOR);
  }

  @Override
  public int countUnique() {
    IntSet ints = new IntOpenHashSet(size());
    for (int i = 0; i < size(); i++) {
      ints.add(data.getInt(i));
    }
    return ints.size();
  }

  @Override
  public DateColumn unique() {
    IntSet ints = new IntOpenHashSet(data.size());
    for (int i = 0; i < size(); i++) {
      ints.add(data.getInt(i));
    }
    DateColumn copy = emptyCopy(ints.size());
    copy.setName(name() + " Unique values");
    copy.data = IntArrayList.wrap(ints.toIntArray());
    return copy;
  }

  @Override
  public DateColumn append(final Column<LocalDate> column) {
    Preconditions.checkArgument(column.type() == this.type());
    DateColumn dateColumn = (DateColumn) column;
    final int size = dateColumn.size();
    for (int i = 0; i < size; i++) {
      appendInternal(dateColumn.getPackedDate(i));
    }
    return this;
  }

  @Override
  public DateColumn append(Column<LocalDate> column, int row) {
    Preconditions.checkArgument(column.type() == this.type());
    return appendInternal(((DateColumn) column).getIntInternal(row));
  }

  @Override
  public DateColumn set(int row, Column<LocalDate> column, int sourceRow) {
    Preconditions.checkArgument(column.type() == this.type());
    return set(row, ((DateColumn) column).getIntInternal(sourceRow));
  }

  @Override
  public LocalDate max() {
    if (isEmpty()) {
      return null;
    }

    Integer max = null;
    for (int aData : data) {
      if (DateColumnType.missingValueIndicator() != aData) {
        if (max == null) {
          max = aData;
        } else {
          max = (max > aData) ? max : aData;
        }
      }
    }

    if (max == null) {
      return null;
    }
    return PackedLocalDate.asLocalDate(max);
  }

  @Override
  public LocalDate min() {
    if (isEmpty()) {
      return null;
    }

    Integer min = null;
    for (int aData : data) {
      if (DateColumnType.missingValueIndicator() != aData) {
        if (min == null) {
          min = aData;
        } else {
          min = (min < aData) ? min : aData;
        }
      }
    }
    if (min == null) {
      return null;
    }
    return PackedLocalDate.asLocalDate(min);
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   *
   * <p>Example: myColumn.set(myColumn.valueIsMissing(), LocalDate.now()); // no more missing values
   */
  @Override
  public DateColumn set(Selection rowSelection, LocalDate newValue) {
    int packed = PackedLocalDate.pack(newValue);
    for (int row : rowSelection) {
      set(row, packed);
    }
    return this;
  }

  @Override
  public DateColumn appendMissing() {
    appendInternal(DateColumnType.missingValueIndicator());
    return this;
  }

  @Override
  public LocalDate get(int index) {
    return PackedLocalDate.asLocalDate(getPackedDate(index));
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  @Override
  public DateColumn append(LocalDate value) {
    return this.appendInternal(PackedLocalDate.pack(value));
  }

  @Override
  public DateColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof java.sql.Date) {
      return append(((java.sql.Date) obj).toLocalDate());
    }
    if (obj instanceof LocalDate) {
      return append((LocalDate) obj);
    }
    throw new IllegalArgumentException(
        "Cannot append " + obj.getClass().getName() + " to DateColumn");
  }

  @Override
  public DateColumn appendCell(String string) {
    return appendInternal(PackedLocalDate.pack(DateColumnType.DEFAULT_PARSER.parse(string)));
  }

  @Override
  public DateColumn appendCell(String string, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(string));
  }

  @Override
  public int getIntInternal(int index) {
    return data.getInt(index);
  }

  protected int getPackedDate(int index) {
    return getIntInternal(index);
  }

  /**
   * Returns a table of dates and the number of observations of those dates
   *
   * @return the summary table
   */
  @Override
  public Table summary() {

    Table table = Table.create("Column: " + name());
    StringColumn measure = StringColumn.create("Measure");
    StringColumn value = StringColumn.create("Value");
    table.addColumns(measure);
    table.addColumns(value);

    measure.append("Count");
    value.append(String.valueOf(size()));

    measure.append("Missing");
    value.append(String.valueOf(countMissing()));

    measure.append("Earliest");
    value.append(String.valueOf(min()));

    measure.append("Latest");
    value.append(String.valueOf(max()));

    return table;
  }

  public static boolean valueIsMissing(int i) {
    return DateColumnType.valueIsMissing(i);
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (getPackedDate(i) == DateColumnType.missingValueIndicator()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns the largest ("top") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the largest observations
   */
  public List<LocalDate> top(int n) {
    List<LocalDate> top = new ArrayList<>();
    int[] values = data.toIntArray();
    IntArrays.parallelQuickSort(values, IntComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(PackedLocalDate.asLocalDate(values[i]));
    }
    return top;
  }

  /**
   * Returns the smallest ("bottom") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the smallest n observations
   */
  public List<LocalDate> bottom(int n) {
    List<LocalDate> bottom = new ArrayList<>();
    int[] values = data.toIntArray();
    IntArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(PackedLocalDate.asLocalDate(values[i]));
    }
    return bottom;
  }

  public IntIterator intIterator() {
    return data.iterator();
  }

  @Override
  public DateColumn removeMissing() {
    DateColumn noMissing = emptyCopy();
    IntIterator iterator = intIterator();
    while (iterator.hasNext()) {
      int i = iterator.nextInt();
      if (!valueIsMissing(i)) {
        noMissing.appendInternal(i);
      }
    }
    return noMissing;
  }

  @Override
  public List<LocalDate> asList() {
    List<LocalDate> dates = new ArrayList<>(size());
    for (LocalDate localDate : this) {
      dates.add(localDate);
    }
    return dates;
  }

  @Override
  public DateColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  public Set<LocalDate> asSet() {
    Set<LocalDate> dates = new HashSet<>();
    DateColumn unique = unique();
    for (LocalDate d : unique) {
      dates.add(d);
    }
    return dates;
  }

  @Override
  public boolean contains(LocalDate localDate) {
    int date = PackedLocalDate.pack(localDate);
    return data.contains(date);
  }

  @Override
  public Column<LocalDate> setMissing(int i) {
    return set(i, DateColumnType.missingValueIndicator());
  }

  public double[] asDoubleArray() {
    double[] doubles = new double[size()];
    for (int i = 0; i < size(); i++) {
      doubles[i] = data.getInt(i);
    }
    return doubles;
  }

  public DoubleColumn asDoubleColumn() {
    return DoubleColumn.create(name(), asDoubleArray());
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return valueIsMissing(getIntInternal(rowNumber));
  }

  public double getDouble(int i) {
    return getIntInternal(i);
  }

  @Override
  public int byteSize() {
    return type().byteSize();
  }

  /**
   * Returns the contents of the cell at rowNumber as a byte[]
   *
   * @param rowNumber the number of the row as int
   */
  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(byteSize()).putInt(getPackedDate(rowNumber)).array();
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator<LocalDate> iterator() {

    return new Iterator<LocalDate>() {

      final IntIterator intIterator = intIterator();

      @Override
      public boolean hasNext() {
        return intIterator.hasNext();
      }

      @Override
      public LocalDate next() {
        return PackedLocalDate.asLocalDate(intIterator.nextInt());
      }
    };
  }

  // fillWith methods

  private DateColumn fillWith(
      int count, Iterator<LocalDate> iterator, Consumer<LocalDate> acceptor) {
    for (int r = 0; r < count; r++) {
      if (!iterator.hasNext()) {
        break;
      }
      acceptor.accept(iterator.next());
    }
    return this;
  }

  @Override
  public DateColumn fillWith(Iterator<LocalDate> iterator) {
    int[] r = new int[1];
    fillWith(size(), iterator, date -> set(r[0]++, date));
    return this;
  }

  private DateColumn fillWith(
      int count, Iterable<LocalDate> iterable, Consumer<LocalDate> acceptor) {
    Iterator<LocalDate> iterator = iterable.iterator();
    for (int r = 0; r < count; r++) {
      if (!iterator.hasNext()) {
        iterator = iterable.iterator();
        if (!iterator.hasNext()) {
          break;
        }
      }
      acceptor.accept(iterator.next());
    }
    return this;
  }

  @Override
  public DateColumn fillWith(Iterable<LocalDate> iterable) {
    int[] r = new int[1];
    fillWith(size(), iterable, date -> set(r[0]++, date));
    return this;
  }

  private DateColumn fillWith(
      int count, Supplier<LocalDate> supplier, Consumer<LocalDate> acceptor) {
    for (int r = 0; r < count; r++) {
      try {
        acceptor.accept(supplier.get());
      } catch (Exception e) {
        break;
      }
    }
    return this;
  }

  @Override
  public DateColumn fillWith(Supplier<LocalDate> supplier) {
    int[] r = new int[1];
    fillWith(size(), supplier, date -> set(r[0]++, date));
    return this;
  }

  @Override
  public LocalDate[] asObjectArray() {
    final LocalDate[] output = new LocalDate[data.size()];
    for (int i = 0; i < data.size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  @Override
  public int compare(LocalDate o1, LocalDate o2) {
    return o1.compareTo(o2);
  }
}

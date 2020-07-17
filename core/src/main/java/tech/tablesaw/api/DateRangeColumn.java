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
import tech.tablesaw.columns.dateranges.DateRange;
import tech.tablesaw.columns.dateranges.DateRangeColumnFormatter;
import tech.tablesaw.columns.dateranges.DateRangeColumnType;
import tech.tablesaw.columns.dateranges.DateRangeFillers;
import tech.tablesaw.columns.dateranges.DateRangeFunctions;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.selection.Selection;

/**
 * A column that contains date ranges, where a date range represents the interval between two dates
 *
 * <p>The column supports operations similar to those in Guava's Range class, as well as some
 * aspects of the Joda Time Interval class, and the Java Time Period class
 */
public class DateRangeColumn extends AbstractColumn<DateRangeColumn, DateRange>
    implements DateRangeFillers<DateRangeColumn>,
        // DateFilters,
        DateRangeFunctions
// CategoricalColumn<LocalDate>  TODO
{

  private IntArrayList from;
  private IntArrayList to;

  // OK
  private final IntComparator comparator =
      (r1, r2) -> {
        int f1 = from.getInt(r1);
        int f2 = from.getInt(r2);
        int t1 = to.getInt(r1);
        int t2 = to.getInt(r2);
        if (f1 < f2) {
          return -1;
        }
        if (f2 < f1) {
          return 1;
        }
        return Integer.compare(t1, t2);
      };

  // OK
  private DateRangeColumnFormatter printFormatter = new DateRangeColumnFormatter();

  // OK
  public static DateRangeColumn create(final String name) {
    return new DateRangeColumn(
        name, new IntArrayList(DEFAULT_ARRAY_SIZE), new IntArrayList(DEFAULT_ARRAY_SIZE));
  }

  // OK
  public static DateRangeColumn create(final String name, final int initialSize) {
    DateRangeColumn column =
        new DateRangeColumn(name, new IntArrayList(initialSize), new IntArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  // OK
  public static DateRangeColumn create(
      String name, Collection<LocalDate> from, Collection<LocalDate> to) {
    Preconditions.checkArgument(
        from.size() == to.size(), "The from and to collections must be the same size.");
    DateRangeColumn column =
        new DateRangeColumn(name, new IntArrayList(from.size()), new IntArrayList(to.size()));
    Iterator<LocalDate> frIterator = from.iterator();
    Iterator<LocalDate> toIterator = to.iterator();
    while (frIterator.hasNext()) {
      LocalDate fromDate = frIterator.next();
      LocalDate toDate = toIterator.next();
      column.append(fromDate, toDate);
    }
    return column;
  }

  // OK
  public static DateRangeColumn create(String name, Column<LocalDate> from, Column<LocalDate> to) {
    Preconditions.checkArgument(
        from.size() == to.size(), "The from and to collections must be the same size.");
    DateRangeColumn column =
        new DateRangeColumn(name, new IntArrayList(from.size()), new IntArrayList(to.size()));
    Iterator<LocalDate> frIterator = from.iterator();
    Iterator<LocalDate> toIterator = to.iterator();
    while (frIterator.hasNext()) {
      LocalDate fromDate = frIterator.next();
      LocalDate toDate = toIterator.next();
      column.append(fromDate, toDate);
    }
    return column;
  }

  // OK
  public static DateRangeColumn create(String name, Stream<LocalDate> from, Stream<LocalDate> to) {
    DateRangeColumn column = create(name);
    Iterator<LocalDate> frIterator = from.iterator();
    Iterator<LocalDate> toIterator = to.iterator();
    while (frIterator.hasNext()) {
      LocalDate fromDate = frIterator.next();
      LocalDate toDate = toIterator.next();
      column.append(fromDate, toDate);
    }
    return column;
  }

  // OK
  private DateRangeColumn(String name, IntArrayList from, IntArrayList to) {
    super(DateRangeColumnType.instance(), name);
    this.from = from;
    this.to = to;
  }

  @Override // OK
  public int size() {
    return from.size();
  }

  @Override // OK
  public DateRangeColumn subset(final int[] rows) {
    final DateRangeColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.appendInternal(getFromInternal(row), getToInternal(row));
    }
    return c;
  }

  // OK
  public DateRangeColumn appendInternal(int f, int t) {
    from.add(f);
    to.add(t);
    return this;
  }

  // OK
  public DateRangeColumn set(int index, int from, int to) {
    this.from.set(index, from);
    this.to.set(index, to);
    return this;
  }

  // OK
  private DateRangeColumn setFrom(int index, int from) {
    this.from.set(index, from);
    return this;
  }

  // OK
  private DateRangeColumn setTo(int index, int to) {
    this.to.set(index, to);
    return this;
  }

  // OK
  public DateRangeColumn set(int index, LocalDate from, LocalDate to) {
    if (from == null) setMissing(index);
    else setFrom(index, PackedLocalDate.pack(from));

    if (to == null) setMissing(index);
    else setTo(index, PackedLocalDate.pack(to));

    return this;
  }

  // OK
  public void setPrintFormatter(
      DateTimeFormatter dateTimeFormatter, String separator, String missingValueString) {
    Preconditions.checkNotNull(dateTimeFormatter);
    Preconditions.checkNotNull(missingValueString);
    this.printFormatter =
        new DateRangeColumnFormatter(dateTimeFormatter, separator, missingValueString);
  }

  // OK
  public void setPrintFormatter(DateTimeFormatter dateTimeFormatter) {
    Preconditions.checkNotNull(dateTimeFormatter);
    this.printFormatter = new DateRangeColumnFormatter(dateTimeFormatter);
  }

  @Override // OK
  public String getString(int row) {
    return printFormatter.format(getFromInternal(row), getToInternal(row));
  }

  @Override // OK
  public String getUnformattedString(int row) {
    return new DateRange(from.getInt(row), to.getInt(row)).toString();
  }

  @Override // OK
  public DateRangeColumn emptyCopy() {
    DateRangeColumn empty = create(name());
    empty.printFormatter = printFormatter;
    return empty;
  }

  @Override // OK
  public DateRangeColumn emptyCopy(int rowSize) {
    DateRangeColumn copy = create(name(), rowSize);
    copy.printFormatter = printFormatter;
    return copy;
  }

  @Override // OK
  public DateRangeColumn copy() {
    DateRangeColumn copy = emptyCopy(from.size());
    copy.from = from.clone();
    copy.to = to.clone();
    return copy;
  }

  @Override // OK
  public void clear() {
    from.clear();
    to.clear();
  }

  @Override // OK
  public DateRangeColumn lead(int n) {
    DateRangeColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  @Override // OK
  public DateRangeColumn lag(int n) {
    int srcPos = n >= 0 ? 0 : 0 - n;
    int[] fromDest = new int[size()];
    int[] toDest = new int[size()];
    int destPos = n <= 0 ? 0 : n;
    int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      fromDest[i] = DateRangeColumnType.missingValueIndicator();
      toDest[i] = DateRangeColumnType.missingValueIndicator();
    }

    System.arraycopy(from.toIntArray(), srcPos, fromDest, destPos, length);
    System.arraycopy(to.toIntArray(), srcPos, toDest, destPos, length);

    DateRangeColumn copy = emptyCopy(size());
    copy.from = new IntArrayList(fromDest);
    copy.to = new IntArrayList(toDest);
    copy.setName(name() + " lag(" + n + ")");
    return copy;
  }

  @Override // TODO
  public void sortAscending() {
    from.sort(IntComparators.NATURAL_COMPARATOR);
  }

  @Override // TODO
  public void sortDescending() {
    from.sort(IntComparators.OPPOSITE_COMPARATOR);
  }

  @Override // OK
  public int countUnique() {
    Set<DateRange> ranges = new HashSet<>(size());
    for (int i = 0; i < size(); i++) {
      ranges.add(get(i));
    }
    return ranges.size();
  }

  @Override // OK
  public DateRangeColumn unique() {
    Set<DateRange> ranges = new HashSet<>(size());
    for (int i = 0; i < size(); i++) {
      ranges.add(get(i));
    }
    DateRangeColumn copy = emptyCopy(ranges.size());
    copy.setName(name() + " Unique values");
    ranges.forEach(copy::append);
    return copy;
  }

  @Override // OK
  public DateRangeColumn append(final Column<DateRange> column) {
    Preconditions.checkArgument(column.type() == this.type());
    DateRangeColumn drc = (DateRangeColumn) column;
    final int size = column.size();
    for (int i = 0; i < size; i++) {
      appendInternal(drc.getFromInternal(i), drc.getToInternal(i));
    }
    return this;
  }

  @Override // OK
  public DateRangeColumn append(Column<DateRange> column, int row) {
    Preconditions.checkArgument(column.type() == this.type());
    DateRangeColumn drc = ((DateRangeColumn) column);
    return appendInternal(drc.getFromInternal(row), drc.getToInternal(row));
  }

  @Override // OK
  public DateRangeColumn set(int row, Column<DateRange> column, int sourceRow) {
    Preconditions.checkArgument(column.type() == this.type());
    DateRangeColumn drc = (DateRangeColumn) column;
    return set(row, drc.getFromInternal(sourceRow), drc.getToInternal(sourceRow));
  }

  /**
   * Returns the latest range in the column, using the comparison logic defined in DateRange NOTE:
   * Nulls are considered minimum if one is present
   */
  @Override // TODO This logic (nulls are minimum may not be consistent with other cols)
  public DateRange max() {
    if (isEmpty()) {
      return null;
    }

    DateRange max = null;
    for (DateRange next : this) {
      if (next != null) {
        if (max == null) {
          max = next;
        } else {
          max = (max.compareTo(next) >= 0) ? max : next;
        }
      }
    }
    return max;
  }

  /**
   * Returns the earliest range in the column, using the comparison logic defined in DateRange NOTE:
   * Nulls are considered minimum if one is present
   */
  @Override // OK ?
  public DateRange min() {
    if (isEmpty()) {
      return null;
    }

    DateRange min = null;
    for (DateRange next : this) {
      if (next != null) {
        if (min == null) {
          min = next;
        } else {
          min = (min.compareTo(next) <= 0) ? min : next;
        }
      } else {
        min = next;
      }
    }
    return min;
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   *
   * <p>Example: myColumn.set(myColumn.valueIsMissing(), LocalDate.now()); // no more missing values
   */
  @Override // OK
  public DateRangeColumn set(Selection rowSelection, DateRange newValue) {
    int f = newValue.getFromInternal();
    int t = newValue.getToInternal();
    for (int row : rowSelection) {
      set(row, f, t);
    }
    return this;
  }

  @Override // OK
  public DateRangeColumn appendMissing() {
    appendInternal(
        DateRangeColumnType.missingValueIndicator(), DateRangeColumnType.missingValueIndicator());
    return this;
  }

  @Override // OK
  public DateRange get(int index) {
    return new DateRange(from.getInt(index), to.getInt(index));
  }

  // OK
  public LocalDate getFrom(int index) {
    return PackedLocalDate.asLocalDate(getFromInternal(index));
  }

  // OK
  public LocalDate getTo(int index) {
    return PackedLocalDate.asLocalDate(getToInternal(index));
  }

  @Override // OK
  public boolean isEmpty() {
    return from.isEmpty();
  }

  @Override // OK
  public IntComparator rowComparator() {
    return comparator;
  }

  @Override // TODO
  public Selection isMissing() {
    return null;
  }

  @Override // TODO
  public Selection isNotMissing() {
    return null;
  }

  // OK
  public DateRangeColumn append(LocalDate from, LocalDate to) {
    return this.appendInternal(PackedLocalDate.pack(from), PackedLocalDate.pack(to));
  }

  // OK
  public DateRangeColumn append(DateRange range) {
    return this.appendInternal(range.getFromInternal(), range.getToInternal());
  }

  @Override // OK
  public DateRangeColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof DateRange) {
      return append((DateRange) obj);
    }
    throw new IllegalArgumentException(
        "Cannot append " + obj.getClass().getName() + " to DateRangeColumn");
  }

  @Override // OK
  public DateRangeColumn appendCell(String string) {
    return append(DateRangeColumnType.DEFAULT_PARSER.parse(string));
  }

  @Override // OK
  public DateRangeColumn appendCell(String string, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(string));
  }

  @Override
  public Column<DateRange> set(int row, DateRange value) {
    return null;
  }

  // OK
  public int getFromInternal(int index) {
    return from.getInt(index);
  }

  // OK
  public int getToInternal(int index) {
    return to.getInt(index);
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

  public static boolean valueIsMissing(int from, int to) {
    return DateRangeColumnType.valueIsMissing(from, to);
  }

  /** Returns the count of missing values in this column */
  @Override // OK
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (isMissing(i)) {
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
  // TODO
  public List<LocalDate> top(int n) {
    List<LocalDate> top = new ArrayList<>();
    int[] values = from.toIntArray();
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
  // TODO
  public List<LocalDate> bottom(int n) {
    List<LocalDate> bottom = new ArrayList<>();
    int[] values = from.toIntArray();
    IntArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(PackedLocalDate.asLocalDate(values[i]));
    }
    return bottom;
  }

  @Override // OK
  public DateRangeColumn removeMissing() {
    DateRangeColumn noMissing = emptyCopy();
    for (int i = 0; i < size(); i++) {
      if (!isMissing(i)) {
        noMissing.appendInternal(from.getInt(i), to.getInt(i));
      }
    }
    return noMissing;
  }

  @Override // OK
  public List<DateRange> asList() {
    List<DateRange> dates = new ArrayList<>(size());
    for (DateRange range : this) {
      dates.add(range);
    }
    return dates;
  }

  @Override // OK
  public DateRangeColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  // OK
  public Set<DateRange> asSet() {
    Set<DateRange> dateRanges = new HashSet<>();
    DateRangeColumn unique = unique();
    for (DateRange d : unique) {
      dateRanges.add(d);
    }
    return dateRanges;
  }

  @Override // OK
  public boolean contains(DateRange range) {
    int f = range.getFromInternal();
    int t = range.getToInternal();
    for (int i = 0; i < size(); i++) {
      if (from.getInt(i) == f && to.getInt(i) == t) {
        return true;
      }
    }
    return false;
  }

  @Override // OK
  public DateRangeColumn setMissing(int i) {
    return set(
        i,
        DateRangeColumnType.missingValueIndicator(),
        DateRangeColumnType.missingValueIndicator());
  }

  // OK
  public DateRangeColumn setFromMissing(int i) {
    return setFrom(i, DateRangeColumnType.missingValueIndicator());
  }

  // OK
  public DateRangeColumn setToMissing(int i) {
    return setTo(i, DateRangeColumnType.missingValueIndicator());
  }

  @Override // OK
  public boolean isMissing(int rowNumber) {
    return valueIsMissing(getFromInternal(rowNumber), getToInternal(rowNumber));
  }

  @Override // OK
  public int byteSize() {
    return type().byteSize();
  }

  /**
   * Returns the contents of the cell at rowNumber as a byte[]
   *
   * @param rowNumber the number of the row as int
   */
  @Override // OK
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(byteSize())
        .putInt(getFromInternal(rowNumber))
        .putInt(getToInternal(rowNumber))
        .array();
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override // OK
  public Iterator<DateRange> iterator() {

    return new Iterator<DateRange>() {

      final IntIterator frIterator = from.iterator();
      final IntIterator toIterator = to.iterator();

      @Override
      public boolean hasNext() {
        // both columns are the same length
        return frIterator.hasNext();
      }

      @Override
      public DateRange next() {
        int f = frIterator.nextInt();
        int t = toIterator.nextInt();
        if (valueIsMissing(f, t)) {
          return null;
        }
        return new DateRange(PackedLocalDate.asLocalDate(f), PackedLocalDate.asLocalDate(t));
      }
    };
  }

  // fillWith methods

  // OK
  private DateRangeColumn fillWith(
      int count, Iterator<DateRange> iterator, Consumer<DateRange> acceptor) {
    for (int r = 0; r < count; r++) {
      if (!iterator.hasNext()) {
        break;
      }
      acceptor.accept(iterator.next());
    }
    return this;
  }

  @Override
  // OK
  public DateRangeColumn fillWith(Iterator<DateRange> iterator) {
    int[] r = new int[1];
    fillWith(size(), iterator, range -> set(r[0]++, range));
    return this;
  }

  // OK
  private DateRangeColumn fillWith(
      int count, Iterable<DateRange> iterable, Consumer<DateRange> acceptor) {
    Iterator<DateRange> iterator = iterable.iterator();
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
  // OK
  public DateRangeColumn fillWith(Iterable<DateRange> iterable) {
    int[] r = new int[1];
    fillWith(size(), iterable, date -> set(r[0]++, date));
    return this;
  }

  // OK
  private DateRangeColumn fillWith(
      int count, Supplier<DateRange> supplier, Consumer<DateRange> acceptor) {
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
  // OK
  public DateRangeColumn fillWith(Supplier<DateRange> supplier) {
    int[] r = new int[1];
    fillWith(size(), supplier, date -> set(r[0]++, date));
    return this;
  }

  @Override // OK
  public DateRange[] asObjectArray() {
    final DateRange[] output = new DateRange[from.size()];
    for (int i = 0; i < from.size(); i++) {
      output[i] = new DateRange(from.getInt(i), to.getInt(i));
    }
    return output;
  }

  @Override // OK
  public int compare(DateRange o1, DateRange o2) {
    return o1.compareTo(o2);
  }
}

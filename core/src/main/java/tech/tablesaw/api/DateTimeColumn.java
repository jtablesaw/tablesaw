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
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import tech.tablesaw.columns.datetimes.DateTimeColumnFormatter;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.datetimes.DateTimeFilters;
import tech.tablesaw.columns.datetimes.DateTimeMapFunctions;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.columns.temporal.TemporalFillers;
import tech.tablesaw.selection.Selection;

/** A column that contains long-integer encoded (packed) local date-time values */
public class DateTimeColumn extends AbstractColumn<DateTimeColumn, LocalDateTime>
    implements DateTimeMapFunctions,
        DateTimeFilters,
        TemporalFillers<LocalDateTime, DateTimeColumn>,
        CategoricalColumn<LocalDateTime> {

  /** The dateTime values held in this column, in their {@link PackedLocalDateTime} format */
  protected LongArrayList data;

  /** A comparator for this column. Note that the ints compared are the column indexes */
  private final IntComparator comparator =
      (r1, r2) -> {
        long f1 = getPackedDateTime(r1);
        long f2 = getPackedDateTime(r2);
        return Long.compare(f1, f2);
      };

  private DateTimeColumnFormatter printFormatter = new DateTimeColumnFormatter();

  /** {@inheritDoc} */
  @Override
  public int valueHash(int rowNumber) {
    return Long.hashCode(getLongInternal(rowNumber));
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(int rowNumber1, int rowNumber2) {
    return getLongInternal(rowNumber1) == getLongInternal(rowNumber2);
  }

  private DateTimeColumn(String name, LongArrayList data) {
    super(DateTimeColumnType.instance(), name, DateTimeColumnType.DEFAULT_PARSER);
    this.data = data;
  }

  /**
   * For internal Tablesaw use only Returns a new column with the given name and data
   *
   * @param name The column name
   * @param longs An array of longs representing datetime values in Tablesaw internal format
   */
  public static DateTimeColumn createInternal(String name, long[] longs) {
    return new DateTimeColumn(name, new LongArrayList(longs));
  }

  public static DateTimeColumn create(String name) {
    return new DateTimeColumn(name, new LongArrayList(DEFAULT_ARRAY_SIZE));
  }

  public static DateTimeColumn create(String name, int initialSize) {
    DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static DateTimeColumn create(String name, Collection<LocalDateTime> data) {
    DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.size()));
    for (LocalDateTime date : data) {
      column.append(date);
    }
    return column;
  }

  public static DateTimeColumn create(String name, Stream<LocalDateTime> stream) {
    DateTimeColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn plus(long amountToAdd, ChronoUnit unit) {
    DateTimeColumn newColumn = emptyCopy();
    newColumn.setName(temporalColumnName(this, amountToAdd, unit));
    DateTimeColumn column1 = this;

    for (int r = 0; r < column1.size(); r++) {
      long packedDateTime = column1.getLongInternal(r);
      if (packedDateTime == DateTimeColumnType.missingValueIndicator()) {
        newColumn.appendMissing();
      } else {
        newColumn.appendInternal(PackedLocalDateTime.plus(packedDateTime, amountToAdd, unit));
      }
    }
    return newColumn;
  }

  public static DateTimeColumn create(String name, LocalDateTime... data) {
    DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.length));
    for (LocalDateTime date : data) {
      column.append(date);
    }
    return column;
  }

  public static boolean valueIsMissing(long value) {
    return DateTimeColumnType.valueIsMissing(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissing(int rowNumber) {
    return valueIsMissing(getLongInternal(rowNumber));
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn subset(final int[] rows) {
    final DateTimeColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.appendInternal(getLongInternal(row));
    }
    return c;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn removeMissing() {
    DateTimeColumn noMissing = emptyCopy();
    LongIterator iterator = longIterator();
    while (iterator.hasNext()) {
      long i = iterator.nextLong();
      if (!valueIsMissing(i)) {
        noMissing.appendInternal(i);
      }
    }
    return noMissing;
  }

  /** {@inheritDoc} */
  @Override
  public boolean contains(LocalDateTime dateTime) {
    long dt = PackedLocalDateTime.pack(dateTime);
    return data.contains(dt);
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn setMissing(int i) {
    return set(i, DateTimeColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  public void setPrintFormatter(DateTimeFormatter dateTimeFormatter, String missingValueString) {
    Preconditions.checkNotNull(dateTimeFormatter);
    Preconditions.checkNotNull(missingValueString);
    this.printFormatter = new DateTimeColumnFormatter(dateTimeFormatter, missingValueString);
  }

  public void setPrintFormatter(DateTimeFormatter dateTimeFormatter) {
    Preconditions.checkNotNull(dateTimeFormatter);
    this.printFormatter = new DateTimeColumnFormatter(dateTimeFormatter);
  }

  public void setPrintFormatter(DateTimeColumnFormatter formatter) {
    Preconditions.checkNotNull(formatter);
    this.printFormatter = formatter;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn lag(int n) {
    int srcPos = n >= 0 ? 0 : -n;
    long[] dest = new long[size()];
    int destPos = Math.max(n, 0);
    int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = DateTimeColumnType.missingValueIndicator();
    }

    System.arraycopy(data.toLongArray(), srcPos, dest, destPos, length);

    DateTimeColumn copy = emptyCopy(size());
    copy.data = new LongArrayList(dest);
    copy.setName(name() + " lag(" + n + ")");
    return copy;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn appendCell(String stringValue) {
    return appendInternal(PackedLocalDateTime.pack(parser().parse(stringValue)));
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn appendCell(String stringValue, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(stringValue));
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn append(LocalDateTime dateTime) {
    if (dateTime != null) {
      final long dt = PackedLocalDateTime.pack(dateTime);
      appendInternal(dt);
    } else {
      appendInternal(DateTimeColumnType.missingValueIndicator());
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof LocalDateTime) {
      return append((LocalDateTime) obj);
    }
    if (obj instanceof Timestamp) {
      Timestamp timestamp = (Timestamp) obj;
      return append(timestamp.toLocalDateTime());
    }
    throw new IllegalArgumentException(
        "Cannot append " + obj.getClass().getName() + " to DateTimeColumn");
  }

  /** {@inheritDoc} */
  @Override
  public int size() {
    return data.size();
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn appendInternal(long dateTime) {
    data.add(dateTime);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public String getString(int row) {
    return printFormatter.format(getPackedDateTime(row));
  }

  /** {@inheritDoc} */
  @Override
  public String getUnformattedString(int row) {
    return PackedLocalDateTime.toString(getPackedDateTime(row));
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn emptyCopy() {
    DateTimeColumn empty = create(name());
    empty.printFormatter = printFormatter;
    return empty;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn emptyCopy(int rowSize) {
    DateTimeColumn column = create(name(), rowSize);
    column.setPrintFormatter(printFormatter);
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn copy() {
    DateTimeColumn column = emptyCopy(data.size());
    column.data = data.clone();
    column.printFormatter = printFormatter;
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    data.clear();
  }

  /** {@inheritDoc} */
  @Override
  public void sortAscending() {
    data.sort(LongComparators.NATURAL_COMPARATOR);
  }

  /** {@inheritDoc} */
  @Override
  public void sortDescending() {
    data.sort(LongComparators.OPPOSITE_COMPARATOR);
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public int countUnique() {
    LongSet ints = new LongOpenHashSet(data.size());
    for (long i : data) {
      ints.add(i);
    }
    return ints.size();
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn unique() {
    LongSet ints = new LongOpenHashSet(data.size());
    for (long i : data) {
      ints.add(i);
    }
    DateTimeColumn column = emptyCopy(ints.size());
    column.setName(name() + " Unique values");
    column.data = LongArrayList.wrap(ints.toLongArray());
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public long getLongInternal(int index) {
    return data.getLong(index);
  }

  protected long getPackedDateTime(int index) {
    return getLongInternal(index);
  }

  /** {@inheritDoc} */
  @Override
  public LocalDateTime get(int index) {
    return PackedLocalDateTime.asLocalDateTime(getPackedDateTime(index));
  }

  /** {@inheritDoc} */
  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   *
   * <p>Example: myColumn.set(myColumn.valueIsMissing(), LocalDateTime.now()); // no more missing
   * values
   */
  @Override
  public DateTimeColumn set(Selection rowSelection, LocalDateTime newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (getPackedDateTime(i) == DateTimeColumnType.missingValueIndicator()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns an array where each entry is the difference, measured in seconds, between the
   * LocalDateTime and midnight, January 1, 1970 UTC.
   *
   * <p>If a value is missing, DateTimeColumnType.missingValueIndicator() is used
   */
  public long[] asEpochSecondArray() {
    return asEpochSecondArray(ZoneOffset.UTC);
  }

  /**
   * Returns the seconds from epoch for each value as an array based on the given offset
   *
   * <p>If a value is missing, DateTimeColumnType.missingValueIndicator() is used
   */
  public long[] asEpochSecondArray(ZoneOffset offset) {
    long[] output = new long[data.size()];
    for (int i = 0; i < data.size(); i++) {
      LocalDateTime dateTime = PackedLocalDateTime.asLocalDateTime(data.getLong(i));
      if (dateTime == null) {
        output[i] = DateTimeColumnType.missingValueIndicator();
      } else {
        output[i] = dateTime.toEpochSecond(offset);
      }
    }
    return output;
  }

  /**
   * Returns an array where each entry is the difference, measured in milliseconds, between the
   * LocalDateTime and midnight, January 1, 1970 UTC.
   *
   * <p>If a missing value is encountered, DateTimeColumnType.missingValueIndicator() is inserted in
   * the array
   */
  public long[] asEpochMillisArray() {
    return asEpochMillisArray(ZoneOffset.UTC);
  }

  /**
   * Returns an array where each entry is the difference, measured in milliseconds, between the
   * LocalDateTime and midnight, January 1, 1970 UTC.
   *
   * <p>If a missing value is encountered, DateTimeColumnType.missingValueIndicator() is inserted in
   * the array
   */
  public long[] asEpochMillisArray(ZoneOffset offset) {
    long[] output = new long[data.size()];
    for (int i = 0; i < data.size(); i++) {
      LocalDateTime dateTime = PackedLocalDateTime.asLocalDateTime(data.getLong(i));
      if (dateTime == null) {
        output[i] = DateTimeColumnType.missingValueIndicator();
      } else {
        output[i] = dateTime.toInstant(offset).toEpochMilli();
      }
    }
    return output;
  }

  public InstantColumn asInstantColumn() {
    return asInstantColumn(ZoneOffset.UTC);
  }

  public InstantColumn asInstantColumn(ZoneId zone) {
    Instant[] output = new Instant[data.size()];
    for (int i = 0; i < data.size(); i++) {
      LocalDateTime dateTime = PackedLocalDateTime.asLocalDateTime(data.getLong(i));
      if (dateTime == null) {
        output[i] = null;
      } else {
        output[i] = dateTime.atZone(zone).toInstant();
      }
    }
    return InstantColumn.create(name(), output);
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn append(Column<LocalDateTime> column) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    DateTimeColumn dateTimeColumn = (DateTimeColumn) column;
    final int size = dateTimeColumn.size();
    for (int i = 0; i < size; i++) {
      append(dateTimeColumn.get(i));
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn append(Column<LocalDateTime> column, int row) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return appendInternal(((DateTimeColumn) column).getLongInternal(row));
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn set(int row, Column<LocalDateTime> column, int sourceRow) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return set(row, ((DateTimeColumn) column).getLongInternal(sourceRow));
  }

  public LocalDateTime max() {
    long max;
    if (!isEmpty()) {
      max = getPackedDateTime(0);
    } else {
      return null;
    }
    for (long aData : data) {
      if (DateTimeColumnType.missingValueIndicator() != aData) {
        max = Math.max(max, aData);
      }
    }

    if (DateTimeColumnType.missingValueIndicator() == max) {
      return null;
    }
    return PackedLocalDateTime.asLocalDateTime(max);
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn appendMissing() {
    appendInternal(DateTimeColumnType.missingValueIndicator());
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public LocalDateTime min() {
    long min;

    if (!isEmpty()) {
      min = getPackedDateTime(0);
    } else {
      return null;
    }
    for (long aData : data) {
      if (DateTimeColumnType.missingValueIndicator() != aData) {
        min = (min < aData) ? min : aData;
      }
    }
    if (Integer.MIN_VALUE == min) {
      return null;
    }
    return PackedLocalDateTime.asLocalDateTime(min);
  }

  public DateTimeColumn set(int index, long value) {
    data.set(index, value);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn set(int index, LocalDateTime value) {
    return value == null ? setMissing(index) : set(index, PackedLocalDateTime.pack(value));
  }

  /**
   * Returns the largest ("top") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the largest observations
   */
  public List<LocalDateTime> top(int n) {
    List<LocalDateTime> top = new ArrayList<>();
    long[] values = data.toLongArray();
    LongArrays.parallelQuickSort(values, LongComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(PackedLocalDateTime.asLocalDateTime(values[i]));
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
  public List<LocalDateTime> bottom(int n) {
    List<LocalDateTime> bottom = new ArrayList<>();
    long[] values = data.toLongArray();
    LongArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(PackedLocalDateTime.asLocalDateTime(values[i]));
    }
    return bottom;
  }

  public LongIterator longIterator() {
    return data.iterator();
  }

  public Set<LocalDateTime> asSet() {
    Set<LocalDateTime> times = new HashSet<>();
    DateTimeColumn unique = unique();
    for (LocalDateTime localDateTime : unique) {
      times.add(localDateTime);
    }
    return times;
  }

  /** {@inheritDoc} */
  @Override
  public int byteSize() {
    return type().byteSize();
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(byteSize()).putLong(getPackedDateTime(rowNumber)).array();
  }

  public double getDouble(int i) {
    return getPackedDateTime(i);
  }

  public double[] asDoubleArray() {
    double[] doubles = new double[size()];
    long[] millis = asEpochSecondArray();
    for (int i = 0; i < millis.length; i++) {
      doubles[i] = millis[i];
    }
    return doubles;
  }

  public DoubleColumn asDoubleColumn() {
    return DoubleColumn.create(name(), asEpochSecondArray());
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator<LocalDateTime> iterator() {

    return new Iterator<LocalDateTime>() {

      final LongIterator longIterator = longIterator();

      @Override
      public boolean hasNext() {
        return longIterator.hasNext();
      }

      @Override
      public LocalDateTime next() {
        return PackedLocalDateTime.asLocalDateTime(longIterator.nextLong());
      }
    };
  }

  // fillWith methods

  private DateTimeColumn fillWith(
      int count, Iterator<LocalDateTime> iterator, Consumer<LocalDateTime> acceptor) {
    for (int r = 0; r < count; r++) {
      if (!iterator.hasNext()) {
        break;
      }
      acceptor.accept(iterator.next());
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn fillWith(Iterator<LocalDateTime> iterator) {
    int[] r = new int[1];
    fillWith(size(), iterator, date -> set(r[0]++, date));
    return this;
  }

  private DateTimeColumn fillWith(
      int count, Iterable<LocalDateTime> iterable, Consumer<LocalDateTime> acceptor) {
    Iterator<LocalDateTime> iterator = iterable.iterator();
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

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn fillWith(Iterable<LocalDateTime> iterable) {
    int[] r = new int[1];
    fillWith(size(), iterable, date -> set(r[0]++, date));
    return this;
  }

  private DateTimeColumn fillWith(
      int count, Supplier<LocalDateTime> supplier, Consumer<LocalDateTime> acceptor) {
    for (int r = 0; r < count; r++) {
      try {
        acceptor.accept(supplier.get());
      } catch (Exception e) {
        break;
      }
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public DateTimeColumn fillWith(Supplier<LocalDateTime> supplier) {
    int[] r = new int[1];
    fillWith(size(), supplier, date -> set(r[0]++, date));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public LocalDateTime[] asObjectArray() {
    final LocalDateTime[] output = new LocalDateTime[data.size()];
    for (int i = 0; i < data.size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  /** {@inheritDoc} */
  @Override
  public int compare(LocalDateTime o1, LocalDateTime o2) {
    return o1.compareTo(o2);
  }
}

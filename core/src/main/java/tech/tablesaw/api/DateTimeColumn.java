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
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
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
import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.temporal.TemporalFillers;
import tech.tablesaw.selection.Selection;

/** A column in a table that contains long-integer encoded (packed) local date-time values */
public class DateTimeColumn extends AbstractColumn<DateTimeColumn, LocalDateTime>
    implements DateTimeMapFunctions,
        DateTimeFilters,
        TemporalFillers<LocalDateTime, DateTimeColumn>,
        CategoricalColumn<LocalDateTime> {

  private LongArrayList epochSeconds;
  private IntArrayList secondNanos;

  private final IntComparator comparator =
      (r1, r2) -> {
        int cmp = Long.compare(getLongInternal(r1), getLongInternal(r2));
        if (cmp == 0) {
          return getIntInternal(r1) - getIntInternal(r2);
        }
        return cmp;
      };

  private DateTimeColumnFormatter printFormatter = new DateTimeColumnFormatter();

  private DateTimeColumn(String name, LongArrayList epochSeconds, IntArrayList secondNanos) {
    super(DateTimeColumnType.instance(), name);
    this.epochSeconds = epochSeconds;
    this.secondNanos = secondNanos;
  }

  /**
   * For internal Tablesaw use only Returns a new column with the given name and data
   *
   * @param name The column name
   * @param epochSeconds An array of longs representing epoch-seconds
   * @param secondNanos An array of ints representing nanosecond-of-second
   */
  public static DateTimeColumn createInternal(String name, long[] epochSeconds, int[] secondNanos) {
    return new DateTimeColumn(name, new LongArrayList(epochSeconds), new IntArrayList(secondNanos));
  }

  public static DateTimeColumn create(String name) {
    return new DateTimeColumn(
        name, new LongArrayList(DEFAULT_ARRAY_SIZE), new IntArrayList(DEFAULT_ARRAY_SIZE));
  }

  public static DateTimeColumn create(String name, int initialSize) {
    DateTimeColumn column =
        new DateTimeColumn(name, new LongArrayList(initialSize), new IntArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static DateTimeColumn create(String name, Collection<LocalDateTime> data) {
    DateTimeColumn column =
        new DateTimeColumn(name, new LongArrayList(data.size()), new IntArrayList(data.size()));
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

  @Override
  public DateTimeColumn plus(long amountToAdd, ChronoUnit unit) {
    DateTimeColumn newColumn = emptyCopy();
    newColumn.setName(temporalColumnName(this, amountToAdd, unit));
    DateTimeColumn column1 = this;

    for (int r = 0; r < column1.size(); r++) {
      long epochSecond = column1.getLongInternal(r);
      int secondNanos = column1.getIntInternal(r);
      if (epochSecond == DateTimeColumnType.missingValueIndicator()) {
        newColumn.appendMissing();
      } else {
        Instant result = Instant.ofEpochSecond(epochSecond, secondNanos).plus(amountToAdd, unit);
        newColumn.appendInternal(result.getEpochSecond(), result.getNano());
      }
    }
    return newColumn;
  }

  public static DateTimeColumn create(String name, LocalDateTime... data) {
    DateTimeColumn column =
        new DateTimeColumn(name, new LongArrayList(data.length), new IntArrayList(data.length));
    for (LocalDateTime date : data) {
      column.append(date);
    }
    return column;
  }

  public static boolean valueIsMissing(long value) {
    return DateTimeColumnType.valueIsMissing(value);
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return valueIsMissing(getLongInternal(rowNumber));
  }

  @Override
  public DateTimeColumn subset(final int[] rows) {
    final DateTimeColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.appendInternal(getLongInternal(row));
    }
    return c;
  }

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

  @Override
  public boolean contains(LocalDateTime dateTime) {
    for (int i = 0; i < epochSeconds.size(); i++) {
      if (epochSeconds.getLong(i) == dateTime.toEpochSecond(ZoneOffset.UTC)
          && secondNanos.getInt(i) == dateTime.getNano()) return true;
    }
    return false;
  }

  @Override
  public DateTimeColumn setMissing(int i) {
    return set(i, DateTimeColumnType.missingValueIndicator());
  }

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

  @Override
  public DateTimeColumn lag(int n) {
    int srcPos = n >= 0 ? 0 : 0 - n;
    long[] destSeconds = new long[size()];
    int[] destNanos = new int[size()];
    int destPos = n <= 0 ? 0 : n;
    int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      destSeconds[i] = InstantColumnType.missingValueIndicator();
      destNanos[i] = IntColumnType.missingValueIndicator();
    }

    System.arraycopy(epochSeconds.toLongArray(), srcPos, destSeconds, destPos, length);
    System.arraycopy(secondNanos.toIntArray(), srcPos, destNanos, destPos, length);

    DateTimeColumn copy = emptyCopy(size());
    copy.epochSeconds = new LongArrayList(destSeconds);
    copy.secondNanos = new IntArrayList(destNanos);
    copy.setName(name() + " lag(" + n + ")");
    return copy;
  }

  @Override
  public DateTimeColumn appendCell(String stringValue) {
    LocalDateTime ldt = DateTimeColumnType.DEFAULT_PARSER.parse(stringValue);
    if (ldt == null)
      return appendInternal(
          DateTimeColumnType.missingValueIndicator(), IntColumnType.missingValueIndicator());

    return appendInternal(ldt.toEpochSecond(ZoneOffset.UTC), ldt.getNano());
  }

  @Override
  public DateTimeColumn appendCell(String stringValue, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(stringValue));
  }

  public DateTimeColumn append(LocalDateTime dateTime) {
    if (dateTime != null) {
      appendInternal(dateTime.toEpochSecond(ZoneOffset.UTC), dateTime.getNano());
    } else {
      appendInternal(DateTimeColumnType.missingValueIndicator());
    }
    return this;
  }

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

  public int size() {
    return epochSeconds.size();
  }

  public DateTimeColumn appendInternal(long epochSeconds, int secondNanos) {
    this.epochSeconds.add(epochSeconds);
    this.secondNanos.add(secondNanos);
    return this;
  }

  public DateTimeColumn appendInternal(long epochSeconds) {
    this.epochSeconds.add(epochSeconds);
    this.secondNanos.add(0);
    return this;
  }

  @Override
  public String getString(int row) {
    return printFormatter.format(epochSeconds.getLong(row), secondNanos.getInt(row));
  }

  @Override
  public String getUnformattedString(int row) {
    return DateTimeColumnFormatter.defaultFormat(
        epochSeconds.getLong(row), secondNanos.getInt(row));
  }

  @Override
  public DateTimeColumn emptyCopy() {
    DateTimeColumn empty = create(name());
    empty.printFormatter = printFormatter;
    return empty;
  }

  @Override
  public DateTimeColumn emptyCopy(int rowSize) {
    DateTimeColumn column = create(name(), rowSize);
    column.setPrintFormatter(printFormatter);
    return column;
  }

  @Override
  public DateTimeColumn copy() {
    DateTimeColumn column = emptyCopy(epochSeconds.size());
    column.epochSeconds = epochSeconds.clone();
    column.secondNanos = secondNanos.clone();
    return column;
  }

  @Override
  public void clear() {
    epochSeconds.clear();
    secondNanos.clear();
  }

  @Override
  public void sortAscending() {
    // FIXME: consider nanos
    epochSeconds.sort(LongComparators.NATURAL_COMPARATOR);
  }

  @Override
  // FIXME: consider nanos
  public void sortDescending() {
    epochSeconds.sort(LongComparators.OPPOSITE_COMPARATOR);
  }

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

  // FIXME: consider nanos
  @Override
  public int countUnique() {
    LongSet ints = new LongOpenHashSet(epochSeconds.size());
    for (long i : epochSeconds) {
      ints.add(i);
    }
    return ints.size();
  }

  // FIXME: consider nanos
  @Override
  public DateTimeColumn unique() {
    LongSet ints = new LongOpenHashSet(epochSeconds.size());
    for (long i : epochSeconds) {
      ints.add(i);
    }
    DateTimeColumn column = emptyCopy(ints.size());
    column.setName(name() + " Unique values");
    column.epochSeconds = LongArrayList.wrap(ints.toLongArray());
    return column;
  }

  @Override
  public boolean isEmpty() {
    return epochSeconds.isEmpty();
  }

  public long getLongInternal(int index) {
    return epochSeconds.getLong(index);
  }

  public int getIntInternal(int index) {
    return secondNanos.getInt(index);
  }

  protected long getPackedDateTime(int index) {
    return getLongInternal(index);
  }

  public LocalDateTime get(int index) {
    if (epochSeconds.getLong(index) == DateTimeColumnType.missingValueIndicator()) return null;

    return LocalDateTime.ofEpochSecond(
        epochSeconds.getLong(index), secondNanos.getInt(index), ZoneOffset.UTC);
  }

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
    long[] output = new long[epochSeconds.size()];
    for (int i = 0; i < epochSeconds.size(); i++) {
      LocalDateTime dateTime =
          LocalDateTime.from(Instant.ofEpochSecond(epochSeconds.getLong(i), secondNanos.getInt(i)));
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
    long[] output = new long[epochSeconds.size()];
    for (int i = 0; i < epochSeconds.size(); i++) {
      LocalDateTime dateTime =
          LocalDateTime.ofEpochSecond(
              epochSeconds.getLong(i), secondNanos.getInt(i), ZoneOffset.UTC);
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
    Instant[] output = new Instant[epochSeconds.size()];
    for (int i = 0; i < epochSeconds.size(); i++) {
      LocalDateTime dateTime =
          LocalDateTime.ofEpochSecond(
              epochSeconds.getLong(i), secondNanos.getInt(i), ZoneOffset.UTC);
      if (dateTime == null) {
        output[i] = null;
      } else {
        output[i] = dateTime.atZone(zone).toInstant();
      }
    }
    return InstantColumn.create(name(), output);
  }

  @Override
  public DateTimeColumn append(Column<LocalDateTime> column) {
    Preconditions.checkArgument(column.type() == this.type());
    DateTimeColumn dateTimeColumn = (DateTimeColumn) column;
    final int size = dateTimeColumn.size();
    for (int i = 0; i < size; i++) {
      append(dateTimeColumn.get(i));
    }
    return this;
  }

  @Override
  public DateTimeColumn append(Column<LocalDateTime> column, int row) {
    Preconditions.checkArgument(column.type() == this.type());
    DateTimeColumn col = (DateTimeColumn) column;
    return appendInternal(col.getLongInternal(row), col.getIntInternal(row));
  }

  @Override
  public DateTimeColumn set(int row, Column<LocalDateTime> column, int sourceRow) {
    Preconditions.checkArgument(column.type() == this.type());
    DateTimeColumn col = (DateTimeColumn) column;
    return set(row, col.getLongInternal(sourceRow), col.getIntInternal(sourceRow));
  }

  public LocalDateTime max() {
    long maxEpochSeconds;
    int maxSecondNanos;
    if (!isEmpty()) {
      maxEpochSeconds = getPackedDateTime(0);
      maxSecondNanos = getIntInternal(0);
    } else {
      return null;
    }
    for (int i = 0; i < epochSeconds.size(); i++) {
      if (DateTimeColumnType.missingValueIndicator() != epochSeconds.getLong(i)) {
        int cmp = Long.compare(maxEpochSeconds, epochSeconds.getLong(i));
        if (cmp != 0) {
          maxEpochSeconds = (cmp > 0) ? maxEpochSeconds : epochSeconds.getLong(i);
          maxSecondNanos = (cmp > 0) ? maxSecondNanos : secondNanos.getInt(i);
        }

        int iCmp = Integer.compare(maxSecondNanos, secondNanos.getInt(i));
        if (iCmp != 0) {
          maxEpochSeconds = (cmp > 0) ? maxEpochSeconds : epochSeconds.getLong(i);
          maxSecondNanos = (cmp > 0) ? maxSecondNanos : secondNanos.getInt(i);
        }
      }
    }

    if (DateTimeColumnType.missingValueIndicator() == maxEpochSeconds) {
      return null;
    }
    return LocalDateTime.ofEpochSecond(maxEpochSeconds, maxSecondNanos, ZoneOffset.UTC);
  }

  @Override
  public DateTimeColumn appendMissing() {
    appendInternal(
        DateTimeColumnType.missingValueIndicator(), IntColumnType.missingValueIndicator());
    return this;
  }

  @Override
  public LocalDateTime min() {
    long minEpochSeconds;
    int minSecondNanos;

    if (!isEmpty()) {
      minEpochSeconds = getPackedDateTime(0);
      minSecondNanos = getIntInternal(0);
    } else {
      return null;
    }
    for (int i = 0; i < epochSeconds.size(); i++) {
      if (DateTimeColumnType.missingValueIndicator() != epochSeconds.getLong(i)) {
        int cmp = Long.compare(minEpochSeconds, epochSeconds.getLong(i));
        if (cmp != 0) {
          minEpochSeconds = (cmp < 0) ? minEpochSeconds : epochSeconds.getLong(i);
          minSecondNanos = (cmp < 0) ? minSecondNanos : secondNanos.getInt(i);
        }

        int iCmp = Integer.compare(minSecondNanos, secondNanos.getInt(i));
        if (iCmp != 0) {
          minEpochSeconds = (cmp < 0) ? minEpochSeconds : epochSeconds.getLong(i);
          minSecondNanos = (cmp < 0) ? minSecondNanos : secondNanos.getInt(i);
        }
      }
    }
    if (Integer.MIN_VALUE == minEpochSeconds) {
      return null;
    }
    return LocalDateTime.ofEpochSecond(minEpochSeconds, minSecondNanos, ZoneOffset.UTC);
  }

  public DateTimeColumn set(int index, long value) {
    epochSeconds.set(index, value);
    return this;
  }

  public DateTimeColumn set(int index, long epochSeconds, int secondNanos) {
    this.epochSeconds.set(index, epochSeconds);
    this.secondNanos.set(index, secondNanos);
    return this;
  }

  @Override
  public DateTimeColumn set(int index, LocalDateTime value) {
    return value == null
        ? setMissing(index)
        : set(index, value.toEpochSecond(ZoneOffset.UTC), value.getNano());
  }

  /**
   * Returns the largest ("top") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the largest observations
   */
  // FIXME: consider nanos
  public List<LocalDateTime> top(int n) {
    List<LocalDateTime> top = new ArrayList<>();
    long[] values = epochSeconds.toLongArray();
    LongArrays.parallelQuickSort(values, LongComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(LocalDateTime.ofEpochSecond(values[i], 0, ZoneOffset.UTC));
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
  // FIXME: consider nanos
  public List<LocalDateTime> bottom(int n) {
    List<LocalDateTime> bottom = new ArrayList<>();
    long[] values = epochSeconds.toLongArray();
    LongArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(LocalDateTime.ofEpochSecond(values[i], 0, ZoneOffset.UTC));
    }
    return bottom;
  }

  public LongIterator longIterator() {
    return epochSeconds.iterator();
  }

  public IntIterator intIterator() {
    return secondNanos.iterator();
  }

  public Set<LocalDateTime> asSet() {
    Set<LocalDateTime> times = new HashSet<>();
    DateTimeColumn unique = unique();
    for (LocalDateTime localDateTime : unique) {
      times.add(localDateTime);
    }
    return times;
  }

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
      final IntIterator intIterator = intIterator();

      @Override
      public boolean hasNext() {
        return longIterator.hasNext();
      }

      @Override
      public LocalDateTime next() {
        return LocalDateTime.ofEpochSecond(
            longIterator.nextLong(), intIterator.nextInt(), ZoneOffset.UTC);
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

  @Override
  public DateTimeColumn fillWith(Supplier<LocalDateTime> supplier) {
    int[] r = new int[1];
    fillWith(size(), supplier, date -> set(r[0]++, date));
    return this;
  }

  @Override
  public LocalDateTime[] asObjectArray() {
    final LocalDateTime[] output = new LocalDateTime[epochSeconds.size()];
    for (int i = 0; i < epochSeconds.size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  @Override
  public int compare(LocalDateTime o1, LocalDateTime o2) {
    return o1.compareTo(o2);
  }
}

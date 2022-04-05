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

import static tech.tablesaw.columns.temporal.TemporalPredicates.isMissing;
import static tech.tablesaw.columns.temporal.TemporalPredicates.isNotMissing;

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
import tech.tablesaw.columns.instant.InstantColumnFormatter;
import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.columns.instant.InstantMapFunctions;
import tech.tablesaw.columns.instant.PackedInstant;
import tech.tablesaw.columns.temporal.TemporalFillers;
import tech.tablesaw.columns.temporal.TemporalFilters;
import tech.tablesaw.selection.Selection;

/**
 * A column that contains long-integer encoded (packed) instant values. An instant is a unique point
 * of time on the timeline. The instants held by Instant column have millisecond precision, unlike
 * instances of {@link java.time.Instant}, which have nanosecond precision
 */
public class InstantColumn extends AbstractColumn<InstantColumn, Instant>
    implements InstantMapFunctions,
        TemporalFillers<Instant, InstantColumn>,
        TemporalFilters<Instant>,
        CategoricalColumn<Instant> {

  protected LongArrayList data;

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

  private final IntComparator comparator =
      (r1, r2) -> {
        long f1 = getPackedInstant(r1);
        long f2 = getPackedInstant(r2);
        return Long.compare(f1, f2);
      };

  private InstantColumnFormatter printFormatter = new InstantColumnFormatter();

  private InstantColumn(String name, LongArrayList data) {
    super(InstantColumnType.instance(), name, InstantColumnType.DEFAULT_PARSER);
    this.data = data;
  }

  public static InstantColumn create(String name) {
    return new InstantColumn(name, new LongArrayList(DEFAULT_ARRAY_SIZE));
  }

  /**
   * For internal Tablesaw use only Returns a new column with the given name and data
   *
   * @param name The column name
   * @param data An array of longs representing Instant values in Tablesaw internal format
   */
  public static InstantColumn createInternal(String name, long[] data) {
    return new InstantColumn(name, new LongArrayList(data));
  }

  public static InstantColumn create(String name, int initialSize) {
    InstantColumn column = new InstantColumn(name, new LongArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static InstantColumn create(String name, Collection<Instant> data) {
    InstantColumn column = new InstantColumn(name, new LongArrayList(data.size()));
    for (Instant date : data) {
      column.append(date);
    }
    return column;
  }

  public static InstantColumn create(String name, Instant... data) {
    InstantColumn column = new InstantColumn(name, new LongArrayList(data.length));
    for (Instant date : data) {
      column.append(date);
    }
    return column;
  }

  public static InstantColumn create(String name, Stream<Instant> stream) {
    InstantColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  public static boolean valueIsMissing(long value) {
    return InstantColumnType.valueIsMissing(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissing(int rowNumber) {
    return valueIsMissing(getLongInternal(rowNumber));
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn plus(long amountToAdd, ChronoUnit unit) {
    InstantColumn newColumn = emptyCopy();
    newColumn.setName(temporalColumnName(this, amountToAdd, unit));
    InstantColumn column1 = this;

    for (int r = 0; r < column1.size(); r++) {
      long packedDateTime = column1.getLongInternal(r);
      if (packedDateTime == InstantColumnType.missingValueIndicator()) {
        newColumn.appendMissing();
      } else {
        newColumn.appendInternal(PackedInstant.plus(packedDateTime, amountToAdd, unit));
      }
    }
    return newColumn;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn subset(final int[] rows) {
    final InstantColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.appendInternal(getLongInternal(row));
    }
    return c;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn removeMissing() {
    InstantColumn noMissing = emptyCopy();
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
  public boolean contains(Instant dateTime) {
    long dt = PackedInstant.pack(dateTime);
    return data.contains(dt);
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn setMissing(int i) {
    return set(i, InstantColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  /**
   * Sets the print formatter to the argument. The print formatter is used in pretty-printing and
   * optionally for writing to text files like CSVs
   */
  public void setPrintFormatter(InstantColumnFormatter formatter) {
    Preconditions.checkNotNull(formatter);
    this.printFormatter = formatter;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn lag(int n) {
    int srcPos = n >= 0 ? 0 : -n;
    long[] dest = new long[size()];
    int destPos = Math.max(n, 0);
    int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = InstantColumnType.missingValueIndicator();
    }

    System.arraycopy(data.toLongArray(), srcPos, dest, destPos, length);

    InstantColumn copy = emptyCopy(size());
    copy.data = new LongArrayList(dest);
    copy.setName(name() + " lag(" + n + ")");
    return copy;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn appendCell(String stringValue) {
    return appendInternal(PackedInstant.pack(parser().parse(stringValue)));
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn appendCell(String stringValue, AbstractColumnParser<?> parser) {
    return appendObj(parser.parse(stringValue));
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn append(Instant dateTime) {
    if (dateTime != null) {
      final long dt = PackedInstant.pack(dateTime);
      appendInternal(dt);
    } else {
      appendInternal(InstantColumnType.missingValueIndicator());
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof Instant) {
      return append((Instant) obj);
    }
    if (obj instanceof Timestamp) {
      Timestamp timestamp = (Timestamp) obj;
      return append(timestamp.toInstant());
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
  public InstantColumn appendInternal(long dateTime) {
    data.add(dateTime);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public String getString(int row) {
    return printFormatter.format(getPackedInstant(row));
  }

  /** {@inheritDoc} */
  @Override
  public String getUnformattedString(int row) {
    return PackedInstant.toString(getPackedInstant(row));
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn emptyCopy() {
    InstantColumn empty = create(name());
    empty.printFormatter = printFormatter;
    return empty;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn emptyCopy(int rowSize) {
    InstantColumn column = create(name(), rowSize);
    column.setPrintFormatter(printFormatter);
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn copy() {
    InstantColumn column = emptyCopy(data.size());
    column.data = data.clone();
    column.printFormatter = this.printFormatter;
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
  public InstantColumn unique() {
    LongSet ints = new LongOpenHashSet(data.size());
    for (long i : data) {
      ints.add(i);
    }
    InstantColumn column = emptyCopy(ints.size());
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

  /** Returns the long-encoded version of the instant at the given index */
  protected long getPackedInstant(int index) {
    return getLongInternal(index);
  }

  /** {@inheritDoc} */
  @Override
  public Instant get(int index) {
    return PackedInstant.asInstant(getPackedInstant(index));
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
   * <p>Example: myColumn.set(myColumn.valueIsMissing(), Instant.now()); // no more missing values
   */
  @Override
  public InstantColumn set(Selection rowSelection, Instant newValue) {
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
      if (getPackedInstant(i) == InstantColumnType.missingValueIndicator()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Returns an array where each entry is the difference, measured in seconds, between the Instant
   * and midnight, January 1, 1970 UTC.
   *
   * <p>If a value is missing, InstantColumnType.missingValueIndicator() is used
   */
  public long[] asEpochSecondArray() {
    return asEpochSecondArray(ZoneOffset.UTC);
  }

  /**
   * Returns the seconds from epoch for each value as an array based on the given offset
   *
   * <p>If a value is missing, InstantColumnType.missingValueIndicator() is used
   */
  public long[] asEpochSecondArray(ZoneOffset offset) {
    long[] output = new long[data.size()];
    for (int i = 0; i < data.size(); i++) {
      Instant instant = PackedInstant.asInstant(data.getLong(i));
      if (instant == null) {
        output[i] = InstantColumnType.missingValueIndicator();
      } else {
        output[i] = instant.getEpochSecond();
      }
    }
    return output;
  }

  /**
   * Returns an array where each entry is the difference, measured in milliseconds, between the
   * Instant and midnight, January 1, 1970 UTC.
   *
   * <p>If a missing value is encountered, InstantColumnType.missingValueIndicator() is inserted in
   * the array
   */
  public long[] asEpochMillisArray() {
    return asEpochMillisArray(ZoneOffset.UTC);
  }

  /**
   * Returns an array where each entry is the difference, measured in milliseconds, between the
   * Instant and midnight, January 1, 1970 UTC.
   *
   * <p>If a missing value is encountered, InstantColumnType.missingValueIndicator() is inserted in
   * the array
   */
  public long[] asEpochMillisArray(ZoneOffset offset) {
    long[] output = new long[data.size()];
    for (int i = 0; i < data.size(); i++) {
      Instant instant = PackedInstant.asInstant(data.getLong(i));
      if (instant == null) {
        output[i] = InstantColumnType.missingValueIndicator();
      } else {
        output[i] = instant.toEpochMilli();
      }
    }
    return output;
  }

  /**
   * Returns a DateTimeColumn where each element is a representation of the associated Instant
   * translated using UTC as the timezone
   */
  public DateTimeColumn asLocalDateTimeColumn() {
    return asLocalDateTimeColumn(ZoneOffset.UTC);
  }

  /**
   * Returns a DateTimeColumn where each element is a representation of the associated Instant
   * translated using the argument as the timezone
   */
  public DateTimeColumn asLocalDateTimeColumn(ZoneId zone) {
    LocalDateTime[] output = new LocalDateTime[data.size()];
    for (int i = 0; i < data.size(); i++) {
      Instant instant = PackedInstant.asInstant(data.getLong(i));
      if (instant == null) {
        output[i] = null;
      } else {
        output[i] = LocalDateTime.ofInstant(instant, zone);
      }
    }
    return DateTimeColumn.create(name(), output);
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn append(Column<Instant> column) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    InstantColumn dateTimeColumn = (InstantColumn) column;
    final int size = dateTimeColumn.size();
    for (int i = 0; i < size; i++) {
      append(dateTimeColumn.get(i));
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn append(Column<Instant> column, int row) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return appendInternal(((InstantColumn) column).getLongInternal(row));
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn set(int row, Column<Instant> column, int sourceRow) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return set(row, ((InstantColumn) column).getLongInternal(sourceRow));
  }

  /** Returns the largest instant value in the column */
  public Instant max() {
    if (isEmpty()) {
      return null;
    }
    long max = Long.MIN_VALUE;
    boolean allMissing = true;
    for (long aData : data) {
      if (InstantColumnType.missingValueIndicator() != aData) {
        max = Math.max(max, aData);
        allMissing = false;
      }
    }
    if (allMissing) {
      return null;
    }
    return PackedInstant.asInstant(max);
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn appendMissing() {
    appendInternal(InstantColumnType.missingValueIndicator());
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Instant min() {
    if (isEmpty()) {
      return null;
    }
    long min = Long.MAX_VALUE;
    boolean allMissing = true;
    for (long aData : data) {
      if (InstantColumnType.missingValueIndicator() != aData) {
        min = Math.min(min, aData);
        allMissing = false;
      }
    }
    if (allMissing) {
      return null;
    }
    return PackedInstant.asInstant(min);
  }

  public InstantColumn set(int index, long value) {
    data.set(index, value);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public InstantColumn set(int index, Instant value) {
    return value == null ? setMissing(index) : set(index, PackedInstant.pack(value));
  }

  /**
   * Returns the largest ("top") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the largest observations
   */
  public List<Instant> top(int n) {
    List<Instant> top = new ArrayList<>();
    long[] values = data.toLongArray();
    LongArrays.parallelQuickSort(values, LongComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(PackedInstant.asInstant(values[i]));
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
  public List<Instant> bottom(int n) {
    List<Instant> bottom = new ArrayList<>();
    long[] values = data.toLongArray();
    LongArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(PackedInstant.asInstant(values[i]));
    }
    return bottom;
  }

  /** Returns an iterator over the long representations of the instants in this column */
  public LongIterator longIterator() {
    return data.iterator();
  }

  /** {@inheritDoc} */
  @Override
  public Set<Instant> asSet() {
    Set<Instant> times = new HashSet<>();
    InstantColumn unique = unique();
    for (Instant Instant : unique) {
      times.add(Instant);
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
    return ByteBuffer.allocate(byteSize()).putLong(getPackedInstant(rowNumber)).array();
  }

  public double getDouble(int i) {
    return getPackedInstant(i);
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
  public Iterator<Instant> iterator() {

    return new Iterator<Instant>() {

      final LongIterator longIterator = longIterator();

      @Override
      public boolean hasNext() {
        return longIterator.hasNext();
      }

      @Override
      public Instant next() {
        return PackedInstant.asInstant(longIterator.nextLong());
      }
    };
  }

  // fillWith methods

  private InstantColumn fillWith(
      int count, Iterator<Instant> iterator, Consumer<Instant> acceptor) {
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
  public InstantColumn fillWith(Iterator<Instant> iterator) {
    int[] r = new int[1];
    fillWith(size(), iterator, date -> set(r[0]++, date));
    return this;
  }

  private InstantColumn fillWith(
      int count, Iterable<Instant> iterable, Consumer<Instant> acceptor) {
    Iterator<Instant> iterator = iterable.iterator();
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
  public InstantColumn fillWith(Iterable<Instant> iterable) {
    int[] r = new int[1];
    fillWith(size(), iterable, date -> set(r[0]++, date));
    return this;
  }

  private InstantColumn fillWith(
      int count, Supplier<Instant> supplier, Consumer<Instant> acceptor) {
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
  public InstantColumn fillWith(Supplier<Instant> supplier) {
    int[] r = new int[1];
    fillWith(size(), supplier, date -> set(r[0]++, date));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Instant[] asObjectArray() {
    final Instant[] output = new Instant[data.size()];
    for (int i = 0; i < data.size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  /** {@inheritDoc} */
  @Override
  public int compare(Instant o1, Instant o2) {
    return o1.compareTo(o2);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isMissing() {
    return eval(isMissing);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isNotMissing() {
    return eval(isNotMissing);
  }
}

package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.io.TypeUtils;
import com.google.common.base.Strings;
import net.mintern.primitive.Primitive;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * A column in a base table that contains float values
 */
public class LocalDateTimeColumn extends AbstractColumn {

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private long[] dateTimes;

  @Override
  public void addCell(String stringvalue) {

    if (stringvalue == null) {
      add(Long.MIN_VALUE);
    } else {
        LocalDateTime dateTime = convert(stringvalue);
        if (dateTime != null) {
          add(dateTime);
        } else {
          add(Long.MIN_VALUE);
        }
    }
  }

  public void add(LocalDateTime dateTime) {
    long dt = PackedLocalDateTime.pack(dateTime);
    add(dt);
  }

  public LocalDateTime convert(String value) {
    if (Strings.isNullOrEmpty(value)
        || TypeUtils.MISSING_INDICATORS.contains(value)
        || value.equals("-1")) {
      return null;
    }
    value = Strings.padStart(value, 4, '0');
    return LocalDateTime.parse(value, TypeUtils.dateTimeFormatter);
  }

  public static LocalDateTimeColumn create(String name) {
    return new LocalDateTimeColumn(name);
  }

  private LocalDateTimeColumn(String name) {
    super(name);
    dateTimes = new long[DEFAULT_ARRAY_SIZE];
  }

  public LocalDateTimeColumn(String name, int initialSize) {
    super(name);
    dateTimes = new long[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public ColumnType type() {
    return ColumnType.LOCAL_DATE_TIME;
  }

  @Override
  public boolean hasNext() {
    return pointer < N;
  }

  public float next() {
    return dateTimes[pointer++];
  }

  public void add(long dateTime) {
    if (N >= dateTimes.length) {
      resize();
    }
    dateTimes[N++] = dateTime;
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    int size = Math.round(dateTimes.length * 2);
    long[] tempDates = new long[size];
    System.arraycopy(dateTimes, 0, tempDates, 0, N);
    dateTimes = tempDates;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    long[] tempDates = new long[N + 100];
    System.arraycopy(dateTimes, 0, tempDates, 0, N);
    dateTimes = tempDates;
  }

  @Override
  public String getString(int row) {
    return PackedLocalDateTime.toString(dateTimes[row]);
  }

  @Override
  public LocalDateTimeColumn emptyCopy() {
    return new LocalDateTimeColumn(name());
  }

  @Override
  public void clear() {
    dateTimes = new long[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private LocalDateTimeColumn copy() {
    LocalDateTimeColumn copy = emptyCopy();
    copy.dateTimes = this.dateTimes;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    LocalDateTimeColumn copy = this.copy();
    Arrays.sort(copy.dateTimes);
    return copy;

  }

  @Override
  public Column sortDescending() {
    LocalDateTimeColumn copy = this.copy();
    Primitive.sort(copy.dateTimes, (d1, d2) -> Long.compare(d2, d1), false);
    return copy;
  }

  // TODO(lwhite): Implement column summary()
  @Override
  public Table summary() {
    return null;
  }

  // TODO(lwhite): Implement countUnique()
  @Override
  public int countUnique() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }
}

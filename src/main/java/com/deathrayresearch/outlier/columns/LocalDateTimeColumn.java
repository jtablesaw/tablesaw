package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.DateTimeMapUtils;
import com.google.common.base.Strings;
import net.mintern.primitive.Primitive;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A column in a base table that contains float values
 */
public class LocalDateTimeColumn extends AbstractColumn implements DateTimeMapUtils {

  public static final Long MISSING_VALUE = Long.MIN_VALUE;

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private long[] data;

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
    data = new long[DEFAULT_ARRAY_SIZE];
  }

  public LocalDateTimeColumn(String name, int initialSize) {
    super(name);
    data = new long[initialSize];
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
    return data[pointer++];
  }

  public void add(long dateTime) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = dateTime;
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    int size = Math.round(data.length * 2);
    long[] tempDates = new long[size];
    System.arraycopy(data, 0, tempDates, 0, N);
    data = tempDates;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    long[] tempDates = new long[N + 100];
    System.arraycopy(data, 0, tempDates, 0, N);
    data = tempDates;
  }

  @Override
  public String getString(int row) {
    return PackedLocalDateTime.toString(data[row]);
  }

  @Override
  public LocalDateTimeColumn emptyCopy() {
    return new LocalDateTimeColumn(name());
  }

  @Override
  public void clear() {
    data = new long[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private LocalDateTimeColumn copy() {
    LocalDateTimeColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    LocalDateTimeColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;

  }

  @Override
  public Column sortDescending() {
    LocalDateTimeColumn copy = this.copy();
    Primitive.sort(copy.data, (d1, d2) -> Long.compare(d2, d1), false);
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

  public long get(int index) {
    return data[index];
  }

  @Override
  public Comparator<Integer> rowComparator() {
    return comparator;
  }

  Comparator<Integer> comparator = new Comparator<Integer>() {

    @Override
    public int compare(Integer r1, Integer r2) {
      long f1 = data[r1];
      long f2 = data[r2];
      return Long.compare(f1, f2);
    }
  };

  public CategoryColumn dayOfWeek() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " day of week" , this.size());
    for (int r = 0; r < this.size(); r++) {
      Long c1 = this.get(r);
      if (c1.equals(LocalDateTimeColumn.MISSING_VALUE)) {
        newColumn.set(r, null);
      } else {
        LocalDateTime value1 = PackedLocalDateTime.asLocalDateTime(c1);
        newColumn.add(value1.getDayOfWeek().toString());
      }
    }
    return newColumn;
  }

  public IntColumn dayOfYear() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of year", this.size());
    for (int r = 0; r < this.size(); r++) {
      Long c1 = this.get(r);
      if (c1.equals(LocalDateTimeColumn.MISSING_VALUE)) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getDayOfYear(c1));
      }
    }
    return newColumn;
  }

  public IntColumn dayOfMonth() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      Long c1 = this.get(r);
      if (c1 == FloatColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getDayOfMonth(c1));
      }
    }
    return newColumn;
  }

  public IntColumn monthNumber() {
    IntColumn newColumn = IntColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.get(r);
      if (c1 == MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getMonthValue(c1));
      }
    }
    return newColumn;
  }

  public CategoryColumn monthName() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.get(r);
      if (c1 == MISSING_VALUE) {
        newColumn.add(CategoryColumn.MISSING_VALUE);
      } else {
        newColumn.add(Month.of(PackedLocalDateTime.getMonthValue(c1)).name());
      }
    }
    return newColumn;
  }

  public IntColumn year() {
    IntColumn newColumn = IntColumn.create(this.name() + " year");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.get(r);
      if (c1 == MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getYear(PackedLocalDateTime.date(c1)));
      }
    }
    return newColumn;
  }

  public RoaringBitmap isEqualTo(LocalDateTime value) {
    RoaringBitmap results = new RoaringBitmap();
    long packedLocalDate = PackedLocalDateTime.pack(value);
    int i = 0;
    while (hasNext()) {
      if (packedLocalDate == next()) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }
}

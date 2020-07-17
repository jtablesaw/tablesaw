package tech.tablesaw.columns.dateranges;

import com.google.common.base.Objects;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import tech.tablesaw.columns.dates.PackedLocalDate;

public class DateRange implements Comparable<DateRange> {
  static final String DEFAULT_SEPARATOR = "/";
  private final int from;
  private final int to;

  public DateRange(LocalDate from, LocalDate to) {
    this.from = PackedLocalDate.pack(from);
    this.to = PackedLocalDate.pack(to);
  }

  public DateRange(int packedDateFrom, int packedDateTo) {
    this.from = packedDateFrom;
    this.to = packedDateTo;
  }

  // TODO
  public static DateRange parse(String s, String separator, DateTimeFormatter formatter) {
    String[] tokens = s.split(separator);
    LocalDate from = LocalDate.parse(tokens[0], formatter);
    LocalDate to = LocalDate.parse(tokens[1], formatter);
    return new DateRange(from, to);
  }

  public LocalDate getFrom() {
    return PackedLocalDate.asLocalDate(from);
  }

  public LocalDate getTo() {
    return PackedLocalDate.asLocalDate(to);
  }

  public int getFromInternal() {
    return from;
  }

  public int getToInternal() {
    return to;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DateRange dateRange = (DateRange) o;
    return Objects.equal(getFrom(), dateRange.getFrom())
        && Objects.equal(getTo(), dateRange.getTo());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getFrom(), getTo());
  }

  /**
   * Returns this date range in ISO Standard Format (e.g. 2013-01-01/2013-06-31) See:
   * https://en.wikipedia.org/wiki/ISO_8601#Time_intervals
   */
  @Override
  public String toString() {
    LocalDate f = getFrom();
    LocalDate t = getTo();
    String fString = "";
    String tString = "";
    if (f != null) {
      fString = DateTimeFormatter.ISO_LOCAL_DATE.format(getFrom());
    }
    if (t != null) {
      tString = DateTimeFormatter.ISO_LOCAL_DATE.format(getTo());
    }
    return new StringBuilder(fString).append(DEFAULT_SEPARATOR).append(tString).toString();
  }

  /**
   * Compares this object with the specified object for order. Returns a negative integer, zero, or
   * a positive integer as this object is less than, equal to, or greater than the specified object.
   *
   * <p>DateRange A is considered less than DateRange B if it starts before B, or if it starts on
   * the same date as B, but ends before B
   *
   * @param o the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
   *     or greater than the specified object.
   * @throws NullPointerException if the specified object is null
   * @throws ClassCastException if the specified object's type prevents it from being compared to
   *     this object.
   */
  @Override
  public int compareTo(DateRange o) {
    if (getFromInternal() < o.getFromInternal()) {
      return -1;
    }
    if (getToInternal() > o.getToInternal()) {
      return 1;
    }
    return (Integer.compare(getToInternal(), o.getToInternal()));
  }
}

package tech.tablesaw.columns.dateranges;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import tech.tablesaw.columns.dates.PackedLocalDate;

/**
 * A DateRange class for use with DateRangeColumn There is no standard Java implementation of this
 * class, or the concept it represents
 */
public class DateRange implements Comparable<DateRange> {
  static final String DEFAULT_SEPARATOR = "/";
  private final int from;
  private final int to;

  public DateRange(LocalDate from, LocalDate to) {
    this.from = PackedLocalDate.pack(from);
    this.to = PackedLocalDate.pack(to);
  }

  /**
   * Returns a new DateRange created from ints representing Tableaw packedLocalDates
   *
   * @param packedDateFrom The start of the range in tablesaw PackedLocalDate representation
   * @param packedDateTo The end of the range in tablesaw PackedLocalDate representation
   */
  public DateRange(int packedDateFrom, int packedDateTo) {
    this.from = packedDateFrom;
    this.to = packedDateTo;
  }

  /**
   * Returns a DateRange object parsed from the given string s
   *
   * <p>If the s
   *
   * @param s The string to parse in the form [fromDate][separator][toDate]
   * @param separator The delimiter separating the from and to parts of the range. If it's null or
   *     empty, DateRange.DEFAULT_SEPARATOR is used
   * @param formatter The formatter to use for parsing. It is applied to from and to individually,
   *     parsing them into LocalDate values
   * @return a new DateRange
   */
  public static DateRange parse(String s, String separator, DateTimeFormatter formatter) {
    String parseError =
        "String %s could not be parsed because it doesn't contain the separator [%s]";
    String sep = Strings.isNullOrEmpty(separator) ? DEFAULT_SEPARATOR : separator;
    Preconditions.checkArgument(s.contains(sep), String.format(parseError, s, sep));

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

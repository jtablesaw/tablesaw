package com.deathrayresearch.outlier.io;

import com.deathrayresearch.outlier.columns.BooleanColumn;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.columns.LocalDateTimeColumn;
import com.deathrayresearch.outlier.columns.LocalTimeColumn;
import com.deathrayresearch.outlier.columns.PackedLocalTime;
import com.deathrayresearch.outlier.columns.TextColumn;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Utilities for working with {@link ColumnType}s
 */
public final class TypeUtils {

  /**
   * A unary lambda expression that converts the input value to a boolean.
   * </p>
   * The expression will throw a java.lang.IllegalArgumentException if the value cannot be
   * converted
   */
  public static final UnaryOperator<Comparable> CONVERT_TO_BOOLEAN = value -> {
    if (value == null) {
      return null;
    } else if (value instanceof Boolean) {
      return (Boolean) value;
    } else if (value instanceof Integer) {
      return ((Integer) value != 0);
    } else if (value instanceof String) {
      if (((String) value).isEmpty()) {
        return ColumnType.BOOLEAN.getMissingValue();
      }
      return TypeUtils.TRUE_STRINGS.contains(value);
    } else {
      throw new IllegalArgumentException("Attempting to convert non-boolean value " +
          value + " to Boolean");
    }
  };

  /**
   * A unary lambda expression that converts the input value to a string.
   */
  public static final UnaryOperator<Comparable> CONVERT_TO_STRING = t -> {
    @Nullable String result = String.valueOf(t);
    if (Strings.isNullOrEmpty(result) || result.equals("null")) {
      return ColumnType.TEXT.getMissingValue();
    }
    return result;
  };

  public static final UnaryOperator<Comparable> CONVERT_TO_CATEGORY = t -> {
    @Nullable String result = String.valueOf(t);
    if (Strings.isNullOrEmpty(result) || result.equals("null")) {
      return ColumnType.CAT.getMissingValue();
    }
    return result;
  };

  /**
   * A unary lambda expression that does nothing, permitting a column to be skipped on import.
   */
  public static final UnaryOperator<Comparable> SKIP_COLUMN = t -> null;
  public static final UnaryOperator<Comparable> CONVERT_TO_MC_DATE_YEAR = t -> null;
  public static final UnaryOperator<Comparable> CONVERT_TO_MC_DATE_MONTH = t -> null;
  public static final UnaryOperator<Comparable> CONVERT_TO_MC_DATE_DAY = t -> null;

  // These Strings will convert to true booleans
  public static final List<String> TRUE_STRINGS =
      Arrays.asList("T", "t", "Y", "y", "TRUE", "true", "1");

  // These Strings will convert to true booleans
  public static final List<String> FALSE_STRINGS =
      Arrays.asList("F", "f", "N", "n", "FALSE", "false", "0");

  // Formats that we accept in parsing dates from strings
  // TODO: Add more types, especially dates with month names spelled-out fully.
  private static final DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("MM-dd-yyyy");
  private static final DateTimeFormatter dtf4 = DateTimeFormatter.ofPattern("MM.dd.yyyy");
  private static final DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter dtf6 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
  private static final DateTimeFormatter dtf7 = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
  private static final DateTimeFormatter dtf8 = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
  private static final DateTimeFormatter dtf9 = DateTimeFormatter.ofPattern("M/d/yyyy");
  private static final DateTimeFormatter dtf10 = DateTimeFormatter.ofPattern("M/d/yy");
  private static final DateTimeFormatter dtf11 = DateTimeFormatter.ofPattern("MMM/dd/yyyy");
  private static final DateTimeFormatter dtf12 = DateTimeFormatter.ofPattern("MMM-dd-yyyy");
  private static final DateTimeFormatter dtf13 = DateTimeFormatter.ofPattern("MMM/dd/yy");
  private static final DateTimeFormatter dtf14 = DateTimeFormatter.ofPattern("MMM-dd-yy");
  private static final DateTimeFormatter dtf15 = DateTimeFormatter.ofPattern("MMM/dd/yyyy");
  private static final DateTimeFormatter dtf16 = DateTimeFormatter.ofPattern("MMM/d/yyyy");
  private static final DateTimeFormatter dtf17 = DateTimeFormatter.ofPattern("MMM-dd-yy");
  private static final DateTimeFormatter dtf18 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
  private static final DateTimeFormatter dtf19 = DateTimeFormatter.ofPattern("MMM d, yyyy");

  private static final DateTimeFormatter dtTimef0 =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter dtTimef1 =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
  private static final DateTimeFormatter dtTimef2 =
      DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
  private static final DateTimeFormatter dtTimef3 =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
  private static final DateTimeFormatter dtTimef4 =
      DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
  private static final DateTimeFormatter dtTimef5 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  private static final DateTimeFormatter timef1 = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
  private static final DateTimeFormatter timef2 = DateTimeFormatter.ofPattern("hh:mm:ss a");
  private static final DateTimeFormatter timef3 = DateTimeFormatter.ofPattern("h:mm:ss a");
  private static final DateTimeFormatter timef4 = DateTimeFormatter.ISO_LOCAL_TIME;
  private static final DateTimeFormatter timef5 = DateTimeFormatter.ofPattern("hh:mm a");
  private static final DateTimeFormatter timef6 = DateTimeFormatter.ofPattern("h:mm a");
  private static final DateTimeFormatter timef7 = DateTimeFormatter.ofPattern("HHmm");


  // A formatter that handles all the date formats defined above
  public static final DateTimeFormatter DATE_FORMATTER =
      new DateTimeFormatterBuilder()
          .appendOptional(dtf1)
          .appendOptional(dtf2)
          .appendOptional(dtf2)
          .appendOptional(dtf3)
          .appendOptional(dtf4)
          .appendOptional(dtf5)
          .appendOptional(dtf6)
          .appendOptional(dtf7)
          .appendOptional(dtf8)
          .appendOptional(dtf9)
          .appendOptional(dtf10)
          .appendOptional(dtf11)
          .appendOptional(dtf12)
          .appendOptional(dtf13)
          .appendOptional(dtf14)
          .appendOptional(dtf15)
          .appendOptional(dtf16)
          .appendOptional(dtf17)
          .appendOptional(dtf18)
          .appendOptional(dtf19)
          .toFormatter();

  // A formatter that handles date time formats defined above
  public static final DateTimeFormatter dateTimeFormatter =
      new DateTimeFormatterBuilder()
          .appendOptional(dtTimef2)
          .appendOptional(dtTimef3)
          .appendOptional(dtTimef4)
          .appendOptional(dtTimef1)
          .appendOptional(dtTimef0)
          .appendOptional(dtTimef5)

          .toFormatter();

  // A formatter that handles time formats defined above
  public static final DateTimeFormatter timeFormatter =
      new DateTimeFormatterBuilder()
          .appendOptional(timef5)
          .appendOptional(timef2)
          .appendOptional(timef3)
          .appendOptional(timef1)
          .appendOptional(timef4)
          .appendOptional(timef6)
          .appendOptional(timef7)
          .toFormatter();

  /**
   * Strings representing missing values in, for example, a CSV file that is being imported
   */
  private static final String missingInd1 = "NaN";
  private static final String missingInd2 = "*";
  private static final String missingInd3 = "NA";
  private static final String missingInd4 = "null";

  public static final ImmutableList<String> MISSING_INDICATORS = ImmutableList.of(
      missingInd1,
      missingInd2,
      missingInd3,
      missingInd4
  );

  /**
   * A lambda expression that attempts to convert a variety of formats to java.time.LocalDate
   * values. The formats LocalDate, java.util.Date, and (appropriate) strings.
   * <p>
   * If the string is a long, it is assumed to be the UTC time in nanoseconds since the epoch.
   * <p>
   * Throws a java.lang.IllegalArgumentException if it gets a value it can't convert
   */
  public static final UnaryOperator<Comparable> CONVERT_TO_LOCAL_DATE = value -> {

    if (value == null) {
      return ColumnType.LOCAL_DATE.getMissingValue();
    }
    if (value instanceof LocalDate) {
      return (LocalDate) value;
    } else if (value instanceof Date) {
      Date d = ((Date) value);
      return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    } else if (value instanceof String) {

      String stringValue = (String) value;
      if (stringValue.isEmpty() || MISSING_INDICATORS.contains(stringValue)) {
        return ColumnType.LOCAL_DATE.getMissingValue();
      }
      if (stringValue.matches("-?[0-9]{0,10}")) {
        // check to see if it's in epoch days)
        return LocalDate.ofEpochDay(Long.getLong(stringValue));
      }
      return LocalDate.parse(stringValue, TypeUtils.DATE_FORMATTER);
    } else {
      throw new IllegalArgumentException("Attempting to convert unsupported date format with " +
          "value " + value + " to LocalDate");
    }
  };

  /**
   * A lambda expression that attempts to convert a variety of formats to java.time.LocalDateTime
   * values. The formats LocalDate, java.util.Date, and (appropriate) strings.
   * <p>
   * If the string is a long, it is assumed to be the UTC time in nanoseconds since the epoch.
   * <p>
   * Throws a java.lang.IllegalArgumentException if it gets a value it can't convert
   */
  public static final UnaryOperator<Comparable> CONVERT_TO_LOCAL_DATE_TIME = value -> {
    if (value == null) {
      return ColumnType.LOCAL_DATE_TIME.getMissingValue();
    }
    if (value instanceof LocalDateTime) {
      return (LocalDateTime) value;
    } else if (value instanceof ZonedDateTime) {
      ZonedDateTime d = (ZonedDateTime) value;
      return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    } else if (value instanceof String) {
      // check to see if it's empty
      String stringValue = (String) value;
      if (stringValue.isEmpty() || MISSING_INDICATORS.contains(stringValue)) {
        return ColumnType.LOCAL_DATE_TIME.getMissingValue();
      }   // check to see if its a timestamp in milliseconds (or might be)
      if (stringValue.matches("\\d+")) {
        Instant instant = Instant.ofEpochMilli(Long.parseLong(stringValue) / 1000L);
        return instant.atZone(ZoneId.of("UTC")).toLocalDate();
      }

      return LocalDateTime.parse(stringValue, TypeUtils.dateTimeFormatter);
    } else {
      throw new IllegalArgumentException("Attempting to convert unsupported date format with " +
          "value " + value + " to LocalDateTime");
    }
  };

  /**
   * A lambda expression that attempts to convert a variety of formats to java.time.LocalDateTime
   * values. The formats LocalDate, java.util.Date, and (appropriate) strings.
   * <p>
   * If the string is a long, it is assumed to be the UTC time in nanoseconds since the epoch.
   * <p>
   * Throws a java.lang.IllegalArgumentException if it gets a value it can't convert
   */
  public static final UnaryOperator<Comparable> CONVERT_TO_LOCAL_TIME = value -> {
    if (value == null) {
      return ColumnType.LOCAL_TIME.getMissingValue();
    }
    if (value instanceof Long) {
      Long longValue = (Long) value;
      if (value.equals(-1)) {
        return ColumnType.LOCAL_TIME.getMissingValue();
      } else {
        return longValue;
      }
    }
    if (value instanceof Integer) {
      Integer intValue = (Integer) value;
      if (value.equals(-1)) {
        return ColumnType.LOCAL_TIME.getMissingValue();
      } else {
        return intValue;
      }
    }
    if (value instanceof LocalTime) {
      return (LocalTime) value;
    }
    if (value instanceof String) {
      // check to see if it's empty
      String stringValue = (String) value;
      if (stringValue.isEmpty()
          || MISSING_INDICATORS.contains(stringValue)
          || stringValue.equals("-1")) {
        return ColumnType.LOCAL_TIME.getMissingValue();
      }
      if (stringValue.length() > 10 && stringValue.matches("\\d+")) {
        return Integer.valueOf(stringValue);
      }
      stringValue = Strings.padStart(stringValue, 4, '0');
      return PackedLocalTime.pack(LocalTime.parse(stringValue, TypeUtils.timeFormatter));
    } else {
      throw new IllegalArgumentException("Attempting to convert unsupported date format with " +
          "value " + value + " to LocalTime");
    }
  };

  /**
   * A lambda expression that attempts to convert a variety of formats to Double values. The
   * formats include Integers, ints, Doubles, doubles, and (appropriate) strings.
   * Throws a java.lang.IllegalArgumentException if it gets a value it can't convert
   */
  public static final UnaryOperator<Comparable> CONVERT_TO_REAL = value -> {

    if (null == value) {
      return ColumnType.FLOAT.getMissingValue();
    }
    if (value instanceof Integer) {
      Integer intValue = (Integer) value;
      return (float) intValue;
    } else if (value instanceof String) {
      String stringValue = (String) value;
      if (Strings.isNullOrEmpty(stringValue) || MISSING_INDICATORS.contains(stringValue)) {
        return null;
      }
      Number number;
      try {
        number = NumberFormat.getNumberInstance().parse(stringValue);
      } catch (ParseException e) {
        e.printStackTrace();
        throw new NumberFormatException(stringValue);
      }
      return number.floatValue();
    } else if (value instanceof Float) {
      return value;
    } else {
      throw new IllegalArgumentException("Attempting to convert non-float value " +
          value + " to Float");
    }
  };

  /**
   * Private constructor to prevent instantiation
   */
  private TypeUtils() {
  }

  /**
   * Constructs and returns a column for the given {@code name} and {@code type}
   */
  public static Column newColumn(@Nonnull String name,
                                 @Nonnull ColumnType type) {

    Preconditions.checkArgument(!Strings.isNullOrEmpty(name),
        "There must be a valid name for a new column");

    switch (type) {
      case LOCAL_DATE:
        return LocalDateColumn.create(name);
      case LOCAL_TIME:
        return LocalTimeColumn.create(name);
      case LOCAL_DATE_TIME:
        return LocalDateTimeColumn.create(name);
      case TEXT:
        return TextColumn.create(name);
      case INTEGER:
        return IntColumn.create(name);
      case FLOAT:
        return FloatColumn.create(name);
      case BOOLEAN:
        return BooleanColumn.create(name);
      case CAT:
        return CategoryColumn.create(name);
      case SKIP:
        break;
    }
    throw new IllegalArgumentException("Unknown ColumnType: " + type);
  }

  /**
   * Constructs and returns a column for the given {@code definition}
   */
/*
  public static Column newColumn(ColumnDefinition definition) {
    return newColumn(definition.name(), definition.type());
  }
*/
}

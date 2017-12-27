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

package tech.tablesaw.io;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for working with {@link ColumnType}s
 */
@Immutable
public final class TypeUtils {

    // These Strings will convert to true booleans
    public static final List<String> TRUE_STRINGS =
            Arrays.asList("T", "t", "Y", "y", "TRUE", "true", "True", "1");
    // A more restricted set of 'true' strings that is used for column type detection
    public static final List<String> TRUE_STRINGS_FOR_DETECTION =
            Arrays.asList("T", "t", "Y", "y", "TRUE", "true", "True");
    // These Strings will convert to false booleans
    public static final List<String> FALSE_STRINGS =
            Arrays.asList("F", "f", "N", "n", "FALSE", "false", "False", "0");
    // A more restricted set of 'false' strings that is used for column type detection
    public static final List<String> FALSE_STRINGS_FOR_DETECTION =
            Arrays.asList("F", "f", "N", "n", "FALSE", "false", "False");

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
    // A formatter that handles all the date formats defined above
    public static final DateTimeFormatter DATE_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendOptional(dtf1)
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
    private static final DateTimeFormatter dtTimef0 =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dtTimef1 =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter dtTimef2 =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final DateTimeFormatter dtTimef3 =
            DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
    private static final DateTimeFormatter dtTimef4 =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
    private static final DateTimeFormatter dtTimef5 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter dtTimef6;

    static {
        dtTimef6 = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .appendLiteral('.')
                .appendPattern("SSS")
                .toFormatter();
    }

    private static final DateTimeFormatter dtTimef7 =
            DateTimeFormatter.ofPattern("M/d/yy H:mm");
    private static final DateTimeFormatter dtTimef8 =
            DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

    // A formatter that handles date time formats defined above
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendOptional(dtTimef2)
                    .appendOptional(dtTimef3)
                    .appendOptional(dtTimef4)
                    .appendOptional(dtTimef1)
                    .appendOptional(dtTimef0)
                    .appendOptional(dtTimef5)
                    .appendOptional(dtTimef6)
                    .appendOptional(dtTimef7)
                    .appendOptional(dtTimef8)
                    .toFormatter();
    private static final DateTimeFormatter timef1 = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final DateTimeFormatter timef2 = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter timef3 = DateTimeFormatter.ofPattern("h:mm:ss a");
    private static final DateTimeFormatter timef4 = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final DateTimeFormatter timef5 = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter timef6 = DateTimeFormatter.ofPattern("h:mm a");
    // A formatter that handles time formats defined above used for type detection.
    // It is more conservative than the converter
    public static final DateTimeFormatter TIME_DETECTION_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendOptional(timef5)
                    .appendOptional(timef2)
                    .appendOptional(timef3)
                    .appendOptional(timef1)
                    .appendOptional(timef4)
                    .appendOptional(timef6)
                    //  .appendOptional(timef7)
                    .toFormatter();
    private static final DateTimeFormatter timef7 = DateTimeFormatter.ofPattern("HHmm");
    // A formatter that handles time formats defined above
    public static final DateTimeFormatter TIME_FORMATTER =
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
     * List of formatters for use in code that selects the correct one for a given Date string
     */
    private static ImmutableList<DateTimeFormatter> dateFormatters = ImmutableList.of(
            dtf1,
            dtf2,
            dtf3,
            dtf4,
            dtf5,
            dtf6,
            dtf7,
            dtf8,
            dtf9,
            dtf10,
            dtf11,
            dtf12,
            dtf13,
            dtf14,
            dtf15,
            dtf16,
            dtf17,
            dtf18,
            dtf19
    );
    /**
     * List of formatters for use in code that selects the correct one for a given DateTime string
     */
    private static ImmutableList<DateTimeFormatter> dateTimeFormatters = ImmutableList.of(
            dtTimef0,
            dtTimef1,
            dtTimef2,
            dtTimef3,
            dtTimef4,
            dtTimef5,
            dtTimef6
    );
    /**
     * List of formatters for use in code that selects the correct one for a given Time string
     */
    private static ImmutableList<DateTimeFormatter> timeFormatters = ImmutableList.of(
            timef1,
            timef2,
            timef3,
            timef4,
            timef5,
            timef6
            //, timef7
    );

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

        Preconditions.checkArgument(type != ColumnType.SKIP,
                "SKIP-ped columns should be handled outside of this method.");

        switch (type) {
            case LOCAL_DATE:
                return new DateColumn(name);
            case LOCAL_TIME:
                return new TimeColumn(name);
            case LOCAL_DATE_TIME:
                return new DateTimeColumn(name);
            case INTEGER:
                return new IntColumn(name);
            case FLOAT:
                return new FloatColumn(name);
            case DOUBLE:
                return new DoubleColumn(name);
            case BOOLEAN:
                return new BooleanColumn(name);
            case CATEGORY:
                return new CategoryColumn(name);
            case SHORT_INT:
                return new ShortColumn(name);
            case LONG_INT:
                return new LongColumn(name);
            default:
                throw new IllegalArgumentException("Unknown ColumnType: " + type);
        }
    }

    /**
     * Returns the first DateTimeFormatter to parse the string, which represents a DATE
     * <p>
     * It's intended to be called at the start of a large formatting job so that it picks the write format and is not
     * called again. This is an optimization, because the older version, which will try multiple formatters was too
     * slow for large data sets.
     */
    public static DateTimeFormatter getDateFormatter(String dateValue) {

        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                formatter.parse(dateValue);
                return formatter;
            } catch (DateTimeParseException e) {
                // ignore;
            }
        }
        return DATE_FORMATTER;
    }

    /**
     * Returns the first DateTimeFormatter to parse the string, which represents a DATE_TIME
     * <p>
     * It's intended to be called at the start of a large formatting job so that it picks the write format and is not
     * called again. This is an optimization, because the older version, which will try multiple formatters was too
     * slow for large data sets.
     */
    public static DateTimeConverter getDateTimeFormatter(String dateTimeValue) {
        for (DateTimeFormatter formatter : dateTimeFormatters) {
            if (canParse(formatter, dateTimeValue)) {
              return new DateTimeConverter(formatter);
            }
        }
        if (canParse(DATE_FORMATTER, dateTimeValue)) {
            return new DateTimeConverter(DATE_FORMATTER);
        }
        if (canParse(DATE_TIME_FORMATTER, dateTimeValue)) {
            return new DateTimeConverter(DATE_TIME_FORMATTER);
        }
        try {
            Long.parseLong(dateTimeValue);
            return new DateTimeConverter();
        } catch (NumberFormatException e) {
        }
        throw new IllegalArgumentException("Could not find datetime parser for " + dateTimeValue);
    }

    private static boolean canParse(DateTimeFormatter formatter, String dateTimeValue) {
      try {
        formatter.parse(dateTimeValue);
        return true;
      } catch (DateTimeParseException e) {
        return false;
      }
    }

    /**
     * Returns the first DateTimeFormatter to parse the string, which represents a TIME
     * <p>
     * It's intended to be called at the start of a large formatting job so that it picks the write format and is not
     * called again. This is an optimization, because the older version, which will try multiple formatters was too
     * slow for large data sets.
     */
    public static DateTimeFormatter getTimeFormatter(String timeValue) {
        for (DateTimeFormatter formatter : timeFormatters) {
            try {
                formatter.parse(timeValue);
                return formatter;
            } catch (DateTimeParseException e) {
                // ignore;
            }
        }
        return DATE_FORMATTER;
    }

    /**
     * Handles converting formatted strings and timestamps.
     * Assumes timestamps are milliseconds since epoch (midnight, January 1, 1970 UTC).
     * This is the timestamp format most commonly used in Java.
     * Unix uses seconds since epoch instead of Java's millis since epoch.
     * Unix timestamps are not currently supported.
     */
    public static class DateTimeConverter {
      private final boolean isTimestamp;
      private final DateTimeFormatter dtFormatter;

      public DateTimeConverter() {
        this.dtFormatter = null;
        this.isTimestamp = true;        
      }

      public DateTimeConverter(DateTimeFormatter dtFormatter) {
        this.dtFormatter = dtFormatter;
        this.isTimestamp = false;
      }

      public LocalDateTime convert(String dateTime) {
        return isTimestamp
            ? Instant.ofEpochMilli(Long.parseLong(dateTime)).atZone(ZoneOffset.UTC).toLocalDateTime()
            : LocalDateTime.parse(dateTime, dtFormatter);
      }
    }

}

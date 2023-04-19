package tech.tablesaw.columns.datetimes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;

public class DateTimeParser extends AbstractColumnParser<LocalDateTime> {

  private static final DateTimeFormatter dtTimef0 =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 2014-07-09 13:03:44
  private static final DateTimeFormatter dtTimef2 =
      DateTimeFormatter.ofPattern(
          "yyyy-MM-dd HH:mm:ss.S"); // 2014-07-09 13:03:44.7 (as above, but without leading 0 in
  // millis)
  private static final DateTimeFormatter dtTimef4 = caseInsensitiveFormatter("dd-MMM-yyyy HH:mm"); // 09-Jul-2014 13:03
  private static final DateTimeFormatter dtTimef5 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private static final DateTimeFormatter dtTimef6; // ISO, with millis appended
  private static final DateTimeFormatter dtTimef7 = //  7/9/14 9:04
      DateTimeFormatter.ofPattern("M/d/yy H:mm");
  private static final DateTimeFormatter dtTimef8 = caseInsensitiveFormatter("M/d/yyyy h:mm:ss a"); //  7/9/2014 9:04:55 PM

  /**
   * Creates a Case-insensitive formatter using the specified pattern.
   * This method will create a formatter based on a simple pattern of letters and symbols as described in the class documentation.
   * For example, d MMM yyyy will format 2011-12-03 as '3 Dec 2011'. The formatter will use the default FORMAT locale.
   * This function can handle cases like am/AM, pm/PM, Jan/JAN, Feb/FEB etc
   *
   * @param pattern the pattern to use, not null
   * @return the formatter based on the pattern, not null
   * @throws IllegalArgumentException if the pattern is invalid
   */
  public static DateTimeFormatter caseInsensitiveFormatter(String pattern) {
    return new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern).toFormatter();
  }

  static {
    dtTimef6 =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .appendLiteral('.')
            .appendPattern("SSS")
            .toFormatter();
  }

  // A formatter that handles date time formats defined above
  public static final DateTimeFormatter DEFAULT_FORMATTER =
      new DateTimeFormatterBuilder()
          .appendOptional(dtTimef7)
          .appendOptional(dtTimef8)
          .appendOptional(dtTimef2)
          .appendOptional(dtTimef4)
          .appendOptional(dtTimef0)
          .appendOptional(dtTimef5)
          .appendOptional(dtTimef6)
          .toFormatter();

  private Locale locale = Locale.getDefault();
  private DateTimeFormatter formatter = DEFAULT_FORMATTER;

  public DateTimeParser(ColumnType columnType) {
    super(columnType);
  }

  public DateTimeParser(DateTimeColumnType dateTimeColumnType, ReadOptions readOptions) {
    super(dateTimeColumnType);
    DateTimeFormatter readCsvFormatter = readOptions.dateTimeFormatter();
    if (readCsvFormatter != null) {
      formatter = readCsvFormatter;
    }
    if (readOptions.locale() != null) {
      locale = readOptions.locale();
    }
    if (readOptions.missingValueIndicators().length > 0) {
      missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicators());
    }
  }

  @Override
  public boolean canParse(String s) {
    if (isMissing(s)) {
      return true;
    }
    try {
      LocalDateTime.parse(s, formatter.withLocale(locale));
      return true;
    } catch (DateTimeParseException e) {
      // it's all part of the plan
      return false;
    }
  }

  @Override
  public LocalDateTime parse(String value) {
    if (isMissing(value)) {
      return null;
    }
    String paddedValue = Strings.padStart(value, 4, '0');
    return LocalDateTime.parse(paddedValue, formatter);
  }
}

package tech.tablesaw.columns.dates;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.datetimes.DateTimeParser;
import tech.tablesaw.io.ReadOptions;

public class DateParser extends AbstractColumnParser<LocalDate> {

  // Formats that we accept in parsing dates from strings
  private static final DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("MM-dd-yyyy");
  private static final DateTimeFormatter dtf4 = DateTimeFormatter.ofPattern("MM.dd.yyyy");
  private static final DateTimeFormatter dtf5 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter dtf6 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
  private static final DateTimeFormatter dtf7 = DateTimeParser.caseInsensitiveFormatter("dd/MMM/yyyy");
  private static final DateTimeFormatter dtf8 = DateTimeParser.caseInsensitiveFormatter("dd-MMM-yyyy");
  private static final DateTimeFormatter dtf9 = DateTimeFormatter.ofPattern("M/d/yyyy");
  private static final DateTimeFormatter dtf10 = DateTimeFormatter.ofPattern("M/d/yy");
  private static final DateTimeFormatter dtf11 = DateTimeParser.caseInsensitiveFormatter("MMM/dd/yyyy");
  private static final DateTimeFormatter dtf12 = DateTimeParser.caseInsensitiveFormatter("MMM-dd-yyyy");
  private static final DateTimeFormatter dtf13 = DateTimeParser.caseInsensitiveFormatter("MMM/dd/yy");
  private static final DateTimeFormatter dtf14 = DateTimeParser.caseInsensitiveFormatter("MMM-dd-yy");
  private static final DateTimeFormatter dtf15 = DateTimeParser.caseInsensitiveFormatter("MMM/dd/yyyy");
  private static final DateTimeFormatter dtf16 = DateTimeParser.caseInsensitiveFormatter("MMM/d/yyyy");
  private static final DateTimeFormatter dtf17 = DateTimeParser.caseInsensitiveFormatter("MMM-dd-yy");
  private static final DateTimeFormatter dtf18 = DateTimeParser.caseInsensitiveFormatter("MMM dd, yyyy");
  private static final DateTimeFormatter dtf19 = DateTimeParser.caseInsensitiveFormatter("MMM d, yyyy");

  // A formatter that handles all the date formats defined above
  public static final DateTimeFormatter DEFAULT_FORMATTER =
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

  private Locale locale = Locale.getDefault();
  private DateTimeFormatter formatter = DEFAULT_FORMATTER;

  public DateParser(ColumnType type, ReadOptions readOptions) {
    super(type);
    DateTimeFormatter readCsvFormatter = readOptions.dateFormatter();
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

  public DateParser(ColumnType type) {
    super(type);
  }

  @Override
  public boolean canParse(String s) {
    if (isMissing(s)) {
      return true;
    }
    try {
      LocalDate.parse(s, formatter.withLocale(locale));
      return true;
    } catch (DateTimeParseException e) {
      // it's all part of the plan
      return false;
    }
  }

  public void setCustomFormatter(DateTimeFormatter f) {
    formatter = f;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  @Override
  public LocalDate parse(String s) {
    if (isMissing(s)) {
      return null;
    }
    return LocalDate.parse(s, formatter);
  }
}

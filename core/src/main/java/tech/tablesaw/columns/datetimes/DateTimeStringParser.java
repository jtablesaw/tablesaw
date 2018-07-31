package tech.tablesaw.columns.datetimes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import static tech.tablesaw.io.TypeUtils.DATE_TIME_FORMATTER;

public class DateTimeStringParser extends StringParser<LocalDateTime> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DATE_TIME_FORMATTER;

    private Locale locale = Locale.getDefault();
    private DateTimeFormatter formatter = DEFAULT_FORMATTER;

    public DateTimeStringParser(ColumnType columnType) {
        super(columnType);
    }

    public DateTimeStringParser(DateTimeColumnType dateTimeColumnType, CsvReadOptions readOptions) {
        super(dateTimeColumnType);
        if (readOptions.dateFormatter() != null) {
            formatter = readOptions.dateFormatter();
        }
        if (readOptions.locale() != null) {
            locale = readOptions.locale();
        }
        if (readOptions.missingValueIndicator() != null) {
            missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
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
        value = Strings.padStart(value, 4, '0');
        return LocalDateTime.parse(value, formatter);
    }
}

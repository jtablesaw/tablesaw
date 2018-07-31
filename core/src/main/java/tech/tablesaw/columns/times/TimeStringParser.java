package tech.tablesaw.columns.times;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import static tech.tablesaw.io.TypeUtils.TIME_DETECTION_FORMATTER;

public class TimeStringParser extends StringParser<LocalTime> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = TIME_DETECTION_FORMATTER;

    private Locale locale = Locale.getDefault();
    private DateTimeFormatter formatter = DEFAULT_FORMATTER;

    public TimeStringParser(ColumnType columnType) {
        super(columnType);
    }

    public TimeStringParser(ColumnType columnType, CsvReadOptions readOptions) {
        super(columnType);
        if (readOptions.dateFormatter() != null) {
            if (readOptions.dateFormatter().getLocale() != null ) {} // TODO HERE AND OTHER TIME COLS, MAKE SURE WE DON'T REPLACE A LOCALE SET IN FORMATTER IF ANY
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
            LocalTime.parse(s, formatter.withLocale(locale));
            return true;
        } catch (DateTimeParseException e) {
            // it's all part of the plan
            return false;
        }
    }

    @Override
    public LocalTime parse(String value) {
        if (isMissing(value)) {
            return null;
        }
        value = Strings.padStart(value, 4, '0');
        return LocalTime.parse(value, formatter);
    }
}

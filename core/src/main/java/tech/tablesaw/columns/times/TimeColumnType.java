package tech.tablesaw.columns.times;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import static tech.tablesaw.io.TypeUtils.TIME_DETECTION_FORMATTER;

public class TimeColumnType extends AbstractColumnType {

    public TimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public TimeColumn create(String name) {
        return TimeColumn.create(name);
    }

    @Override
    public StringParser<LocalTime> defaultParser() {
        return new TimeStringParser(this);
    }

    @Override
    public StringParser<LocalTime> customParser(CsvReadOptions options) {
        return new TimeStringParser(this, options);
    }

    static class TimeStringParser extends StringParser<LocalTime> {

        static final DateTimeFormatter DEFAULT_FORMATTER = TIME_DETECTION_FORMATTER;

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
        public LocalTime parse(String s) {
            return null;
        }
    }
}

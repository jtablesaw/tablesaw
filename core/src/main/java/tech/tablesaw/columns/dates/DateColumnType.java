package tech.tablesaw.columns.dates;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class DateColumnType extends AbstractColumnType {

    public DateColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateColumn create(String name) {
        return DateColumn.create(name);
    }

    @Override
    public StringParser<LocalDate> defaultParser() {
        return new DateStringParser(this);
    }

    @Override
    public StringParser<LocalDate> customParser(CsvReadOptions options) {
        return new DateStringParser(this, options);
    }

    static class DateStringParser extends StringParser<LocalDate> {

        Locale locale = Locale.getDefault();
        DateTimeFormatter formatter = DEFAULT_FORMATTER;
        List<String> missingValueStrings = TypeUtils.MISSING_INDICATORS;

        public DateStringParser(ColumnType type, CsvReadOptions readOptions) {
            super(type);
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

        public DateStringParser(ColumnType type) {
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

        private boolean isMissing(String s) {
            return Strings.isNullOrEmpty(s) || missingValueStrings.contains(s);
        }

        @Override
        public LocalDate parse(String s) {
            if (missingValueStrings.contains(s)) {
                return null;
            }
            return LocalDate.parse(s, formatter);
        }

        // Formats that we accept in parsing dates from strings
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
        private static final DateTimeFormatter DEFAULT_FORMATTER =
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

    }
}

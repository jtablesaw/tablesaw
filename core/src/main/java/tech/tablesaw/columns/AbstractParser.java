package tech.tablesaw.columns;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.numbers.NumberOutOfRangeException;
import tech.tablesaw.io.TypeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * A parser for turning strings into objects that can be inserted into a column
 *
 * It serves two purposes, to determine if a string can be parsed into the desired object type, and to actually
 * parse the string.
 *
 * Implementations may take additional parameters such as a locale or DateTimeFormatter.
 *
 * @param <T>   The Class of object to be inserted: String for StringColumn, LocalDate for DateColumn, etc.
 */
public abstract class AbstractParser<T> {

    private final ColumnType columnType;

    protected List<String> missingValueStrings = TypeUtils.MISSING_INDICATORS;

    public AbstractParser(ColumnType columnType) {
        this.columnType = columnType;
    }

    public abstract boolean canParse(String s);

    public abstract T parse(String s) throws NumberOutOfRangeException;

    public ColumnType columnType() {
        return columnType;
    }

    protected boolean isMissing(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty() || missingValueStrings.contains(s);
    }

    public byte parseByte(String s) throws NumberOutOfRangeException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to bytes");
    }

    public byte parseBoolean(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to booleans");
    }

    public int parseInt(String s) throws NumberOutOfRangeException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to ints");
    }

    public short parseShort(String s) throws NumberOutOfRangeException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to shorts");
    }

    public long parseLong(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to longs");
    }

    public LocalDate parseDate(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to dates");
    }

    public String parseString(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to strings");
    }

    public LocalTime parseTime(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to times");
    }

    public LocalDateTime parseDateTime(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to dateTimes");
    }

    public double parseDouble(String s) throws NumberOutOfRangeException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to doubles");
    }

    public float parseFloat(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to floats");
    }

    protected static String remove(final String str, final char remove) {
        if (str == null || str.indexOf(remove) == -1) {
            return str;
        }
        final char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }
}

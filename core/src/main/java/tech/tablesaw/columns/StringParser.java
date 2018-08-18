package tech.tablesaw.columns;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.TypeUtils;

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
public abstract class StringParser<T> {

    private final ColumnType columnType;

    protected List<String> missingValueStrings = TypeUtils.MISSING_INDICATORS;

    public StringParser(ColumnType columnType) {
        this.columnType = columnType;
    }

    public abstract boolean canParse(String s);

    public abstract T parse(String s);

    public ColumnType columnType() {
        return columnType;
    }

    protected boolean isMissing(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty() || missingValueStrings.contains(s);
    }

    public byte parseByte(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to booleans");
    }

    public int parseInt(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to ints");
    }

    public double parseDouble(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to doubles");
    }

    public float parseFloat(String s) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " doesn't support parsing to floats");
    }
}

package tech.tablesaw.columns;

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

    public abstract boolean canParse(String s);

    public abstract T parse(String s);
}

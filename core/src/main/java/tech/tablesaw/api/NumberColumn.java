package tech.tablesaw.api;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.DoublePredicate;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

/**
 * An abstract class that provides a partial implementation for columns of numeric data
 *
 * @param <C> The column type
 * @param <T> The (boxed) type of data in the column
 */
public abstract class NumberColumn<C extends NumberColumn<C, T>, T extends Number>
    extends AbstractColumn<C, T> implements NumericColumn<T> {

  private NumberColumnFormatter printFormatter = new NumberColumnFormatter();

  protected Locale locale;

  protected final IntComparator comparator =
      (r1, r2) -> {
        final double f1 = getDouble(r1);
        final double f2 = getDouble(r2);
        return Double.compare(f1, f2);
      };

  protected NumberColumn(final ColumnType type, final String name, AbstractColumnParser<T> parser) {
    super(type, name, parser);
  }

  protected abstract C createCol(final String name, int size);

  protected abstract C createCol(final String name);

  /**
   * Updates this column where values matching the selection are replaced with the corresponding
   * value from the given column
   */
  public NumberColumn<C, T> set(DoublePredicate condition, NumberColumn<C, T> other) {
    for (int row = 0; row < size(); row++) {
      if (condition.test(getDouble(row))) {
        set(row, other.get(row));
      }
    }
    return this;
  }

  /**
   * Sets the value of all elements in this column matching condition to be equal to newValue and
   * returns this column
   */
  public NumberColumn<C, T> set(DoublePredicate condition, T newValue) {
    for (int row = 0; row < size(); row++) {
      if (condition.test(getDouble(row))) {
        set(row, newValue);
      }
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public void setPrintFormatter(NumberFormat format, String missingValueIndicator) {
    setPrintFormatter(new NumberColumnFormatter(format, missingValueIndicator));
  }

  /** {@inheritDoc} */
  @Override
  public void setPrintFormatter(final NumberColumnFormatter formatter) {
    this.printFormatter = formatter;
    formatter.setColumnType(type());
  }

  /** Returns the NumbetPrintFormatter for this column, or null */
  protected NumberColumnFormatter getPrintFormatter() {
    return printFormatter;
  }

  /**
   * Returns the largest ("top") n values in the column TODO(lwhite): Consider whether this should
   * exclude missing
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the largest observations
   */
  public abstract NumericColumn<T> top(final int n);

  /**
   * Returns the smallest ("bottom") n values in the column TODO(lwhite): Consider whether this
   * should exclude missing
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the smallest n observations
   */
  public abstract NumericColumn<T> bottom(final int n);

  /** {@inheritDoc} */
  @Override
  public String getString(final int row) {
    final double value = getDouble(row);
    if (DoubleColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(printFormatter.format(value));
  }

  /** {@inheritDoc} */
  @Override
  public C emptyCopy() {
    final C column = createCol(name());
    column.setPrintFormatter(printFormatter);
    column.locale = locale;
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public C emptyCopy(final int rowSize) {
    final C column = createCol(name(), rowSize);
    column.setPrintFormatter(printFormatter);
    column.locale = locale;
    return column;
  }

  /**
   * Compares the given ints, which refer to the indexes of the doubles in this column, according to
   * the values of the doubles themselves
   */
  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  /** {@inheritDoc} */
  @Override
  public int byteSize() {
    return type().byteSize();
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (isMissing(i)) {
        count++;
      }
    }
    return count;
  }
}

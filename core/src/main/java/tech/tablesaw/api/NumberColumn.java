package tech.tablesaw.api;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.DoublePredicate;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

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

  protected NumberColumn(final ColumnType type, final String name) {
    super(type, name);
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

  public NumberColumn<C, T> set(DoublePredicate condition, T newValue) {
    for (int row = 0; row < size(); row++) {
      if (condition.test(getDouble(row))) {
        set(row, newValue);
      }
    }
    return this;
  }

  public void setPrintFormatter(final NumberFormat format, final String missingValueString) {
    this.printFormatter = new NumberColumnFormatter(format, missingValueString);
  }

  public void setPrintFormatter(final NumberColumnFormatter formatter) {
    this.printFormatter = formatter;
  }

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

  @Override
  public String getString(final int row) {
    final double value = getDouble(row);
    if (DoubleColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(printFormatter.format(value));
  }

  @Override
  public C emptyCopy() {
    final C column = createCol(name());
    column.setPrintFormatter(printFormatter);
    column.locale = locale;
    return column;
  }

  @Override
  public C emptyCopy(final int rowSize) {
    final C column = createCol(name(), rowSize);
    column.setPrintFormatter(printFormatter);
    column.locale = locale;
    return column;
  }

  public abstract C copy();

  /**
   * Compares the given ints, which refer to the indexes of the doubles in this column, according to
   * the values of the doubles themselves
   */
  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  @Override
  public int byteSize() {
    return type().byteSize();
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public abstract byte[] asBytes(final int rowNumber);

  @Override
  public abstract C appendMissing();

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

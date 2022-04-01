package tech.tablesaw.columns.strings;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;

/** Abstract super class for Text like columns. */
public abstract class AbstractStringColumn<C extends AbstractColumn<C, String>>
    extends AbstractColumn<C, String>
    implements CategoricalColumn<String>, StringFilters, StringMapFunctions, StringReduceUtils {

  private StringColumnFormatter printFormatter = new StringColumnFormatter();

  /** Constructs a column of the given ColumnType, name, and parser */
  public AbstractStringColumn(ColumnType type, String name, AbstractColumnParser<String> parser) {
    super(type, name, parser);
  }

  public abstract BackingStringColumnType backingColumnType();

  /**
   * Sets an {@link StringColumnFormatter} which will be used to format the display of data from
   * this column when it is printed (using, for example, Table:print()) and optionally when written
   * to a text file like a CSV.
   */
  public void setPrintFormatter(StringColumnFormatter formatter) {
    Preconditions.checkNotNull(formatter);
    this.printFormatter = formatter;
  }

  /** Returns the current {@link StringColumnFormatter}. */
  public StringColumnFormatter getPrintFormatter() {
    return printFormatter;
  }

  /** {@inheritDoc} */
  @Override
  public String getString(int row) {
    return printFormatter.format(get(row));
  }

  /** {@inheritDoc} */
  @Override
  public String getUnformattedString(int row) {
    return String.valueOf(get(row));
  }

  /**
   * Returns the largest ("top") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the largest observations
   */
  public List<String> top(int n) {
    List<String> top = new ArrayList<>();
    Column<String> copy = this.copy();
    copy.sortDescending();
    for (int i = 0; i < n; i++) {
      top.add(copy.get(i));
    }
    return top;
  }

  /**
   * Returns the smallest ("bottom") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is
   *     greater than the number of observations in the column
   * @return A list, possibly empty, of the smallest n observations
   */
  public List<String> bottom(int n) {
    List<String> bottom = new ArrayList<>();
    Column<String> copy = this.copy();
    copy.sortAscending();
    for (int i = 0; i < n; i++) {
      bottom.add(copy.get(i));
    }
    return bottom;
  }

  /** {@inheritDoc} */
  @Override
  public Column<String> append(Column<String> column, int row) {
    return append(column.getUnformattedString(row));
  }

  /** {@inheritDoc} */
  @Override
  public Column<String> set(int row, Column<String> column, int sourceRow) {
    return set(row, column.getUnformattedString(sourceRow));
  }

  /** {@inheritDoc} */
  @Override
  public int byteSize() {
    return type().byteSize();
  }

  /** {@inheritDoc} */
  @Override
  public int compare(String o1, String o2) {
    return o1.compareTo(o2);
  }

  public abstract double getDouble(int i);

  /**
   * Returns a list of boolean columns suitable for use as dummy variables in, for example,
   * regression analysis, select a column of categorical data must be encoded as a list of columns,
   * such that each column represents a single category and indicates whether it is present (1) or
   * not present (0)
   *
   * @return a list of {@link BooleanColumn}
   */
  public abstract List<BooleanColumn> getDummies();

  public abstract int firstIndexOf(String value);

  public abstract int countOccurrences(String value);

  public abstract double[] asDoubleArray();

  public abstract @Nullable DictionaryMap getDictionary();
}
